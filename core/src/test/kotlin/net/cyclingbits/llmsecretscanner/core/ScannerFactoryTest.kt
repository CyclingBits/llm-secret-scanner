package net.cyclingbits.llmsecretscanner.core

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.File

class ScannerFactoryTest {

    @Test
    fun `should validate configuration without creating scanner`() {
        val validConfig = ScannerConfiguration(
            sourceDirectories = listOf(File(".")),
            modelName = "ai/test-model:latest"
        )
        
        assertNotNull(validConfig)
        assertEquals("ai/test-model:latest", validConfig.modelName)
        assertEquals(listOf(File(".")), validConfig.sourceDirectories)
    }

    @Test
    fun `should reject invalid configuration`() {
        assertThrows(IllegalArgumentException::class.java) {
            ScannerConfiguration(
                sourceDirectories = listOf(File("/non/existent/path")),
                modelName = "ai/test-model:latest"
            )
        }
    }
}