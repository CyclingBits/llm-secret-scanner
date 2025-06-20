package net.cyclingbits.llmsecretscanner.maven.plugin

import net.cyclingbits.llmsecretscanner.core.Scanner
import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.service.FileScanner
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import java.io.File

@Mojo(name = "scan", defaultPhase = LifecyclePhase.VERIFY)
class LLMSecretScanner : AbstractMojo() {

    @Parameter(property = "scan.sourceDirectory", defaultValue = "\${project.basedir}", required = true)
    private lateinit var sourceDirectory: File

    @Parameter(
        property = "scan.include",
        defaultValue = "**/*.java,**/*.kt,**/*.xml,**/*.properties,**/*.yml,**/*.yaml,**/*.json,**/*.md,**/*.sql,**/*.gradle,**/*.kts,**/*.env,**/*.sh,**/*.bat,**/*.html,**/*.css,**/*.js,**/*.ts,**/*.dockerfile"
    )
    private lateinit var includes: String

    @Parameter(property = "scan.exclude", defaultValue = "**/target/**")
    private lateinit var excludes: String

    @Parameter(property = "scan.modelName", defaultValue = "ai/phi4:latest")
    private lateinit var modelName: String

    @Parameter(property = "scan.fileAnalysisTimeout", defaultValue = "60")
    private var fileAnalysisTimeout: Int = 60

    @Parameter(property = "scan.maxTokens", defaultValue = "10000")
    private var maxTokens: Int = 10_000

    @Parameter(property = "scan.temperature", defaultValue = "0.0")
    private var temperature: Double = 0.0

    @Parameter(property = "scan.dockerImage", defaultValue = "alpine/socat:1.7.4.3-r0")
    private lateinit var dockerImage: String

    @Parameter(property = "scan.maxFileSizeBytes", defaultValue = "102400")
    private var maxFileSizeBytes: Int = 100 * 1024

    @Parameter(property = "scan.systemPrompt")
    private var systemPrompt: String? = null

    @Parameter(property = "scan.failOnError", defaultValue = "false")
    private var failOnError: Boolean = false

    override fun execute() {
        try {
            val config = ScannerConfiguration(
                sourceDirectory = sourceDirectory,
                includes = includes,
                excludes = excludes,
                modelName = modelName,
                fileAnalysisTimeout = fileAnalysisTimeout,
                maxTokens = maxTokens,
                temperature = temperature,
                dockerImage = dockerImage,
                maxFileSizeBytes = maxFileSizeBytes,
                systemPrompt = systemPrompt
            )

            val fileScanner = FileScanner(config).findFiles()
            
            val scanResult = Scanner(config).use { scanner ->
                scanner.executeScan(fileScanner)
            }

            if (scanResult.issues.isNotEmpty() && failOnError) {
                throw MojoFailureException("LLM Secret Scanner found ${scanResult.issues.size} security issues. Build failed due to failOnError=true setting.")
            }

        } catch (e: MojoFailureException) {
            throw e // Re-throw MojoFailureException as-is
        } catch (e: Exception) {
            log.error("LLM Secret Scanner execution failed: ${e.message}", e)
            if (failOnError) {
                throw MojoFailureException("LLM Secret Scanner execution failed: ${e.message}", e)
            } else {
                log.warn("Continuing build despite scanner failure (failOnError=false)")
            }
        }
    }

}