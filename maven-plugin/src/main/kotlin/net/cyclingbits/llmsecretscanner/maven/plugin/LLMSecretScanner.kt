package net.cyclingbits.llmsecretscanner.maven.plugin

import net.cyclingbits.llmsecretscanner.core.ScannerFactory
import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.config.ScannerDefaults
import net.cyclingbits.llmsecretscanner.core.exception.DockerContainerException
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import java.io.File
import java.io.FileNotFoundException

@Mojo(name = "scan", defaultPhase = LifecyclePhase.VERIFY)
class LLMSecretScanner : AbstractMojo() {

    @Parameter(property = "scan.sourceDirectories", defaultValue = "\${project.basedir}", required = true)
    private lateinit var sourceDirectories: List<File>

    @Parameter(
        property = "scan.include",
        defaultValue = ScannerDefaults.DEFAULT_INCLUDES
    )
    private lateinit var includes: String

    @Parameter(property = "scan.exclude", defaultValue = ScannerDefaults.DEFAULT_EXCLUDES)
    private lateinit var excludes: String

    @Parameter(property = "scan.modelName", defaultValue = ScannerDefaults.DEFAULT_MODEL_NAME)
    private lateinit var modelName: String

    @Parameter(property = "scan.chunkAnalysisTimeout", defaultValue = "${ScannerDefaults.CHUNK_ANALYSIS_TIMEOUT}")
    private var chunkAnalysisTimeout: Int = ScannerDefaults.CHUNK_ANALYSIS_TIMEOUT

    @Parameter(property = "scan.maxTokens", defaultValue = "${ScannerDefaults.MAX_TOKENS}")
    private var maxTokens: Int = ScannerDefaults.MAX_TOKENS

    @Parameter(property = "scan.temperature", defaultValue = "${ScannerDefaults.TEMPERATURE}")
    private var temperature: Double = ScannerDefaults.TEMPERATURE

    @Parameter(property = "scan.dockerImage", defaultValue = ScannerDefaults.DEFAULT_DOCKER_IMAGE)
    private lateinit var dockerImage: String

    @Parameter(property = "scan.maxFileSizeBytes", defaultValue = "${ScannerDefaults.MAX_FILE_SIZE_BYTES}")
    private var maxFileSizeBytes: Int = ScannerDefaults.MAX_FILE_SIZE_BYTES

    @Parameter(property = "scan.systemPrompt")
    private var systemPrompt: String? = null

    @Parameter(property = "scan.failOnError", defaultValue = "false")
    private var failOnError: Boolean = false

    @Parameter(property = "scan.enableChunking", defaultValue = "true")
    private var enableChunking: Boolean = true

    @Parameter(property = "scan.maxLinesPerChunk", defaultValue = "${ScannerDefaults.MAX_LINES_PER_CHUNK}")
    private var maxLinesPerChunk: Int = ScannerDefaults.MAX_LINES_PER_CHUNK

    @Parameter(property = "scan.chunkOverlapLines", defaultValue = "${ScannerDefaults.CHUNK_OVERLAP_LINES}")
    private var chunkOverlapLines: Int = ScannerDefaults.CHUNK_OVERLAP_LINES

    override fun execute() {
        configureLogging()
        try {
            val config = ScannerConfiguration(
                sourceDirectories = sourceDirectories,
                includes = includes,
                excludes = excludes,
                maxFileSizeBytes = maxFileSizeBytes,
                modelName = modelName,
                maxTokens = maxTokens,
                temperature = temperature,
                dockerImage = dockerImage,
                systemPrompt = systemPrompt ?: ScannerDefaults.loadSystemPrompt(),
                chunkAnalysisTimeout = chunkAnalysisTimeout,
                enableChunking = enableChunking,
                maxLinesPerChunk = maxLinesPerChunk,
                chunkOverlapLines = chunkOverlapLines
            )

            val scanResult = ScannerFactory.create(config).use { scanner ->
                scanner.scan()
            }

            if (scanResult.hasIssues && failOnError) {
                throw MojoFailureException("LLM Secret Scanner found ${scanResult.totalIssues} security issues. Build failed due to failOnError=true setting.")
            }

        } catch (e: MojoFailureException) {
            throw e
        } catch (e: FileNotFoundException) {
            handleError("System prompt file not found. Please check system_prompt.md exists in resources", e)
        } catch (e: DockerContainerException) {
            handleError("Please ensure Docker Desktop is running and Docker Model Runner is enabled", e)
        } catch (e: Exception) {
            handleError("LLM Secret Scanner execution failed: ${e.message}", e)
        }
    }

    private fun handleError(message: String, cause: Exception) {
        log.error(message, cause)
        if (failOnError) {
            throw MojoFailureException(message, cause)
        } else {
            log.warn("Continuing build despite error (failOnError=false)")
        }
    }

    private fun configureLogging() {
        // Disable TestContainers verbose logging before any container operations
        System.setProperty("testcontainers.reuse.enable", "false")
        System.setProperty("org.slf4j.simpleLogger.log.org.testcontainers", "off")
        System.setProperty("org.slf4j.simpleLogger.log.testcontainers", "off")
        System.setProperty("org.slf4j.simpleLogger.log.tc", "off")
        System.setProperty("org.slf4j.simpleLogger.log.com.github.dockerjava", "off")
        System.setProperty("org.slf4j.simpleLogger.log.docker", "off")
        System.setProperty("org.slf4j.simpleLogger.log.com.github.dockerjava.api", "off")
        System.setProperty("org.slf4j.simpleLogger.log.com.github.dockerjava.core", "off")
        System.setProperty("org.slf4j.simpleLogger.log.org.testcontainers.utility", "off")
        System.setProperty("org.slf4j.simpleLogger.log.org.testcontainers.dockerclient", "off")
        System.setProperty("org.slf4j.simpleLogger.log.org.testcontainers.containers", "off")
    }
}