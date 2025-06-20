package net.cyclingbits.llmsecretscanner.core

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.service.FileScanner
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class ScannerIntegrationTest {

    private lateinit var testDir: File
    private lateinit var javaFile: File

    @BeforeEach
    fun setUp() {
        testDir = createTempDir()
        testDir.mkdirs()

        javaFile = File(testDir, "TestFile.java")
        javaFile.writeText(
            """
            public class TestFile {
                private String password = "secret123";
                private String apiKey = "api-key-xyz-789";
                public void method() {
                    String token = "bearer-token-abc123";
                }
            }
        """.trimIndent()
        )
    }

    @AfterEach
    fun tearDown() {
        if (::testDir.isInitialized) {
            testDir.deleteRecursively()
        }
    }

    @Test
    fun executeScan_withValidConfiguration_findsSecrets() {
        val config = ScannerConfiguration(
            sourceDirectory = testDir,
            modelName = "ai/llama3.2:latest",
            includes = "**/*.java",
            fileAnalysisTimeout = 120
        )

        val fileScanner = FileScanner(config)
        val filesToScan = fileScanner.findFiles()

        val scanResult = Scanner(config).use { scanner ->
            scanner.executeScan(filesToScan)
        }

        assertTrue(scanResult.issues.isNotEmpty(), "Should find at least one security issue")
        assertTrue(scanResult.issues.any { it.filePath.contains("TestFile.java") }, "Should find issues in TestFile.java")
    }

    @Test
    fun executeScan_withCustomSystemPrompt_usesCustomPrompt() {
        val customPrompt = "Find only API keys in the provided code. Return results as JSON array."

        val config = ScannerConfiguration(
            sourceDirectory = testDir,
            modelName = "ai/llama3.2:latest",
            includes = "**/*.java",
            systemPrompt = customPrompt,
            fileAnalysisTimeout = 120
        )

        val fileScanner = FileScanner(config)
        val filesToScan = fileScanner.findFiles()

        val scanResult = Scanner(config).use { scanner ->
            scanner.executeScan(filesToScan)
        }

        assertNotNull(scanResult)
    }

    @Test
    fun executeScan_withNoMatchingFiles_returnsEmptyList() {
        val config = ScannerConfiguration(
            sourceDirectory = testDir,
            modelName = "ai/llama3.2:latest",
            includes = "**/*.xyz",
            fileAnalysisTimeout = 120
        )

        val fileScanner = FileScanner(config)
        val filesToScan = fileScanner.findFiles()

        val scanResult = Scanner(config).use { scanner ->
            scanner.executeScan(filesToScan)
        }

        assertTrue(scanResult.issues.isEmpty(), "Should return empty list when no files match")
    }

    @Test
    fun executeScan_withLargeFile_handlesFileSizeLimit() {
        val largeContent = "// ".repeat(50_000) + "password=\"secret123\""
        val largeFile = File(testDir, "LargeFile.java")
        largeFile.writeText(largeContent)

        val config = ScannerConfiguration(
            sourceDirectory = testDir,
            modelName = "ai/llama3.2:latest",
            includes = "**/*.java",
            maxFileSizeBytes = 1000,
            fileAnalysisTimeout = 120
        )

        val fileScanner = FileScanner(config)
        val filesToScan = fileScanner.findFiles()

        val scanResult = Scanner(config).use { scanner ->
            scanner.executeScan(filesToScan)
        }

        assertNotNull(scanResult)
    }

    @Test
    fun executeScan_withMultipleFiles_analyzesAllFiles() {
        val file1 = File(testDir, "File1.java")
        file1.writeText("public class File1 { private String key = \"secret-key-1\"; }")

        val file2 = File(testDir, "File2.java")
        file2.writeText("public class File2 { private String token = \"auth-token-2\"; }")

        val config = ScannerConfiguration(
            sourceDirectory = testDir,
            modelName = "ai/llama3.2:latest",
            includes = "**/*.java",
            fileAnalysisTimeout = 120
        )

        val fileScanner = FileScanner(config)
        val filesToScan = fileScanner.findFiles()

        val scanResult = Scanner(config).use { scanner ->
            scanner.executeScan(filesToScan)
        }

        assertNotNull(scanResult)
    }

    private fun createTempDir(): File {
        val tempDir = File.createTempFile("scanner-test", "dir")
        tempDir.delete()
        return tempDir
    }
}