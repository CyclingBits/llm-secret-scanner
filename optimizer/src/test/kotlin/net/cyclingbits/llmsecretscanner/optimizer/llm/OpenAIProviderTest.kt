package net.cyclingbits.llmsecretscanner.optimizer.llm

import io.mockk.mockk
import net.cyclingbits.llmsecretscanner.optimizer.logger.OptimizerLogger
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.time.Duration

@Disabled
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class OpenAIProviderTest {

    private val mockLogger = mockk<OptimizerLogger>(relaxed = true)
    private val provider = OpenAIProvider(Duration.ofMinutes(5), mockLogger)

    @Test
    fun `should get suggestion from OpenAI API`() {
        val prompt = """
            Current system prompt performance:
            - Detection rate: 85%
            - False positive rate: 15%
            
            Please suggest improvements to the system prompt.
            
            REASONING:
            [Your analysis here]
            
            SYSTEM_PROMPT:
            [Improved prompt here]
        """.trimIndent()

        val suggestion = provider.getSuggestion(prompt)

        assertNotNull(suggestion)
        assertNotNull(suggestion.newSystemPrompt)
        assertNotNull(suggestion.reasoning)
        assertTrue(suggestion.newSystemPrompt.isNotEmpty())
        assertTrue(suggestion.reasoning.isNotEmpty())
    }
}