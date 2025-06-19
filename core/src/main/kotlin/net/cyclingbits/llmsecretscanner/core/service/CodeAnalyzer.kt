package net.cyclingbits.llmsecretscanner.core.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.exception.AnalysisException
import net.cyclingbits.llmsecretscanner.core.exception.HttpException
import net.cyclingbits.llmsecretscanner.core.exception.JsonParserException
import net.cyclingbits.llmsecretscanner.core.exception.TimeoutException
import net.cyclingbits.llmsecretscanner.core.model.Issue
import org.testcontainers.containers.DockerModelRunnerContainer
import java.io.File
import java.io.FileNotFoundException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class CodeAnalyzer(
    private val config: ScannerConfiguration,
    private val container: DockerModelRunnerContainer
) {

    private val objectMapper = ObjectMapper()

    fun analyzeFile(file: File, fileIndex: Int, totalFiles: Int): List<Issue> {
        ScanReporter.reportFileAnalysisStart(file, fileIndex, totalFiles, config.sourceDirectory)
        
        val startTime = System.currentTimeMillis()

        val userPrompt = createUserPrompt(file)
        val systemPrompt = config.systemPrompt ?: readSystemPrompt()

        val jsonResponse = analyzeCode(systemPrompt, userPrompt)
        val issues = AnalysisResultMapper.parseAnalysisResult(jsonResponse)

        val analysisTime = System.currentTimeMillis() - startTime
        ScanReporter.reportFileIssues(file, issues, analysisTime, fileIndex, totalFiles, config.sourceDirectory)

        return issues
    }

    private fun createUserPrompt(file: File): String {
        if (file.length() > config.maxFileSizeBytes) {
            throw AnalysisException("File ${file.path} is too large (${file.length()} bytes). Maximum size is ${config.maxFileSizeBytes} bytes.")
        }

        val fileContent = file.readText()
        val numberedLines = fileContent.lines().mapIndexed { index, line ->
            "${(index + 1).toString().padStart(3, ' ')}: $line"
        }.joinToString("\n")

        return "Analyze the following code from file '${file.path}':\n```\n$numberedLines\n```"
    }

    private fun readSystemPrompt(): String {
        return this::class.java.classLoader.getResourceAsStream("system_prompt.txt")?.use { stream ->
            stream.bufferedReader().use { it.readText() }
        } ?: throw FileNotFoundException("system_prompt.txt not found in resources")
    }

    private fun analyzeCode(systemPrompt: String, userPrompt: String): String {
        val requestBody = createRequestBody(systemPrompt, userPrompt)
        val response = sendRequest(requestBody)
        return parseResponse(response)
    }

    private fun createRequestBody(systemPrompt: String, userPrompt: String): String {
        val requestMap = mapOf(
            "model" to config.modelName,
            "messages" to createMessages(systemPrompt, userPrompt),
            "max_tokens" to config.maxTokens,
            "temperature" to config.temperature
        )
        return objectMapper.writeValueAsString(requestMap)
    }

    private fun createMessages(systemPrompt: String, userPrompt: String): List<Map<String, String>> {
        return listOf(
            mapOf("role" to "system", "content" to systemPrompt),
            mapOf("role" to "user", "content" to userPrompt)
        )
    }

    private fun sendRequest(jsonBody: String): String {
        val endpoint = "${container.openAIEndpoint}/v1/chat/completions"

        val (_, _, result) = Fuel.post(endpoint)
            .header("Content-Type", "application/json")
            .jsonBody(jsonBody)
            .timeout(config.timeout * 1000)
            .timeoutRead(config.fileAnalysisTimeout * 1000)
            .responseString()

        return result.fold(
            success = { it },
            failure = { error ->
                when (error.cause?.cause ?: error.cause) {
                    is SocketTimeoutException -> throw TimeoutException("Model API timeout after ${config.fileAnalysisTimeout}s", error)
                    is ConnectException -> throw HttpException("Cannot connect to LLM API at $endpoint", error)
                    is UnknownHostException -> throw HttpException("Unknown host: $endpoint", error)
                    else -> throw HttpException("HTTP request failed: ${error.message}", error)
                }
            }
        )
    }

    private fun parseResponse(responseBody: String): String {
        try {
            val responseMap = objectMapper.readValue(responseBody, Map::class.java)
            val content = extractContent(responseMap)
            return content ?: throw JsonParserException("Invalid response format - no content found")
        } catch (e: Exception) {
            throw JsonParserException("Failed to parse JSON response", e)
        }
    }

    private fun extractContent(responseMap: Map<*, *>): String? {
        val choices = responseMap["choices"] as? List<*>
        val firstChoice = choices?.firstOrNull() as? Map<*, *>
        val message = firstChoice?.get("message") as? Map<*, *>
        return message?.get("content") as? String
    }
}