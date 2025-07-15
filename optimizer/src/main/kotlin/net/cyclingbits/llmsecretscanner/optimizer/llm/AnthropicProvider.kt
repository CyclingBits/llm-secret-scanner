package net.cyclingbits.llmsecretscanner.optimizer.llm

import com.anthropic.client.AnthropicClient
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.Model
import net.cyclingbits.llmsecretscanner.optimizer.config.OptimizerConfiguration.LLMProviderType.ANTHROPIC
import net.cyclingbits.llmsecretscanner.optimizer.logger.OptimizerLogger
import java.time.Duration

class AnthropicProvider(timeout: Duration, logger: OptimizerLogger) : LLMProvider(timeout, logger) {

    override val providerType = ANTHROPIC

    private val client: AnthropicClient by lazy {
        AnthropicOkHttpClient.builder()
            .apiKey(apiKey)
            .timeout(timeout)
            .build()
    }

    override fun callLLM(prompt: String): String {
        val params = MessageCreateParams.builder()
            .maxTokens(MAX_TOKENS)
            .model(providerType.model as Model)
            .enabledThinking(THINKING_BUDGET)
            .putAdditionalHeader("beta", "interleaved-thinking-2025-05-14")
            .addUserMessage(prompt)
            .build()

        val response = client.messages().create(params)
        val firstTextBlock = response.content().firstOrNull { it.isText() }
        return firstTextBlock?.asText()?.text() ?: ""
    }

}