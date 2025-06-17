package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.model.Issue
import org.junit.jupiter.api.Test
import java.io.File

class ScanReporterTest {

    @Test
    fun reportScanStart_validConfig_succeeds() {
        val tempDir = createTempDir()
        val config = ScannerConfiguration(
            sourceDirectory = tempDir,
            modelName = "ai/phi4:latest"
        )
        val reporter = ScanReporter()
        
        reporter.reportScanStart(config)
        
        tempDir.deleteRecursively()
    }

    @Test
    fun reportFilesFound_positiveCount_succeeds() {
        val reporter = ScanReporter()
        
        reporter.reportFilesFound(5)
    }

    @Test
    fun reportNoFilesFound_succeeds() {
        val reporter = ScanReporter()
        
        reporter.reportNoFilesFound()
    }

    @Test
    fun reportScanComplete_withResults_succeeds() {
        val reporter = ScanReporter()
        
        reporter.reportScanComplete(3, 5000)
    }

    @Test
    fun reportError_withMessage_succeeds() {
        val reporter = ScanReporter()
        
        reporter.reportError("Test error message")
    }

    @Test
    fun reportFileIssues_withIssues_succeeds() {
        val tempDir = createTempDir()
        val testFile = File(tempDir, "test.java")
        testFile.writeText("class Test {}")
        
        val issues = listOf(
            Issue(
                filePath = testFile.absolutePath,
                lineNumber = 1,
                issueType = "Password",
                description = "Test issue"
            )
        )
        val reporter = ScanReporter()
        
        reporter.reportFileIssues(testFile, issues, 1500, 1, 5, tempDir)
        
        tempDir.deleteRecursively()
    }

    @Test
    fun reportContainerStart_succeeds() {
        val reporter = ScanReporter()
        
        reporter.reportContainerStart()
    }

    @Test
    fun reportContainerStarted_succeeds() {
        val reporter = ScanReporter()
        
        reporter.reportContainerStarted()
    }

    private fun createTempDir(): File {
        val tempDir = File.createTempFile("test", "dir")
        tempDir.delete()
        tempDir.mkdirs()
        return tempDir
    }
}