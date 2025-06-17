package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.File

class ContainerManagerTest {

    @Test
    fun constructor_validConfig_succeeds() {
        val tempDir = createTempDir()
        val config = ScannerConfiguration(
            sourceDirectory = tempDir,
            modelName = "ai/phi4:latest"
        )
        val reporter = mockk<ScanReporter>(relaxed = true)

        val containerManager = ContainerManager(config, reporter)

        assertNotNull(containerManager)

        tempDir.deleteRecursively()
    }

    @Test
    fun close_withoutContainer_succeeds() {
        val tempDir = createTempDir()
        val config = ScannerConfiguration(
            sourceDirectory = tempDir,
            modelName = "ai/phi4:latest"
        )
        val reporter = mockk<ScanReporter>(relaxed = true)
        val containerManager = ContainerManager(config, reporter)

        containerManager.close()

        tempDir.deleteRecursively()
    }

    private fun createTempDir(): File {
        val tempDir = File.createTempFile("test", "dir")
        tempDir.delete()
        tempDir.mkdirs()
        return tempDir
    }
}