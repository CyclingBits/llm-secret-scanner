package net.cyclingbits.llmsecretscanner.optimizer.llm

import com.google.genai.Client
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.ThinkingConfig
import net.cyclingbits.llmsecretscanner.optimizer.config.OptimizerConfiguration.LLMProviderType
import net.cyclingbits.llmsecretscanner.optimizer.config.OptimizerConfiguration.LLMProviderType.GOOGLE
import net.cyclingbits.llmsecretscanner.optimizer.logger.OptimizerLogger
import java.time.Duration

class GoogleProvider(timeout: Duration, logger: OptimizerLogger) : LLMProvider(timeout, logger) {

    override val providerType = GOOGLE

    private val client by lazy {
        Client.builder()
            .apiKey(apiKey)
            .build()
    }

    override fun callLLM(prompt: String): String {
        val config = GenerateContentConfig.builder()
            .temperature(DEFAULT_TEMPERATURE)
            .maxOutputTokens(MAX_TOKENS.toInt())
            .thinkingConfig(
                ThinkingConfig.builder()
                    .thinkingBudget(THINKING_BUDGET.toInt())
                    .includeThoughts(true)
                    .build()
            )
            .build()

        val response = client.models.generateContent(providerType.modelName, prompt, config)
        return response.text() ?: ""
    }

}