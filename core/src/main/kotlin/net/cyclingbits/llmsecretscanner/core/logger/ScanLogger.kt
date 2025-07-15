package net.cyclingbits.llmsecretscanner.core.logger

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.file.FileChunk
import net.cyclingbits.llmsecretscanner.core.model.FileScanResult
import net.cyclingbits.llmsecretscanner.core.model.Issue
import net.cyclingbits.llmsecretscanner.events.*
import net.cyclingbits.llmsecretscanner.events.LoggerUtils.getRelativePath
import net.cyclingbits.llmsecretscanner.events.LoggerUtils.toSecondsString
import net.cyclingbits.llmsecretscanner.events.LoggerUtils.truncateSecret
import java.io.File
import java.time.Duration

class ScanLogger(override val eventSource: EventSource = EventSource.CORE) : Logger {

    fun reportScanStart() {
        EventStore.log(
            source = eventSource,
            type = EventType.SCANNER,
            level = LogLevel.INFO
        ) {
            addEmpty()
            add("{} {}", LoggerColors.SCANNER_ICON, LoggerColors.boldCyan("Starting LLM Secret Scanner"))
        }
    }

    fun reportScanConfiguration(config: ScannerConfiguration) {
        EventStore.log(
            source = eventSource,
            type = EventType.SCANNER,
            level = LogLevel.INFO,
            data = mapOf("config" to config)
        ) {
            add("       Configuration:")
            add("       {} Model: {}", LoggerColors.DOCKER_ICON, LoggerColors.green(config.modelName))
            add("       {} Docker image: {}", LoggerColors.DOCKER_ICON, LoggerColors.green(config.dockerImage))
            if (config.sourceDirectories.size == 1) {
                add("       {} Source directory: {}", LoggerColors.FILE_ICON, LoggerColors.blue(config.sourceDirectories[0].absolutePath))
            } else {
                add("       {} Source directories ({}):", LoggerColors.FILE_ICON, LoggerColors.yellow(config.sourceDirectories.size.toString()))
                config.sourceDirectories.forEach { dir ->
                    add("           - {}", LoggerColors.blue(dir.absolutePath))
                }
            }
            add("       {} Include patterns: {}", LoggerColors.INCLUDE_ICON, LoggerColors.cyan(config.includes))
            add("       {} Exclude patterns: {}", LoggerColors.EXCLUDE_ICON, LoggerColors.yellow(config.excludes))
            add("       {} Max file size: {}KB", LoggerColors.FILE_SIZE_ICON, LoggerColors.cyan((config.maxFileSizeBytes / 1024).toString()))
            add("       {} System prompt: {}", LoggerColors.INFO_ICON, LoggerColors.cyan(truncateAndClean(config.systemPrompt)))
            add("       {} Max tokens: {}", LoggerColors.TOKENS_ICON, LoggerColors.cyan(config.maxTokens.toString()))
            add("       {} Temperature: {}", LoggerColors.TEMPERATURE_ICON, LoggerColors.cyan(config.temperature.toString()))
            add("       {} Top P: {}", LoggerColors.TEMPERATURE_ICON, LoggerColors.cyan(config.topP.toString()))
            add("       {} Seed: {}", LoggerColors.INFO_ICON, LoggerColors.cyan(config.seed.toString()))
            add("       {} Frequency penalty: {}", LoggerColors.INFO_ICON, LoggerColors.cyan(config.frequencyPenalty.toString()))
            add("       {} Connection timeout: {}s", LoggerColors.TIMEOUT_ICON, LoggerColors.yellow(config.timeout.toString()))
            add("       {} Chunk analysis timeout: {}s", LoggerColors.TIMEOUT_ICON, LoggerColors.yellow(config.chunkAnalysisTimeout.toString()))
            add("       {} Enable chunking: {}", LoggerColors.CHUNKING_ICON, LoggerColors.cyan(config.enableChunking.toString()))
            add("       {} Max lines per chunk: {}", LoggerColors.CHUNK_SIZE_ICON, LoggerColors.cyan(config.maxLinesPerChunk.toString()))
            add("       {} Chunk overlap lines: {}", LoggerColors.CHUNK_OVERLAP_ICON, LoggerColors.cyan(config.chunkOverlapLines.toString()))
        }
    }

    fun reportContainerStart() {
        EventStore.log(
            source = eventSource,
            type = EventType.CONTAINER,
            level = LogLevel.INFO
        ) {
            addEmpty()
            add("{} Starting Docker container...", LoggerColors.DOCKER_ICON)
        }
    }

    fun reportContainerStarted() {
        EventStore.log(
            source = eventSource,
            type = EventType.CONTAINER,
            level = LogLevel.INFO
        ) {
            add("{} Docker container started successfully", LoggerColors.SUCCESS_ICON)
        }
    }

    fun reportFilesFound(sourceDirectories: List<File>, filesToScan: List<File>, config: ScannerConfiguration) {
        EventStore.log(
            source = eventSource,
            type = EventType.FILE,
            level = LogLevel.INFO,
            data = mapOf(
                "sourceDirectories" to sourceDirectories,
                "filesToScan" to filesToScan,
                "includes" to config.includes,
                "excludes" to config.excludes
            )
        ) {
            addEmpty()
            add("{} Found {} file matching patterns in:", LoggerColors.FILE_ICON, LoggerColors.boldYellow(filesToScan.size.toString()))
            sourceDirectories.forEach { dir ->
                add("    - {}", LoggerColors.blue(dir.absolutePath))
            }
            add("    {} Include patterns: {}", LoggerColors.INCLUDE_ICON, LoggerColors.cyan(config.includes))
            add("    {} Exclude patterns: {}", LoggerColors.EXCLUDE_ICON, LoggerColors.yellow(config.excludes))
        }
    }

    fun reportNoFilesFound(config: ScannerConfiguration, sourceDirectories: List<File>) {
        EventStore.log(
            source = eventSource,
            type = EventType.FILE,
            level = LogLevel.WARN,
            data = mapOf(
                "sourceDirectories" to sourceDirectories,
                "includes" to config.includes,
                "excludes" to config.excludes
            )
        ) {
            add("{} No file found matching the specified patterns", LoggerColors.WARNING_ICON)
        }
    }

    fun reportFileAnalysisStart(file: File, fileIndex: Int, totalFiles: Int, config: ScannerConfiguration) {
        EventStore.log(
            source = eventSource,
            type = EventType.ANALYSE,
            level = LogLevel.INFO,
            data = mapOf(
                "file" to file,
                "fileIndex" to fileIndex,
                "totalFiles" to totalFiles
            )
        ) {
            addEmpty()
            add(
                "[{}/{}] Analyzing file {}...",
                LoggerColors.cyan(fileIndex.toString()),
                LoggerColors.cyan(totalFiles.toString()),
                LoggerColors.blue(getRelativePath(file, config.sourceDirectories))
            )
        }
    }

    fun reportChunk(file: File, chunkCount: Int, config: ScannerConfiguration) {
        EventStore.log(
            source = eventSource,
            type = EventType.CHUNK,
            level = LogLevel.INFO,
            data = mapOf(
                "file" to file,
                "chunkCount" to chunkCount,
                "maxLinesPerChunk" to config.maxLinesPerChunk,
                "overlapLines" to config.chunkOverlapLines
            )
        ) {
            add(
                "       {} Chunking file {} into {} chunks ({} lines per chunk, {} overlap)",
                LoggerColors.CHUNK_ICON,
                LoggerColors.blue(getRelativePath(file, config.sourceDirectories)),
                LoggerColors.boldYellow(chunkCount.toString()),
                LoggerColors.cyan(config.maxLinesPerChunk.toString()),
                LoggerColors.cyan(config.chunkOverlapLines.toString())
            )
        }
    }

    fun reportChunkAnalysis(chunk: FileChunk, fileType: String) {
        EventStore.log(
            source = eventSource,
            type = EventType.CHUNK,
            level = LogLevel.INFO,
            data = mapOf(
                "fileType" to fileType,
                "chunkIndex" to chunk.chunkIndex,
                "totalChunks" to chunk.totalChunks,
                "startLine" to chunk.startLine,
                "endLine" to chunk.endLine,
                "chunkContent" to chunk.content.lines(),
            )
        ) {
            add(
                "       {} Analyzing chunk {}/{} (lines {}-{})",
                LoggerColors.ANALYSIS_ICON,
                LoggerColors.cyan((chunk.chunkIndex + 1).toString()),
                LoggerColors.cyan(chunk.totalChunks.toString()),
                LoggerColors.blue(chunk.startLine.toString()),
                LoggerColors.blue(chunk.endLine.toString())
            )
        }
    }

    fun reportLLMRequest(parameters: Map<String, Any?>) {
        EventStore.log(
            source = eventSource,
            type = EventType.LLM,
            level = LogLevel.INFO,
            data = mapOf("parameters" to parameters)
        ) {
            add("              {} Sending LLM request", LoggerColors.REQUEST_ICON)
        }
    }

    fun reportLLMTimeout(config: ScannerConfiguration, error: Throwable) {
        EventStore.log(
            source = eventSource,
            type = EventType.LLM,
            level = LogLevel.ERROR,
            data = mapOf("timeoutSeconds" to config.chunkAnalysisTimeout),
            error = error
        ) {
            add(
                "{} Model API timeout after {}s",
                LoggerColors.WARNING_ICON,
                config.chunkAnalysisTimeout
            )
        }
    }

    fun reportLLMResponse(responseBody: String, analysisTime: Duration) {
        EventStore.log(
            source = eventSource,
            type = EventType.LLM,
            level = LogLevel.INFO,
            data = mapOf(
                "responseBody" to responseBody,
                "responseBodyLength" to responseBody.length,
                "analysisTimeSeconds" to analysisTime.toSecondsString()
            )
        ) {
            add("              {} Received LLM response ({}s)", LoggerColors.RESPONSE_ICON, LoggerColors.yellow(analysisTime.toSecondsString()))
        }
    }

    fun reportJsonParse(content: String) {
        EventStore.log(
            source = eventSource,
            type = EventType.JSON,
            level = LogLevel.INFO,
            data = mapOf(
                "content" to content,
                "contentLength" to content.length
            )
        ) {
            add("              {} Extracted JSON content ({} chars)", LoggerColors.JSON_ICON, LoggerColors.cyan(content.length.toString()))
        }
    }

    fun reportJsonParseError(rawResponse: String, error: Throwable) {
        EventStore.log(
            source = eventSource,
            type = EventType.JSON,
            level = LogLevel.ERROR,
            data = mapOf(
                "rawResponse" to rawResponse,
                "rawResponseLength" to rawResponse.length
            ),
            error = error
        ) {
            add("             {} JSON parse error", LoggerColors.ERROR_ICON)
        }
    }

    fun reportFileIssues(file: File, issues: List<Issue>, analysisTime: Duration, fileIndex: Int, totalFiles: Int) {
        EventStore.log(
            source = eventSource,
            type = EventType.ANALYSE,
            level = LogLevel.INFO,
            data = mapOf(
                "file" to file,
                "issues" to issues,
                "analysisTimeSeconds" to analysisTime.toSecondsString(),
                "fileIndex" to fileIndex,
                "totalFiles" to totalFiles
            )
        ) {
            add(
                "       Found {} {} (analyzed in {}s)",
                LoggerColors.boldYellow(issues.size.toString()),
                LoggerColors.cyan("issues"),
                LoggerColors.yellow(analysisTime.toSecondsString())
            )

            issues.forEachIndexed { index, issue ->
                val secretDisplay = truncateSecret(issue.secretValue)

                add(
                    "       {} #{} | Line {} | {}",
                    LoggerColors.ISSUE_ICON,
                    LoggerColors.yellow((index + 1).toString()),
                    LoggerColors.blue(issue.lineNumber.toString()),
                    secretDisplay
                )
            }
        }
    }

    fun reportScanComplete(fileScanResults: List<FileScanResult>, totalFiles: Int, totalTime: Duration) {
        EventStore.log(
            source = eventSource,
            type = EventType.SCANNER,
            level = LogLevel.INFO,
            data = mapOf(
                "totalIssues" to fileScanResults.sumOf { it.issues.size },
                "filesAnalyzed" to fileScanResults.size,
                "totalFiles" to totalFiles,
                "totalTimeSeconds" to totalTime.toSecondsString()
            )
        ) {
            addEmpty()
            add(
                "{} {} Analyzed {} of {} file, found {} total issues in {}s",
                LoggerColors.SUCCESS_ICON,
                LoggerColors.boldGreen("Scan completed successfully."),
                LoggerColors.boldCyan(fileScanResults.size.toString()),
                LoggerColors.boldYellow(totalFiles.toString()),
                LoggerColors.boldYellow(fileScanResults.sumOf { it.issues.size }.toString()),
                LoggerColors.yellow(totalTime.toSecondsString())
            )
        }
    }

}