package net.cyclingbits.llmsecretscanner.evaluator.logger

import net.cyclingbits.llmsecretscanner.core.logger.EventStore
import net.cyclingbits.llmsecretscanner.core.model.FileScanResult
import net.cyclingbits.llmsecretscanner.core.model.Issue
import net.cyclingbits.llmsecretscanner.evaluator.model.DetectionResults
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.io.File

class EvaluatorLoggerTest {

    private val logger = EvaluatorLogger()

    @BeforeEach
    fun setUp() {
        EventStore.clear()
    }

    @Test
    fun `should log evaluation start`() {
        logger.reportEvaluationStart()
        
        val events = EventStore.getAll()
        assertEquals(1, events.size)
        assertTrue(events[0].messages.any { it.contains("Starting model evaluation") })
    }

    @Test
    fun `should log evaluation complete with time`() {
        val totalTimeSeconds = 125L
        
        logger.reportEvaluationComplete(totalTimeSeconds)
        
        val events = EventStore.getAll()
        assertEquals(1, events.size)
        assertTrue(events[0].messages.any { it.contains("Evaluation completed successfully") })
        assertTrue(events[0].messages.any { it.contains("2m 5s") })
    }

    @Test
    fun `should log file saved`() {
        val filePath = "/path/to/results.md"
        
        logger.reportFileSaved(filePath)
        
        val events = EventStore.getAll()
        assertEquals(1, events.size)
        assertTrue(events[0].messages.any { it.contains("Results saved to") })
        assertTrue(events[0].messages.any { it.contains(filePath) })
    }

    @Test
    fun `should log expected issues loaded`() {
        val totalIssues = 25
        val fileCount = 5
        
        logger.reportExpectedIssuesLoaded(totalIssues, fileCount)
        
        val events = EventStore.getAll()
        assertEquals(1, events.size)
        assertTrue(events[0].messages.any { it.contains("Loaded") && it.contains("25") && it.contains("5") })
    }

    @Test
    fun `should log detection results`() {
        val detectionResults = DetectionResults(
            detectedIssues = listOf(
                FileScanResult(File("test.kt"), listOf(
                    Issue(10, "API_KEY", "sk-123"),
                    Issue(20, "PASSWORD", "admin")
                ))
            ),
            expectedIssues = listOf(
                FileScanResult(File("test.kt"), listOf(
                    Issue(10, "API_KEY", "sk-123"),
                    Issue(20, "PASSWORD", "admin"),
                    Issue(30, "TOKEN", "token123")
                ))
            ),
            correctIssues = listOf(
                Issue(10, "API_KEY", "sk-123"),
                Issue(20, "PASSWORD", "admin")
            ),
            incorrectIssues = listOf(
                Issue(30, "TOKEN", "token123")
            ),
            falsePositiveIssues = emptyList()
        )
        
        logger.reportDetectionResults(detectionResults)
        
        val events = EventStore.getAll()
        assertEquals(1, events.size)
        assertTrue(events[0].messages.any { it.contains("Detection analysis") })
    }

    @Test
    fun `should format time correctly for short duration`() {
        val shortTimeSeconds = 45L
        
        logger.reportEvaluationComplete(shortTimeSeconds)
        
        val events = EventStore.getAll()
        assertTrue(events[0].messages.any { it.contains("45s") })
    }

    @Test
    fun `should format time correctly for long duration`() {
        val longTimeSeconds = 3665L // 1h 1m 5s
        
        logger.reportEvaluationComplete(longTimeSeconds)
        
        val events = EventStore.getAll()
        assertTrue(events[0].messages.any { it.contains("61m 5s") })
    }
}