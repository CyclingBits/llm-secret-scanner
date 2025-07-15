package net.cyclingbits.llmsecretscanner.optimizer.config

import com.anthropic.models.messages.Model
import com.openai.models.ChatModel
import net.cyclingbits.llmsecretscanner.events.EventSource
import net.cyclingbits.llmsecretscanner.events.LogLevel
import net.cyclingbits.llmsecretscanner.optimizer.llm.*
import net.cyclingbits.llmsecretscanner.optimizer.logger.OptimizerLogger
import java.io.File
import java.time.Duration

data class OptimizerConfiguration(
    val maxIterations: Int = 10,
    val llmProvider: LLMProviderType = LLMProviderType.ANTHROPIC,
    val fileType: FileType = FileType.PROPERTIES,
    val timeout: Duration = Duration.ofMinutes(10),
    val outputDir: File = File("optimizer/src/main/resources/optimization_results"),
    val logSources: Set<EventSource> = setOf(EventSource.OPTIMIZER, EventSource.EVALUATOR),
    val minLogLevel: LogLevel = LogLevel.INFO
) {
    enum class FileType(val includePattern: String) {
        JAVA("**/*.java"),
        KOTLIN("**/*.kt"),
        PROPERTIES("**/*.properties"),
        XML("**/*.xml"),
        YML("**/*.yml")
    }

    enum class LLMProviderType(val model: Any, val modelName: String, val apiKeyEnvVar: String?) {
        OPENAI(ChatModel.O4_MINI, "o4-mini", "OPENAI_API_KEY"),
        ANTHROPIC(Model.CLAUDE_SONNET_4_0, "claude-sonnet-4.0", "ANTHROPIC_API_KEY"),
        GOOGLE("gemini-2.5-pro", "gemini-2.5-pro", "GOOGLE_API_KEY"),
        CLAUDE_CODE("", "", null);
        
        fun createProvider(config: OptimizerConfiguration, logger: OptimizerLogger): LLMProvider = when (this) {
            OPENAI -> OpenAIProvider(config.timeout, logger)
            ANTHROPIC -> AnthropicProvider(config.timeout, logger)
            GOOGLE -> GoogleProvider(config.timeout, logger)
            CLAUDE_CODE -> ClaudeCodeProvider(config.timeout, logger)
        }
    }
}