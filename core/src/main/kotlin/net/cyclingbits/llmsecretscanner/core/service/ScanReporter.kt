package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.model.Issue
import net.cyclingbits.llmsecretscanner.core.util.LogColors
import org.slf4j.LoggerFactory
import java.io.File

object ScanReporter {

    private val logger = LoggerFactory.getLogger(ScanReporter::class.java)

    fun reportScanStart(config: ScannerConfiguration) {
        logger.info("")
        logger.info("{} {}", LogColors.SCANNER_ICON, LogColors.boldCyan("Starting LLM Secret Scanner with configuration:"))
        logger.info("       {} Source directory: {}", LogColors.FILE_ICON, LogColors.blue(config.sourceDirectory.absolutePath))
        logger.info("       {} Model: {}", LogColors.DOCKER_ICON, LogColors.green(config.modelName))
        logger.info("       ⏱️ File analysis timeout: {}s", LogColors.yellow(config.fileAnalysisTimeout.toString()))
        logger.info("       ✅ Include patterns: {}", LogColors.cyan(config.includes))
        logger.info("       ❌ Exclude patterns: {}", LogColors.yellow(config.excludes))
        logger.info("")
    }

    fun reportFilesFound(fileCount: Int) {
        logger.info("{} Found {} files matching patterns", LogColors.FILE_ICON, LogColors.boldYellow(fileCount.toString()))
    }

    fun reportNoFilesFound() {
        logger.warn("No files found matching the specified patterns")
    }

    fun reportScanComplete(totalIssues: Int, filesAnalyzed: Int, totalFiles: Int, totalTimeMs: Long) {
        logger.info("")
        val totalTimeSeconds = String.format("%.1f", totalTimeMs / 1000.0)
        logger.info(
            "{} {} Analyzed {} of {} files, found {} total issues in {}s",
            LogColors.SUCCESS_ICON,
            LogColors.boldGreen("Scan completed successfully."),
            LogColors.boldCyan(filesAnalyzed.toString()),
            LogColors.boldYellow(totalFiles.toString()),
            LogColors.boldYellow(totalIssues.toString()),
            LogColors.yellow(totalTimeSeconds)
        )
    }

    fun reportError(message: String) {
        logger.error("{} {}", LogColors.ERROR_ICON, LogColors.boldRed(message))
    }

    fun reportError(message: String, exception: Throwable) {
        logger.error("{} {}", LogColors.ERROR_ICON, LogColors.boldRed(message), exception)
    }

    fun reportContainerStart() {
        logger.info("{} Starting Docker container...", LogColors.DOCKER_ICON)
    }

    fun reportContainerStarted() {
        logger.info("{} Docker container started successfully", LogColors.SUCCESS_ICON)
    }

    fun reportAnalysisStart(fileCount: Int) {
        logger.info("{} Starting analysis of {} files", LogColors.SCANNER_ICON, LogColors.boldYellow(fileCount.toString()))
    }

    fun reportFileAnalysisStart(file: File, fileIndex: Int, totalFiles: Int, baseDir: File) {
        logger.info("")
        val relativePath = try {
            baseDir.toPath().relativize(file.toPath()).toString()
        } catch (e: IllegalArgumentException) {
            // Fallback if relativize fails - just use the filename
            file.name
        }
        logger.info(
            "[{}/{}] Analyzing file {}...",
            LogColors.cyan(fileIndex.toString()),
            LogColors.cyan(totalFiles.toString()),
            LogColors.blue(relativePath)
        )
    }

    fun reportFileIssues(file: File, issues: List<Issue>, analysisTimeMs: Long, fileIndex: Int, totalFiles: Int, baseDir: File) {
        val analysisTimeSeconds = String.format("%.1f", analysisTimeMs / 1000.0)
        logger.info(
            "       Found {} {} (analyzed in {}s)",
            LogColors.boldYellow(issues.size.toString()),
            LogColors.cyan("issues"),
            LogColors.yellow(analysisTimeSeconds)
        )

        issues.forEachIndexed { index, issue ->
            val issueColor = when (issue.issueType.lowercase()) {
                "password" -> LogColors.boldRed(issue.issueType)
                "api key", "token" -> LogColors.boldYellow(issue.issueType)
                "database credentials" -> LogColors.boldRed(issue.issueType)
                "private key" -> LogColors.boldRed(issue.issueType)
                "secret" -> LogColors.boldYellow(issue.issueType)
                else -> LogColors.cyan(issue.issueType)
            }

            logger.info(
                "       {} #{} | Line {} | {} | {}",
                LogColors.ISSUE_ICON,
                LogColors.yellow((index + 1).toString()),
                LogColors.blue(issue.lineNumber.toString()),
                issueColor,
                issue.description
            )
        }
    }
}