package net.cyclingbits.llmsecretscanner.optimizer.llm

import io.mockk.mockk
import net.cyclingbits.llmsecretscanner.optimizer.logger.OptimizerLogger
import net.cyclingbits.llmsecretscanner.optimizer.model.LLMSuggestion
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.time.Duration

@Disabled
@EnabledIfEnvironmentVariable(named = "GOOGLE_API_KEY", matches = ".+")
class GoogleProviderTest {

    private val mockLogger = mockk<OptimizerLogger>(relaxed = true)
    private val provider = GoogleProvider(Duration.ofMinutes(5), mockLogger)

    @Test
    fun `should get suggestion from Google Gemini API`() {
        val prompt = """
            Current system prompt analysis:
            - Detection rate: 82%
            - False positive rate: 18% 
            - Main issues: Over-detection in test files, missing some cloud provider tokens
            
            Please provide optimization suggestions in this format:
            
            REASONING:
            [Detailed analysis of improvements needed]
            
            SYSTEM_PROMPT: 
            [Your optimized system prompt]
        """.trimIndent()

        val suggestion = provider.getSuggestion(prompt)

        assertNotNull(suggestion)
        assertNotNull(suggestion.newSystemPrompt)
        assertNotNull(suggestion.reasoning)
        assertTrue(suggestion.newSystemPrompt.isNotEmpty())
        assertTrue(suggestion.reasoning.isNotEmpty())
        assertTrue(suggestion.newSystemPrompt.length > 30, "System prompt should have meaningful content")
    }
    
}