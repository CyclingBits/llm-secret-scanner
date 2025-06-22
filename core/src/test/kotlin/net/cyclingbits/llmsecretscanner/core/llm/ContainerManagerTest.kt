package net.cyclingbits.llmsecretscanner.core.llm

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.llm.ContainerManager
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.File

class ContainerManagerTest {

    @Test
    fun constructor_validConfig_succeeds() {
        val tempDir = createTempDir()
        val config = ScannerConfiguration(
            sourceDirectories = listOf(tempDir),
            modelName = "ai/phi4:latest"
        )

        val containerManager = ContainerManager(config)

        assertNotNull(containerManager)

        tempDir.deleteRecursively()
    }

    @Test
    fun close_withoutContainer_succeeds() {
        val tempDir = createTempDir()
        val config = ScannerConfiguration(
            sourceDirectories = listOf(tempDir),
            modelName = "ai/phi4:latest"
        )
        val containerManager = ContainerManager(config)

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