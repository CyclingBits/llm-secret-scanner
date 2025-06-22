package net.cyclingbits.llmsecretscanner.core.util

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.model.Issue
import net.cyclingbits.llmsecretscanner.core.util.ScanReporter
import org.junit.jupiter.api.Test
import java.io.File

class ScanReporterTest {

    @Test
    fun reportScanStart_validConfig_succeeds() {
        val tempDir = createTempDir()
        val config = ScannerConfiguration(
            sourceDirectories = listOf(tempDir),
            modelName = "ai/phi4:latest"
        )
        ScanReporter.reportScanStart(config)
        
        tempDir.deleteRecursively()
    }

    @Test
    fun reportFilesFound_positiveCount_succeeds() {
        ScanReporter.reportFilesFound(5)
    }

    @Test
    fun reportNoFilesFound_succeeds() {
        ScanReporter.reportNoFilesFound()
    }

    @Test
    fun reportScanComplete_withResults_succeeds() {
        ScanReporter.reportScanComplete(3, 8, 10, 5000)
    }

    @Test
    fun reportError_withMessage_succeeds() {
        ScanReporter.reportError("Test error message")
    }

    @Test
    fun reportFileIssues_withIssues_succeeds() {
        val tempDir = createTempDir()
        val testFile = File(tempDir, "test.java")
        testFile.writeText("class Test {}")
        
        val issues = listOf(
            Issue(
                filePath = testFile.absolutePath,
                issueNumber = 1,
                lineNumber = 1,
                issueType = "Password",
                secretValue = "test123"
            )
        )
        ScanReporter.reportFileIssues(testFile, issues, 1500, 1, 5, listOf(tempDir))
        
        tempDir.deleteRecursively()
    }

    @Test
    fun reportContainerStart_succeeds() {
        ScanReporter.reportContainerStart()
    }

    @Test
    fun reportContainerStarted_succeeds() {
        ScanReporter.reportContainerStarted()
    }

    private fun createTempDir(): File {
        val tempDir = File.createTempFile("test", "dir")
        tempDir.delete()
        tempDir.mkdirs()
        return tempDir
    }
}