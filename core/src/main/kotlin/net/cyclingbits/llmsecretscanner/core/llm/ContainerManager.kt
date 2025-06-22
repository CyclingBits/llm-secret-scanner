package net.cyclingbits.llmsecretscanner.core.llm

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.exception.DockerContainerException
import net.cyclingbits.llmsecretscanner.core.util.ScanReporter
import org.testcontainers.containers.DockerModelRunnerContainer

class ContainerManager(
    private val config: ScannerConfiguration,
) : AutoCloseable {

    private var container: DockerModelRunnerContainer? = null

    fun startContainer(): DockerModelRunnerContainer {
        return try {
            ScanReporter.reportContainerStart()

            val dockerModelRunnerContainer = DockerModelRunnerContainer(config.dockerImage).withModel(config.modelName)
            dockerModelRunnerContainer.start()
            container = dockerModelRunnerContainer

            ScanReporter.reportContainerStarted()

            dockerModelRunnerContainer

        } catch (e: Exception) {
            throw DockerContainerException("Error occurred while starting Docker container", e)
        }
    }

    override fun close() {
        container?.let { containerInstance ->
            try {
                if (containerInstance.isRunning) {
                    containerInstance.stop()
                }
            } catch (e: Exception) {
                ScanReporter.reportError("Error stopping container: ${containerInstance.containerId}", e)
            }
        }
    }

}