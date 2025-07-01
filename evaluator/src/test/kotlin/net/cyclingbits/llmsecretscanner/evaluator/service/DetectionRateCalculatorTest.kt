package net.cyclingbits.llmsecretscanner.evaluator.service

import net.cyclingbits.llmsecretscanner.core.model.FileScanResult
import net.cyclingbits.llmsecretscanner.core.model.Issue
import net.cyclingbits.llmsecretscanner.evaluator.logger.EvaluatorLogger
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.File

class DetectionRateCalculatorTest {

    private val logger = mockk<EvaluatorLogger>(relaxed = true)
    private val calculator = DetectionRateCalculator(logger)

    @Test
    fun `should calculate perfect detection rate`() {
        val detectedIssues = listOf(
            FileScanResult(File("test.kt"), listOf(
                Issue(10, "API_KEY", "sk-123"),
                Issue(20, "PASSWORD", "admin123")
            ))
        )
        
        val expectedIssues = listOf(
            FileScanResult(File("test.kt"), listOf(
                Issue(10, "API_KEY", "sk-123"),
                Issue(20, "PASSWORD", "admin123")
            ))
        )
        
        val negativeResults = emptyList<FileScanResult>()
        
        val metrics = calculator.calculate(detectedIssues, expectedIssues, negativeResults)
        
        assertEquals(100.0, metrics.detectionRate, 0.01)
        assertEquals(100.0, metrics.falsePositiveRate, 0.01)
    }

    @Test
    fun `should calculate partial detection rate`() {
        val detectedIssues = listOf(
            FileScanResult(File("test.kt"), listOf(
                Issue(10, "API_KEY", "sk-123")
            ))
        )
        
        val expectedIssues = listOf(
            FileScanResult(File("test.kt"), listOf(
                Issue(10, "API_KEY", "sk-123"),
                Issue(20, "PASSWORD", "admin123")
            ))
        )
        
        val negativeResults = emptyList<FileScanResult>()
        
        val metrics = calculator.calculate(detectedIssues, expectedIssues, negativeResults)
        
        assertEquals(50.0, metrics.detectionRate, 0.01)
        assertEquals(100.0, metrics.falsePositiveRate, 0.01)
    }

    @Test
    fun `should calculate false positive rate`() {
        val detectedIssues = emptyList<FileScanResult>()
        val expectedIssues = emptyList<FileScanResult>()
        
        val negativeResults = listOf(
            FileScanResult(File("clean.kt"), listOf(
                Issue(5, "FALSE_POSITIVE", "not-a-secret")
            ))
        )
        
        val metrics = calculator.calculate(detectedIssues, expectedIssues, negativeResults)
        
        assertEquals(0.0, metrics.detectionRate)
        assertEquals(100.0, metrics.falsePositiveRate, 0.01)
    }

    @Test
    fun `should handle empty results`() {
        val detectedIssues = emptyList<FileScanResult>()
        val expectedIssues = emptyList<FileScanResult>()
        val negativeResults = emptyList<FileScanResult>()
        
        val metrics = calculator.calculate(detectedIssues, expectedIssues, negativeResults)
        
        assertEquals(0.0, metrics.detectionRate)
        assertEquals(100.0, metrics.falsePositiveRate)
    }

    @Test
    fun `should handle mixed results`() {
        val detectedIssues = listOf(
            FileScanResult(File("test.kt"), listOf(
                Issue(10, "API_KEY", "sk-123"),
                Issue(15, "WRONG", "not-expected")
            ))
        )
        
        val expectedIssues = listOf(
            FileScanResult(File("test.kt"), listOf(
                Issue(10, "API_KEY", "sk-123"),
                Issue(20, "PASSWORD", "admin123")
            ))
        )
        
        val negativeResults = listOf(
            FileScanResult(File("clean.kt"), listOf(
                Issue(5, "FALSE_POSITIVE", "innocent-code")
            ))
        )
        
        val metrics = calculator.calculate(detectedIssues, expectedIssues, negativeResults)
        
        assertEquals(50.0, metrics.detectionRate, 0.01)
        assertTrue(metrics.falsePositiveRate > 0.0)
    }
}