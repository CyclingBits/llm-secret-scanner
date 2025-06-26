package net.cyclingbits.llmsecretscanner.core.llm

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.exception.DockerContainerException
import net.cyclingbits.llmsecretscanner.core.logger.ScanLogger
import org.testcontainers.containers.DockerModelRunnerContainer

class ContainerManager(
    private val config: ScannerConfiguration,
    private val logger: ScanLogger
) : AutoCloseable {

    private var container: DockerModelRunnerContainer? = null

    fun getContainer(): DockerModelRunnerContainer {
        if (container == null) {
            startContainer()
        }
        return requireNotNull(container) { "Container failed to start" }
    }

    private fun startContainer(): DockerModelRunnerContainer {
        return try {
            logger.reportContainerStart()

            val dockerModelRunnerContainer = DockerModelRunnerContainer(config.dockerImage).withModel(config.modelName)
            dockerModelRunnerContainer.start()
            container = dockerModelRunnerContainer

            logger.reportContainerStarted()

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
                logger.reportError("Error stopping container: ${containerInstance.containerId}", e)
            }
        }
        container = null
    }

}