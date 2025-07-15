package net.cyclingbits.llmsecretscanner.optimizer.llm

import net.cyclingbits.llmsecretscanner.optimizer.config.OptimizerConfiguration.LLMProviderType
import net.cyclingbits.llmsecretscanner.optimizer.logger.OptimizerLogger
import net.cyclingbits.llmsecretscanner.optimizer.model.LLMSuggestion
import java.time.Duration

abstract class LLMProvider(
    protected val timeout: Duration,
    protected val logger: OptimizerLogger
) {
    abstract val providerType: LLMProviderType

    companion object {
        const val MAX_TOKENS = 64_000L
        const val THINKING_BUDGET = 32_000L
        const val DEFAULT_TEMPERATURE = 0.0f
    }

    protected val apiKey: String by lazy {
        providerType.apiKeyEnvVar?.let { getApiKey(it) } ?: ""
    }

    fun getSuggestion(prompt: String): LLMSuggestion {
        logger.reportLLMRequest(providerType, prompt)
        val response = callLLM(prompt)
        logger.reportLLMResponse(providerType, response)
        return parseResponse(response)
    }

    fun getRawResponse(prompt: String): String {
        logger.reportLLMRequest(providerType, prompt)
        val response = callLLM(prompt)
        logger.reportLLMResponse(providerType, response)
        return response
    }

    protected abstract fun callLLM(prompt: String): String

    protected fun getApiKey(envVarName: String): String =
        System.getenv(envVarName) ?: throw IllegalArgumentException("$envVarName environment variable is required")

    fun parseResponse(response: String): LLMSuggestion {
        val lines = response.split("\n")
//        val reasoningStart = lines.indexOfFirst { it.contains("REASONING:") }
        val promptStart = lines.indexOfFirst { it.contains("UPDATED_SYSTEM_PROMPT:") }

//        if (reasoningStart < 0 || promptStart < 0) {
        if (promptStart < 0) {
            throw IllegalArgumentException("Invalid LLM response format: missing UPDATED_SYSTEM_PROMPT: sections")
        }

//        val valreasoning = lines.drop(reasoningStart + 1).takeWhile { !it.contains("SYSTEM_PROMPT:") }.joinToString("\n").trim()
//        val reasoning = lines.takeWhile { !it.contains("SYSTEM_PROMPT:") }.joinToString("\n").trim()
        val newPrompt = lines.drop(promptStart + 1).joinToString("\n").trim()

//        if (reasoning.isBlank()) {
//            throw IllegalArgumentException("Invalid LLM response: REASONING section is empty")
//        }

        if (newPrompt.isBlank()) {
            throw IllegalArgumentException("Invalid LLM response: UPDATED_SYSTEM_PROMPT section is empty")
        }

//        return LLMSuggestion(newPrompt, reasoning)
        return LLMSuggestion(newPrompt, "")
    }
}