package net.cyclingbits.llmsecretscanner.optimizer.llm

import io.mockk.mockk
import net.cyclingbits.llmsecretscanner.optimizer.logger.OptimizerLogger
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.time.Duration

@Disabled
@EnabledIfEnvironmentVariable(named = "ANTHROPIC_API_KEY", matches = ".+")
class AnthropicProviderTest {

    private val mockLogger = mockk<OptimizerLogger>(relaxed = true)
    private val provider = AnthropicProvider(Duration.ofMinutes(5), mockLogger)

    @Test
    fun `should get suggestion from Anthropic API`() {
        val prompt = """
            Analyze this system prompt performance:
            - Detection rate: 78%
            - False positive rate: 22%
            - Common issues: Missing modern token formats, too many test file detections
            
            Please provide improvements in this format:
            
            REASONING:
            [Your detailed analysis]
            
            SYSTEM_PROMPT:
            [Your improved prompt]
        """.trimIndent()

        val suggestion = provider.getSuggestion(prompt)

        assertNotNull(suggestion)
        assertNotNull(suggestion.newSystemPrompt)
        assertNotNull(suggestion.reasoning)
        assertTrue(suggestion.newSystemPrompt.isNotEmpty())
        assertTrue(suggestion.reasoning.isNotEmpty())
        assertTrue(suggestion.newSystemPrompt.length > 50, "System prompt should be substantial")
    }

}