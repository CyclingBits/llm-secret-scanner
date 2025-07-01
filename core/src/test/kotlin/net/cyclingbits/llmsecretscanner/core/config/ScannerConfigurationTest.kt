package net.cyclingbits.llmsecretscanner.core.config

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.io.File

class ScannerConfigurationTest {

    @Test
    fun `should create configuration with defaults`() {
        val config = ScannerConfiguration(
            sourceDirectories = listOf(File(".")),
            modelName = "ai/test-model:latest"
        )
        
        assertEquals(listOf(File(".")), config.sourceDirectories)
        assertEquals("ai/test-model:latest", config.modelName)
        assertEquals("**/*.java,**/*.kt,**/*.xml,**/*.properties,**/*.yml,**/*.yaml,**/*.json,**/*.md,**/*.sql,**/*.gradle,**/*.kts,**/*.env,**/*.sh,**/*.bat,**/*.html,**/*.css,**/*.js,**/*.ts,**/*.dockerfile", config.includes)
        assertEquals("**/target/**", config.excludes)
        assertEquals(100 * 1024, config.maxFileSizeBytes)
        assertEquals(true, config.enableChunking)
        assertEquals(40, config.maxLinesPerChunk)
        assertEquals(5, config.chunkOverlapLines)
    }

    @Test
    fun `should validate source directories exist`() {
        val exception = assertThrows<IllegalArgumentException> {
            ScannerConfiguration(
                sourceDirectories = listOf(File("/non/existent/path")),
                modelName = "ai/test-model:latest"
            )
        }
        
        assertTrue(exception.message!!.contains("Source directory must exist"))
    }

    @Test
    fun `should validate model name format`() {
        val exception = assertThrows<IllegalArgumentException> {
            ScannerConfiguration(
                sourceDirectories = listOf(File(".")),
                modelName = "invalid-model-name"
            )
        }
        
        assertTrue(exception.message!!.contains("Invalid model name format"))
    }

    @Test
    fun `should validate chunk size is positive`() {
        val exception = assertThrows<IllegalArgumentException> {
            ScannerConfiguration(
                sourceDirectories = listOf(File(".")),
                modelName = "ai/test-model:latest",
                maxLinesPerChunk = 0
            )
        }
        
        assertTrue(exception.message!!.contains("Max lines per chunk must be positive"))
    }

    @Test
    fun `should validate overlap is less than chunk size`() {
        val exception = assertThrows<IllegalArgumentException> {
            ScannerConfiguration(
                sourceDirectories = listOf(File(".")),
                modelName = "ai/test-model:latest",
                maxLinesPerChunk = 10,
                chunkOverlapLines = 15
            )
        }
        
        assertTrue(exception.message!!.contains("must be less than max lines per chunk"))
    }

    @Test
    fun `should validate temperature range`() {
        val exception = assertThrows<IllegalArgumentException> {
            ScannerConfiguration(
                sourceDirectories = listOf(File(".")),
                modelName = "ai/test-model:latest",
                temperature = 2.5
            )
        }
        
        assertTrue(exception.message!!.contains("Temperature must be between 0.0 and 2.0"))
    }
}