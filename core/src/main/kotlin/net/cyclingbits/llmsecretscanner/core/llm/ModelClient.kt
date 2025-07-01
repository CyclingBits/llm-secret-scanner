package net.cyclingbits.llmsecretscanner.core.llm

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.logger.ScanLogger
import java.net.ConnectException
import java.time.Duration
import java.time.Instant

class ModelClient(
    private val config: ScannerConfiguration,
    private val logger: ScanLogger,
    private val containerManager: ContainerManager
) : AutoCloseable {

    private val client = lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                jackson()
            }
            install(HttpTimeout) {
                requestTimeoutMillis = config.chunkAnalysisTimeout * 1000L
                connectTimeoutMillis = config.timeout * 1000L
                socketTimeoutMillis = config.chunkAnalysisTimeout * 1000L
            }
        }
    }

    suspend fun analyze(userPrompt: String): String? {
        val requestBody = createRequestBody(userPrompt)
        val response = sendRequest(requestBody)
        return response
    }

    private fun createRequestBody(userPrompt: String): Map<String, Any> {
        val body = mutableMapOf<String, Any>(
            "model" to config.modelName,
            "messages" to createMessages(userPrompt),
            "max_tokens" to config.maxTokens,
            "temperature" to config.temperature,
            "top_p" to config.topP,
            "seed" to config.seed,
            "frequency_penalty" to config.frequencyPenalty
        )

        return body
    }

    private fun createMessages(userPrompt: String): List<Map<String, String>> {
        return listOf(
            mapOf("role" to "system", "content" to config.systemPrompt),
            mapOf("role" to "user", "content" to userPrompt)
        )
    }

    private suspend fun sendRequest(requestBody: Map<String, Any>): String? {
        val startTime = Instant.now()
        logger.reportLLMRequest(requestBody)

        val endpoint = "${containerManager.getContainer().openAIEndpoint}/v1/chat/completions"

        return try {
            val response = client.value.post(endpoint) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.bodyAsText()
            logger.reportLLMResponse(response, Duration.between(startTime, Instant.now()))
            response
        } catch (e: HttpRequestTimeoutException) {
            logger.reportLLMTimeout(config, e).let { null }
        } catch (e: ConnectException) {
            logger.reportError("Cannot connect to LLM API at $endpoint", e).let { null }
        } catch (e: Exception) {
            logger.reportError("HTTP request failed: ${e.message}", e).let { null }
        }
    }

    override fun close() {
        if (client.isInitialized()) {
            client.value.close()
        }
    }
}
