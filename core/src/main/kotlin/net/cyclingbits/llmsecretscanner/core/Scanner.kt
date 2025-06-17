package net.cyclingbits.llmsecretscanner.core

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.exception.*
import net.cyclingbits.llmsecretscanner.core.model.Issue
import net.cyclingbits.llmsecretscanner.core.service.*
import org.slf4j.LoggerFactory

/**
 * Main scanner orchestrator that coordinates file discovery, container management,
 * code analysis, and progress reporting.
 */
class Scanner(private val config: ScannerConfiguration) : AutoCloseable {

    private val logger = LoggerFactory.getLogger(Scanner::class.java)
    
    private val fileScanner = FileScanner(config)
    private val reporter = ScanReporter()
    private val containerManager = ContainerManager(config, reporter)
    
    init {
        // Disable TestContainers verbose logging
        System.setProperty("testcontainers.reuse.enable", "false")
        System.setProperty("org.slf4j.simpleLogger.log.org.testcontainers", "off")
        System.setProperty("org.slf4j.simpleLogger.log.testcontainers", "off")
        System.setProperty("org.slf4j.simpleLogger.log.tc", "off")
        System.setProperty("org.slf4j.simpleLogger.log.com.github.dockerjava", "off")
    }

    /**
     * Executes the complete scanning workflow:
     * 1. Reports scan start
     * 2. Discovers files to scan
     * 3. Starts Docker container
     * 4. Analyzes files with LLM
     * 5. Reports results
     */
    fun executeScan(): List<Issue> {
        return try {
            // Report scan configuration
            reporter.reportScanStart(config)
            
            // Discover files to scan
            val filesToScan = fileScanner.findFiles()
            
            if (filesToScan.isEmpty()) {
                reporter.reportNoFilesFound()
                return emptyList()
            }
            
            reporter.reportFilesFound(filesToScan.size)
            
            // Start container
            val container = containerManager.startContainer()

            // Analyze files
            val scanStartTime = System.currentTimeMillis()
            val codeAnalyzer = CodeAnalyzer(config, container, reporter)
            val issues = codeAnalyzer.analyzeFiles(filesToScan)
            
            // Report final results
            val totalTime = System.currentTimeMillis() - scanStartTime
            reporter.reportScanComplete(issues.size, totalTime)
            
            issues

        } catch (e: NoFilesFoundException) {
            logger.warn("No files found to scan: {}", e.message)
            reporter.reportNoFilesFound()
            emptyList()
        } catch (e: DockerContainerException) {
            logger.error("Failed to start Docker container: {}", e.message, e)
            reporter.reportError("Docker container startup failed")
            emptyList()
        } catch (e: TimeoutException) {
            logger.error("LLM analysis timed out: {}", e.message, e)
            reporter.reportError("Analysis timed out")
            emptyList()
        } catch (e: AnalysisException) {
            logger.error("Analysis failed: {}", e.message, e)
            reporter.reportError("Code analysis failed")
            emptyList()
        } catch (e: JsonParserException) {
            logger.error("Failed to parse LLM response: {}", e.message, e)
            reporter.reportError("Response parsing failed")
            emptyList()
        } catch (e: Exception) {
            logger.error("Unexpected error during scan: {}", e.message, e)
            reporter.reportError("Unexpected error occurred")
            emptyList()
        }
    }

    override fun close() {
        containerManager.close()
    }
}