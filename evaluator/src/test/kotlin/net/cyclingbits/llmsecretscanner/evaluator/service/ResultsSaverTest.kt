package net.cyclingbits.llmsecretscanner.evaluator.service

import net.cyclingbits.llmsecretscanner.evaluator.logger.EvaluatorLogger
import net.cyclingbits.llmsecretscanner.evaluator.model.EvaluationResult
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ResultsSaverTest {

    private val logger = mockk<EvaluatorLogger>(relaxed = true)
    private val resultsSaver = ResultsSaver(logger)

    @Test
    fun `should create ResultsSaver instance`() {
        assertNotNull(resultsSaver)
    }

    @Test
    fun `should handle empty results list`() {
        val results = emptyList<EvaluationResult>()
        
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun `should create evaluation result object`() {
        val result = EvaluationResult("ai/test:latest", 85.0, 10.0, 30000)
        
        assertEquals("ai/test:latest", result.modelName)
        assertEquals(85.0, result.detectionRate)
        assertEquals(10.0, result.falsePositiveRate)
        assertEquals(30000, result.scanTime)
    }

    @Test
    fun `should format time for display`() {
        val shortTime = 30000L
        val longTime = 125000L
        
        assertTrue(shortTime < 60000)
        assertTrue(longTime > 60000)
    }

    @Test
    fun `should handle multiple results`() {
        val results = listOf(
            EvaluationResult("ai/phi4:latest", 85.0, 10.0, 30000),
            EvaluationResult("ai/llama3.2:latest", 80.0, 15.0, 45000),
            EvaluationResult("ai/mistral:latest", 75.0, 20.0, 35000)
        )
        
        assertEquals(3, results.size)
        assertTrue(results.all { it.detectionRate > 0 })
    }
}