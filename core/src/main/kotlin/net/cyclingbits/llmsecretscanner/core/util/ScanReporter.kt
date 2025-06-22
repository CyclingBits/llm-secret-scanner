package net.cyclingbits.llmsecretscanner.core.util

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.config.ScannerDefaults
import net.cyclingbits.llmsecretscanner.core.model.Issue
import org.slf4j.LoggerFactory
import java.io.File

object ScanReporter {

    private val logger = LoggerFactory.getLogger(ScanReporter::class.java)

    private fun findBaseDirectory(file: File, sourceDirectories: List<File>): File {
        return sourceDirectories.find { dir ->
            file.absolutePath.startsWith(dir.absolutePath)
        } ?: sourceDirectories.firstOrNull() ?: File(".")
    }

    fun reportScanStart(config: ScannerConfiguration) {
        logger.info("")
        logger.info("{} {}", LogColors.SCANNER_ICON, LogColors.boldCyan("Starting LLM Secret Scanner with configuration:"))
        if (config.sourceDirectories.size == 1) {
            logger.info("       {} Source directory: {}", LogColors.FILE_ICON, LogColors.blue(config.sourceDirectories[0].absolutePath))
        } else {
            logger.info("       {} Source directories ({}):", LogColors.FILE_ICON, LogColors.yellow(config.sourceDirectories.size.toString()))
            config.sourceDirectories.forEach { dir ->
                logger.info("         - {}", LogColors.blue(dir.absolutePath))
            }
        }
        logger.info("       {} Model: {}", LogColors.DOCKER_ICON, LogColors.green(config.modelName))
        logger.info("       ⏱️ Chunk analysis timeout: {}s", LogColors.yellow(config.chunkAnalysisTimeout.toString()))
        logger.info("       ✅ Include patterns: {}", LogColors.cyan(config.includes))
        logger.info("       ❌ Exclude patterns: {}", LogColors.yellow(config.excludes))
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
        logger.info("")
        logger.info("{} Starting Docker container...", LogColors.DOCKER_ICON)
    }

    fun reportContainerStarted() {
        logger.info("{} Docker container started successfully", LogColors.SUCCESS_ICON)
    }

    fun reportAnalysisStartForDirectory(directory: File, fileCount: Int) {
        logger.info("")
        logger.info("{} Starting analysis of {} files in {}",
            LogColors.SCANNER_ICON, 
            LogColors.boldYellow(fileCount.toString()),
            LogColors.blue(directory.absolutePath)
        )
    }

    fun reportFileAnalysisStart(file: File, fileIndex: Int, totalFiles: Int, sourceDirectories: List<File>) {
        val baseDir = findBaseDirectory(file, sourceDirectories)
        logger.info("")
        val relativePath = try {
            baseDir.toPath().relativize(file.toPath()).toString()
        } catch (e: IllegalArgumentException) {
            file.name
        }
        logger.info(
            "[{}/{}] Analyzing file {}...",
            LogColors.cyan(fileIndex.toString()),
            LogColors.cyan(totalFiles.toString()),
            LogColors.blue(relativePath)
        )
    }

    fun reportFileIssues(file: File, issues: List<Issue>, analysisTimeMs: Long, fileIndex: Int, totalFiles: Int, sourceDirectories: List<File>) {
        val baseDir = findBaseDirectory(file, sourceDirectories)
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
                (issue.secretValue ?: "No secret value").let { if (it.length > ScannerDefaults.SECRET_DISPLAY_LENGTH) it.take(ScannerDefaults.SECRET_DISPLAY_LENGTH) + "..." else it }
            )
        }
    }

    fun reportCorrectDetection(issue: Issue) {
        logger.info("{} Correct at line {} - {}", 
            LogColors.SUCCESS_ICON, 
            LogColors.blue(issue.lineNumber.toString()), 
            issue.secretValue?.take(ScannerDefaults.SECRET_DISPLAY_LENGTH) ?: "No value"
        )
    }

    fun reportIncorrectDetection(issue: Issue) {
        logger.info("{} Incorrect at line {} - {}", 
            LogColors.ERROR_ICON, 
            LogColors.blue(issue.lineNumber.toString()), 
            issue.secretValue?.take(ScannerDefaults.SECRET_DISPLAY_LENGTH) ?: "No value"
        )
    }

    fun reportMissedSecret(issue: Issue) {
        logger.info("{} Missed at line {} - {}", 
            LogColors.ERROR_ICON, 
            LogColors.blue(issue.lineNumber.toString()), 
            issue.secretValue?.take(ScannerDefaults.SECRET_DISPLAY_LENGTH) ?: "No value"
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

    fun reportFalsePositives(falsePositiveCount: Int) {
        logger.info("⚠️ False positives: {} issues detected in files that should have none",
            LogColors.boldRed(falsePositiveCount.toString())
        )
    }
}