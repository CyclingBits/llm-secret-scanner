package net.cyclingbits.llmsecretscanner.core.config

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import java.io.File

class ScannerConfigurationTest {

    @Test
    fun create_validConfiguration_succeeds() {
        val tempDir = createTempDir()
        
        val config = ScannerConfiguration(
            sourceDirectory = tempDir,
            modelName = "ai/phi4:latest"
        )
        
        assertEquals(tempDir, config.sourceDirectory)
        assertEquals("ai/phi4:latest", config.modelName)
        assertEquals(1, config.timeout)
        
        tempDir.deleteRecursively()
    }

    @Test
    fun create_invalidModelName_throwsException() {
        val tempDir = createTempDir()
        
        assertThrows<IllegalArgumentException> {
            ScannerConfiguration(
                sourceDirectory = tempDir,
                modelName = "invalid-model-name"
            )
        }
        
        tempDir.deleteRecursively()
    }

    @Test
    fun create_nonExistentDirectory_throwsException() {
        val nonExistentDir = File("/path/that/does/not/exist")
        
        assertThrows<IllegalArgumentException> {
            ScannerConfiguration(
                sourceDirectory = nonExistentDir,
                modelName = "ai/phi4:latest"
            )
        }
    }

    @Test
    fun create_invalidTimeout_throwsException() {
        val tempDir = createTempDir()
        
        assertThrows<IllegalArgumentException> {
            ScannerConfiguration(
                sourceDirectory = tempDir,
                modelName = "ai/phi4:latest",
                timeout = -1
            )
        }
        
        tempDir.deleteRecursively()
    }

    @Test
    fun create_timeoutTooLarge_throwsException() {
        val tempDir = createTempDir()
        
        assertThrows<IllegalArgumentException> {
            ScannerConfiguration(
                sourceDirectory = tempDir,
                modelName = "ai/phi4:latest",
                timeout = 40
            )
        }
        
        tempDir.deleteRecursively()
    }

    @Test
    fun create_invalidMaxTokens_throwsException() {
        val tempDir = createTempDir()
        
        assertThrows<IllegalArgumentException> {
            ScannerConfiguration(
                sourceDirectory = tempDir,
                modelName = "ai/phi4:latest",
                maxTokens = 0
            )
        }
        
        tempDir.deleteRecursively()
    }

    @Test
    fun create_invalidTemperature_throwsException() {
        val tempDir = createTempDir()
        
        assertThrows<IllegalArgumentException> {
            ScannerConfiguration(
                sourceDirectory = tempDir,
                modelName = "ai/phi4:latest",
                temperature = 3.0
            )
        }
        
        tempDir.deleteRecursively()
    }

    @Test
    fun create_emptyIncludes_throwsException() {
        val tempDir = createTempDir()
        
        assertThrows<IllegalArgumentException> {
            ScannerConfiguration(
                sourceDirectory = tempDir,
                modelName = "ai/phi4:latest",
                includes = ""
            )
        }
        
        tempDir.deleteRecursively()
    }

    @Test
    fun create_withCustomSystemPrompt_succeeds() {
        val tempDir = createTempDir()
        val customPrompt = "Find secrets and API keys in the code"
        
        val config = ScannerConfiguration(
            sourceDirectory = tempDir,
            modelName = "ai/phi4:latest",
            systemPrompt = customPrompt
        )
        
        assertEquals(customPrompt, config.systemPrompt)
        
        tempDir.deleteRecursively()
    }

    @Test
    fun create_withNullSystemPrompt_succeeds() {
        val tempDir = createTempDir()
        
        val config = ScannerConfiguration(
            sourceDirectory = tempDir,
            modelName = "ai/phi4:latest",
            systemPrompt = null
        )
        
        assertEquals(null, config.systemPrompt)
        
        tempDir.deleteRecursively()
    }

    private fun createTempDir(): File {
        val tempDir = File.createTempFile("test", "dir")
        tempDir.delete()
        tempDir.mkdirs()
        return tempDir
    }
}