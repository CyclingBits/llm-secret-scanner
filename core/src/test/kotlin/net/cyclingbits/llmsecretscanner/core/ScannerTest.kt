package net.cyclingbits.llmsecretscanner.core

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.File

class ScannerTest {

    @Test
    fun constructor_validConfig_succeeds() {
        val tempDir = createTempDir()
        val config = ScannerConfiguration(
            sourceDirectory = tempDir,
            modelName = "ai/phi4:latest"
        )
        
        val scanner = Scanner(config)
        
        assertNotNull(scanner)
        
        tempDir.deleteRecursively()
    }

    @Test
    fun executeScan_emptyFilesList_returnsEmptyList() {
        val tempDir = createTempDir()
        val config = ScannerConfiguration(
            sourceDirectory = tempDir,
            modelName = "ai/phi4:latest"
        )
        val scanner = Scanner(config)
        
        val result = scanner.executeScan(emptyList())
        
        assertNotNull(result)
        assertTrue(result.issues.isEmpty())
        assertTrue(result.filesAnalyzed == 0)
        assertTrue(result.totalFiles == 0)
        
        tempDir.deleteRecursively()
    }

    private fun createTempDir(): File {
        val tempDir = File.createTempFile("test", "dir")
        tempDir.delete()
        tempDir.mkdirs()
        return tempDir
    }
}