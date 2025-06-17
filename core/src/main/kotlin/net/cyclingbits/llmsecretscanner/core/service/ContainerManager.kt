package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import org.testcontainers.containers.DockerModelRunnerContainer

class ContainerManager(
    private val config: ScannerConfiguration,
    private val reporter: ScanReporter
) : AutoCloseable {
    
    private var container: DockerModelRunnerContainer? = null
    
    fun startContainer(): DockerModelRunnerContainer {
        reporter.reportContainerStart()
        
        val startedContainer = DockerContainerProvider.startContainer(config.modelName, config.dockerImage)
        container = startedContainer
        
        reporter.reportContainerStarted()
        return startedContainer
    }
    
    override fun close() {
        container?.let { containerInstance ->
            try {
                if (containerInstance.isRunning) {
                    containerInstance.stop()
                } else {
                }
            } catch (e: Exception) {
                reporter.reportError("Error stopping container: ${containerInstance.containerId}", e)
            }
        }
    }
}