package net.cyclingbits.llmsecretscanner.optimizer.llm

import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.ChatModel
import com.openai.models.ReasoningEffort
import com.openai.models.chat.completions.ChatCompletionCreateParams
import net.cyclingbits.llmsecretscanner.optimizer.config.OptimizerConfiguration.LLMProviderType.OPENAI
import net.cyclingbits.llmsecretscanner.optimizer.logger.OptimizerLogger
import java.time.Duration

class OpenAIProvider(timeout: Duration, logger: OptimizerLogger) : LLMProvider(timeout, logger) {

    override val providerType = OPENAI

    private val client by lazy {
        OpenAIOkHttpClient.builder()
            .apiKey(apiKey)
            .timeout(timeout)
            .build()
    }

    override fun callLLM(prompt: String): String {
        val completion = client.chat().completions().create(
            ChatCompletionCreateParams.builder()
                .model(providerType.model as ChatModel)
                .reasoningEffort(ReasoningEffort.HIGH)
                .addUserMessage(prompt)
                .build()
        )

        return completion.choices().first().message().content().orElse("")
    }

}