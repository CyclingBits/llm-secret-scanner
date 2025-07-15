package net.cyclingbits.llmsecretscanner.optimizer.llm

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import net.cyclingbits.claudecode.api.ClaudeCodeClient
import net.cyclingbits.claudecode.api.assistantMessages
import net.cyclingbits.claudecode.types.ClaudeCodeOptions
import net.cyclingbits.llmsecretscanner.optimizer.config.OptimizerConfiguration.LLMProviderType
import net.cyclingbits.llmsecretscanner.optimizer.logger.OptimizerLogger
import java.time.Duration

class ClaudeCodeProvider(
    timeout: Duration,
    logger: OptimizerLogger
) : LLMProvider(timeout, logger) {

    override val providerType = LLMProviderType.CLAUDE_CODE

    private val client = ClaudeCodeClient()

    override fun callLLM(prompt: String): String = runBlocking {
        try {
            withTimeout(timeout.toMillis()) {
                val options = ClaudeCodeOptions(
                    continueConversation = false,
                    timeoutMs = timeout.toMillis(),
                    maxTurns = 20
                )

                val response = client
                    .query(prompt, options)
                    .assistantMessages()
                    .toList()
                    .joinToString("\n\n") { it.text }
                    .trim()

                response
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Claude Code request failed: ${e.message}", e)
        }
    }
}