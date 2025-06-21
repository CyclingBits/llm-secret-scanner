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
        logger.info("       ⏱️ Chunk analysis timeout: {}s", LogColors.yellow(config.chunkAnalysisTimeout.toString()))
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

    fun reportWarning(message: String) {
        logger.warn("       ⚠️ {}", LogColors.yellow(message))
    }

    fun reportChunk(chunkCount: Int, maxLinesPerChunk: Int, overlapLines: Int) {
        logger.info("       📄 Chunking file into {} chunks ({} lines per chunk, {} overlap)", 
            LogColors.boldYellow(chunkCount.toString()),
            LogColors.cyan(maxLinesPerChunk.toString()),
            LogColors.cyan(overlapLines.toString())
        )
    }

    fun reportChunkAnalysis(chunkIndex: Int, totalChunks: Int, startLine: Int, endLine: Int) {
        logger.info("       🔍 Analyzing chunk [{}/{}] (lines {}-{})", 
            LogColors.cyan(chunkIndex.toString()),
            LogColors.cyan(totalChunks.toString()),
            LogColors.blue(startLine.toString()),
            LogColors.blue(endLine.toString())
        )
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
                (issue.secretValue ?: "No secret value").let { if (it.length > 25) it.take(25) + "..." else it }
            )
        }
    }

    fun reportCorrectDetection(issue: Issue) {
        logger.info("{} Correct at line {} - {}", 
            LogColors.SUCCESS_ICON, 
            LogColors.blue(issue.lineNumber.toString()), 
            issue.secretValue?.take(25) ?: "No value"
        )
    }

    fun reportIncorrectDetection(issue: Issue) {
        logger.info("{} Incorrect at line {} - {}", 
            LogColors.ERROR_ICON, 
            LogColors.blue(issue.lineNumber.toString()), 
            issue.secretValue?.take(25) ?: "No value"
        )
    }

    fun reportMissedSecret(issue: Issue) {
        logger.info("{} Missed at line {} - {}", 
            LogColors.ERROR_ICON, 
            LogColors.blue(issue.lineNumber.toString()), 
            issue.secretValue?.take(25) ?: "No value"
        )
    }

    fun reportEvaluationStart(message: String) {
        logger.info("")
        logger.info("{} {}", LogColors.SCANNER_ICON, LogColors.boldCyan(message))
    }

    fun reportEvaluationComplete(message: String) {
        logger.info("{} {}", LogColors.SUCCESS_ICON, LogColors.boldGreen(message))
    }

    fun reportFileSaved(filePath: String) {
        logger.info("{} Results saved to {}", "💾", LogColors.blue(filePath))
    }

    fun reportDetectionResults(correctCount: Int, incorrectCount: Int, missedCount: Int) {
        logger.info("")
        logger.info("📊 Detection results: {} correct, {} incorrect, {} missed",
            LogColors.boldGreen(correctCount.toString()), 
            LogColors.boldRed(incorrectCount.toString()),
            LogColors.boldYellow(missedCount.toString())
        )
    }
}