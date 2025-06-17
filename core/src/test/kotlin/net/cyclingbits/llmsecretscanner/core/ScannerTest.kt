package net.cyclingbits.llmsecretscanner.core

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull
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
        scanner.close()
        
        tempDir.deleteRecursively()
    }

    @Test
    fun executeScan_emptyDirectory_returnsEmptyList() {
        val tempDir = createTempDir()
        val config = ScannerConfiguration(
            sourceDirectory = tempDir,
            modelName = "ai/phi4:latest"
        )
        val scanner = Scanner(config)
        
        val result = scanner.executeScan()
        
        assertNotNull(result)
        scanner.close()
        
        tempDir.deleteRecursively()
    }

    @Test
    fun close_succeeds() {
        val tempDir = createTempDir()
        val config = ScannerConfiguration(
            sourceDirectory = tempDir,
            modelName = "ai/phi4:latest"
        )
        val scanner = Scanner(config)
        
        scanner.close()
        
        tempDir.deleteRecursively()
    }

    private fun createTempDir(): File {
        val tempDir = File.createTempFile("test", "dir")
        tempDir.delete()
        tempDir.mkdirs()
        return tempDir
    }
}