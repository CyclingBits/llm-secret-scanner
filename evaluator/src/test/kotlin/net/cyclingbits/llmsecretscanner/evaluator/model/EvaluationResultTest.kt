package net.cyclingbits.llmsecretscanner.evaluator.model

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class EvaluationResultTest {

    private val testConfig = mockk<ScannerConfiguration>()

    @Test
    fun `should create evaluation result with all fields`() {
        val result = EvaluationResult(
            modelName = "ai/phi4:latest",
            detectionRate = 85.5,
            falsePositiveRate = 12.3,
            scanTime = 45000,
            config = testConfig
        )
        
        assertEquals("ai/phi4:latest", result.modelName)
        assertEquals(85.5, result.detectionRate, 0.01)
        assertEquals(12.3, result.falsePositiveRate, 0.01)
        assertEquals(45000, result.scanTime)
    }

    @Test
    fun `should handle zero rates`() {
        val result = EvaluationResult(
            modelName = "ai/test:latest",
            detectionRate = 0.0,
            falsePositiveRate = 0.0,
            scanTime = 1000,
            config = testConfig
        )
        
        assertEquals(0.0, result.detectionRate)
        assertEquals(0.0, result.falsePositiveRate)
    }

    @Test
    fun `should handle perfect detection`() {
        val result = EvaluationResult(
            modelName = "ai/perfect:latest",
            detectionRate = 100.0,
            falsePositiveRate = 0.0,
            scanTime = 30000,
            config = testConfig
        )
        
        assertEquals(100.0, result.detectionRate)
        assertEquals(0.0, result.falsePositiveRate)
    }
}