package net.cyclingbits.llmsecretscanner.evaluator.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class DetectionMetricsTest {

    @Test
    fun `should create detection metrics with all fields`() {
        val metrics = DetectionMetrics(
            detectionRate = 75.0,
            falsePositiveRate = 15.5
        )
        
        assertEquals(75.0, metrics.detectionRate, 0.01)
        assertEquals(15.5, metrics.falsePositiveRate, 0.01)
    }

    @Test
    fun `should handle perfect metrics`() {
        val metrics = DetectionMetrics(
            detectionRate = 100.0,
            falsePositiveRate = 0.0
        )
        
        assertEquals(100.0, metrics.detectionRate)
        assertEquals(0.0, metrics.falsePositiveRate)
    }

    @Test
    fun `should handle worst case metrics`() {
        val metrics = DetectionMetrics(
            detectionRate = 0.0,
            falsePositiveRate = 100.0
        )
        
        assertEquals(0.0, metrics.detectionRate)
        assertEquals(100.0, metrics.falsePositiveRate)
    }
}