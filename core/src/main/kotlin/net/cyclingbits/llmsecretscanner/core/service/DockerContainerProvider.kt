package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.exception.DockerContainerException
import net.cyclingbits.llmsecretscanner.core.util.LogColors
import org.slf4j.LoggerFactory
import org.testcontainers.containers.DockerModelRunnerContainer

object DockerContainerProvider {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun startContainer(modelName: String, dockerImage: String = "alpine/socat:1.7.4.3-r0"): DockerModelRunnerContainer {
        return try {
            val dockerModelRunnerContainer = DockerModelRunnerContainer(dockerImage).withModel(modelName)
            dockerModelRunnerContainer.start()
            dockerModelRunnerContainer
        } catch (e: Exception) {
            logger.error("{} Failed to start Docker container for model: {}", LogColors.ERROR_ICON, LogColors.boldRed(modelName), e)
            throw DockerContainerException("Error occurred while starting Docker container", e)
        }
    }

}