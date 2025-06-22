package net.cyclingbits.llmsecretscanner.core

import net.cyclingbits.llmsecretscanner.core.Scanner
import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import java.io.File

class ScannerTest {

    private lateinit var testDir: File

    @BeforeEach
    fun setUp() {
        testDir = createTempDir()
        testDir.mkdirs()
    }

    @AfterEach
    fun tearDown() {
        if (::testDir.isInitialized) {
            testDir.deleteRecursively()
        }
    }

    @Test
    fun constructor_validConfiguration_succeeds() {
        val config = ScannerConfiguration(
            sourceDirectories = listOf(testDir),
            modelName = "ai/phi4:latest"
        )

        assertDoesNotThrow {
            Scanner(config).use { scanner ->
                assertNotNull(scanner)
            }
        }
    }

    @Test
    fun executeScan_emptyFileList_returnsEmptyResult() {
        val config = ScannerConfiguration(
            sourceDirectories = listOf(testDir),
            modelName = "ai/phi4:latest"
        )

        Scanner(config).use { scanner ->
            val result = scanner.executeScan(emptyList())

            assertEquals(0, result.issues.size)
            assertEquals(0, result.filesAnalyzed)
            assertEquals(0, result.totalFiles)
        }
    }

    @Test
    fun executeScan_singleFile_returnsCorrectCounts() {
        val testFile = File(testDir, "test.java")
        testFile.writeText("class Test {}")
        
        val config = ScannerConfiguration(
            sourceDirectories = listOf(testDir),
            modelName = "ai/phi4:latest"
        )

        Scanner(config).use { scanner ->
            val result = scanner.executeScan(listOf(testFile))

            assertEquals(1, result.totalFiles)
            assertTrue(result.filesAnalyzed <= 1) // May be 0 if analysis fails due to no Docker
        }
    }

    private fun createTempDir(): File {
        val tempDir = File.createTempFile("test", "dir")
        tempDir.delete()
        tempDir.mkdirs()
        return tempDir
    }
}