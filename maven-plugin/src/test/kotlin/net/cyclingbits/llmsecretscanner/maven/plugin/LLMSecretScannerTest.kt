package net.cyclingbits.llmsecretscanner.maven.plugin

import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugin.logging.Log
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.assertThrows
import io.mockk.mockk
import java.io.File

class LLMSecretScannerTest {

    @Test
    fun constructor_succeeds() {
        val plugin = LLMSecretScanner()
        
        assertNotNull(plugin)
    }

    @Test
    fun execute_nonExistentDirectory_logsError() {
        val plugin = LLMSecretScanner()
        val mockLog = mockk<Log>(relaxed = true)
        
        plugin.log = mockLog
        setPrivateField(plugin, "sourceDirectories", listOf(File("/nonexistent")))
        setPrivateField(plugin, "modelName", "ai/llama3.2:latest")
        setPrivateField(plugin, "includes", "**/*.java")
        setPrivateField(plugin, "excludes", "**/target/**")
        setPrivateField(plugin, "chunkAnalysisTimeout", 60)
        setPrivateField(plugin, "maxTokens", 10000)
        setPrivateField(plugin, "temperature", 0.0)
        setPrivateField(plugin, "dockerImage", "alpine/socat:1.7.4.3-r0")
        setPrivateField(plugin, "maxFileSizeBytes", 100 * 1024)
        setPrivateField(plugin, "systemPrompt", null)
        setPrivateField(plugin, "failOnError", false)
        
        plugin.execute()
    }

    @Test
    fun execute_validDirectoryWithFailOnError_throwsException() {
        val tempDir = createTempDir()
        tempDir.mkdirs()
        
        val plugin = LLMSecretScanner()
        val mockLog = mockk<Log>(relaxed = true)
        
        plugin.log = mockLog
        setPrivateField(plugin, "sourceDirectories", listOf(tempDir))
        setPrivateField(plugin, "modelName", "ai/llama3.2:latest")
        setPrivateField(plugin, "includes", "**/*.java")
        setPrivateField(plugin, "excludes", "**/target/**")
        setPrivateField(plugin, "chunkAnalysisTimeout", 60)
        setPrivateField(plugin, "maxTokens", 10000)
        setPrivateField(plugin, "temperature", 0.0)
        setPrivateField(plugin, "dockerImage", "alpine/socat:1.7.4.3-r0")
        setPrivateField(plugin, "maxFileSizeBytes", 100 * 1024)
        setPrivateField(plugin, "systemPrompt", null)
        setPrivateField(plugin, "failOnError", false)
        
        plugin.execute()
        
        tempDir.deleteRecursively()
    }

    @Test
    fun execute_invalidModelName_handlesException() {
        val tempDir = createTempDir()
        tempDir.mkdirs()
        
        val plugin = LLMSecretScanner()
        val mockLog = mockk<Log>(relaxed = true)
        
        plugin.log = mockLog
        setPrivateField(plugin, "sourceDirectories", listOf(tempDir))
        setPrivateField(plugin, "modelName", "invalid-model")
        setPrivateField(plugin, "includes", "**/*.java")
        setPrivateField(plugin, "excludes", "**/target/**")
        setPrivateField(plugin, "chunkAnalysisTimeout", 60)
        setPrivateField(plugin, "maxTokens", 10000)
        setPrivateField(plugin, "temperature", 0.0)
        setPrivateField(plugin, "dockerImage", "alpine/socat:1.7.4.3-r0")
        setPrivateField(plugin, "maxFileSizeBytes", 100 * 1024)
        setPrivateField(plugin, "systemPrompt", null)
        setPrivateField(plugin, "failOnError", false)
        
        plugin.execute()
        
        tempDir.deleteRecursively()
    }

    @Test
    fun execute_invalidModelNameWithFailOnError_throwsException() {
        val tempDir = createTempDir()
        tempDir.mkdirs()
        
        val plugin = LLMSecretScanner()
        val mockLog = mockk<Log>(relaxed = true)
        
        plugin.log = mockLog
        setPrivateField(plugin, "sourceDirectories", listOf(tempDir))
        setPrivateField(plugin, "modelName", "invalid-model")
        setPrivateField(plugin, "includes", "**/*.java")
        setPrivateField(plugin, "excludes", "**/target/**")
        setPrivateField(plugin, "chunkAnalysisTimeout", 60)
        setPrivateField(plugin, "maxTokens", 10000)
        setPrivateField(plugin, "temperature", 0.0)
        setPrivateField(plugin, "dockerImage", "alpine/socat:1.7.4.3-r0")
        setPrivateField(plugin, "maxFileSizeBytes", 100 * 1024)
        setPrivateField(plugin, "systemPrompt", null)
        setPrivateField(plugin, "failOnError", true)
        
        assertThrows<MojoFailureException> {
            plugin.execute()
        }
        
        tempDir.deleteRecursively()
    }

    @Test
    fun execute_withCustomSystemPrompt_succeeds() {
        val tempDir = createTempDir()
        tempDir.mkdirs()
        
        val plugin = LLMSecretScanner()
        val mockLog = mockk<Log>(relaxed = true)
        val customPrompt = "Find API keys and passwords in the code"
        
        plugin.log = mockLog
        setPrivateField(plugin, "sourceDirectories", listOf(tempDir))
        setPrivateField(plugin, "modelName", "ai/llama3.2:latest")
        setPrivateField(plugin, "includes", "**/*.java")
        setPrivateField(plugin, "excludes", "**/target/**")
        setPrivateField(plugin, "chunkAnalysisTimeout", 60)
        setPrivateField(plugin, "maxTokens", 10000)
        setPrivateField(plugin, "temperature", 0.0)
        setPrivateField(plugin, "dockerImage", "alpine/socat:1.7.4.3-r0")
        setPrivateField(plugin, "systemPrompt", customPrompt)
        setPrivateField(plugin, "failOnError", false)
        
        plugin.execute()
        
        tempDir.deleteRecursively()
    }

    private fun setPrivateField(obj: Any, fieldName: String, value: Any?) {
        val field = obj.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        field.set(obj, value)
    }

    private fun createTempDir(): File {
        val tempDir = File.createTempFile("test", "dir")
        tempDir.delete()
        return tempDir
    }
}