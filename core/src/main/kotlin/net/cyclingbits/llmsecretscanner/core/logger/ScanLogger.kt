package net.cyclingbits.llmsecretscanner.core.logger

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.event.EventType
import net.cyclingbits.llmsecretscanner.core.event.LogLevel
import net.cyclingbits.llmsecretscanner.core.file.FileChunk
import net.cyclingbits.llmsecretscanner.core.logger.LoggerUtils.getIssueTypeColor
import net.cyclingbits.llmsecretscanner.core.logger.LoggerUtils.getRelativePath
import net.cyclingbits.llmsecretscanner.core.logger.LoggerUtils.toSecondsString
import net.cyclingbits.llmsecretscanner.core.logger.LoggerUtils.truncateSecret
import net.cyclingbits.llmsecretscanner.core.model.FileScanResult
import net.cyclingbits.llmsecretscanner.core.model.Issue
import java.io.File
import java.time.Duration

open class ScanLogger() {

    fun reportScanStart() {
        EventStore.log(
            {
                addEmpty()
                add("{} {}", LoggerColors.SCANNER_ICON, LoggerColors.boldCyan("Starting LLM Secret Scanner"))
            },
            {
                type(EventType.SCANER)
                level(LogLevel.INFO)
            }
        )
    }

    fun reportScanConfiguration(config: ScannerConfiguration) {
        EventStore.log(
            {
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
                add("       {} System prompt: {}", LoggerColors.INFO_ICON, LoggerColors.cyan(config.systemPrompt.take(100) + if (config.systemPrompt.length > 100) "..." else ""))
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
            },
            {
                type(EventType.SCANER)
                level(LogLevel.INFO)
                data("config" to config)
            }
        )
    }

    fun reportContainerStart() {
        EventStore.log(
            {
                addEmpty()
                add("{} Starting Docker container...", LoggerColors.DOCKER_ICON)
            },
            {
                type(EventType.CONTAINER)
                level(LogLevel.INFO)
            }
        )
    }

    fun reportContainerStarted() {
        EventStore.log(
            {
                add("{} Docker container started successfully", LoggerColors.SUCCESS_ICON)
            },
            {
                type(EventType.CONTAINER)
                level(LogLevel.INFO)
            }
        )
    }

    fun reportFilesFound(sourceDirectories: List<File>, filesToScan: List<File>, config: ScannerConfiguration) {
        EventStore.log(
            {
                addEmpty()
                add("{} Found {} file matching patterns in:", LoggerColors.FILE_ICON, LoggerColors.boldYellow(filesToScan.size.toString()))
                sourceDirectories.forEach { dir ->
                    add("    - {}", LoggerColors.blue(dir.absolutePath))
                }
                add("    {} Include patterns: {}", LoggerColors.INCLUDE_ICON, LoggerColors.cyan(config.includes))
                add("    {} Exclude patterns: {}", LoggerColors.EXCLUDE_ICON, LoggerColors.yellow(config.excludes))
            },
            {
                type(EventType.FILE)
                level(LogLevel.INFO)
                data(
                    "sourceDirectories" to sourceDirectories,
                    "filesToScan" to filesToScan,
                    "includes" to config.includes,
                    "excludes" to config.excludes
                )
            }
        )
    }

    fun reportNoFilesFound(config: ScannerConfiguration, sourceDirectories: List<File>) {
        EventStore.log(
            {
                add("{} No file found matching the specified patterns", LoggerColors.WARNING_ICON)
            },
            {
                type(EventType.FILE)
                level(LogLevel.WARN)
                data(
                    "sourceDirectories" to sourceDirectories,
                    "includes" to config.includes,
                    "excludes" to config.excludes
                )
            }
        )
    }

    fun reportAnalysisStartForDirectory(directory: File, fileCount: Int) {
        EventStore.log(
            {
                addEmpty()
                add(
                    "{} Starting analysis of {} file in {}",
                    LoggerColors.SCANNER_ICON,
                    LoggerColors.boldYellow(fileCount.toString()),
                    LoggerColors.blue(directory.absolutePath)
                )
            },
            {
                type(EventType.CONTAINER)
                level(LogLevel.INFO)
                data(
                    "directory" to directory,
                    "fileCount" to fileCount
                )
            }
        )
    }

    fun reportFileAnalysisStart(file: File, fileIndex: Int, totalFiles: Int, config: ScannerConfiguration) {
        EventStore.log(
            {
                addEmpty()
                add(
                    "[{}/{}] Analyzing file {}...",
                    LoggerColors.cyan(fileIndex.toString()),
                    LoggerColors.cyan(totalFiles.toString()),
                    LoggerColors.blue(getRelativePath(file, config.sourceDirectories))
                )
            },
            {
                type(EventType.ANALYSE)
                level(LogLevel.INFO)
                data(
                    "file" to file,
                    "fileIndex" to fileIndex,
                    "totalFiles" to totalFiles
                )
            }
        )
    }

    fun reportChunk(file: File, chunkCount: Int, config: ScannerConfiguration) {
        EventStore.log(
            {
                add(
                    "       {} Chunking file {} into {} chunks ({} lines per chunk, {} overlap)",
                    LoggerColors.CHUNK_ICON,
                    LoggerColors.blue(getRelativePath(file, config.sourceDirectories)),
                    LoggerColors.boldYellow(chunkCount.toString()),
                    LoggerColors.cyan(config.maxLinesPerChunk.toString()),
                    LoggerColors.cyan(config.chunkOverlapLines.toString())
                )
            },
            {
                type(EventType.CHUNK)
                level(LogLevel.INFO)
                data(
                    "file" to file,
                    "chunkCount" to chunkCount,
                    "maxLinesPerChunk" to config.maxLinesPerChunk,
                    "overlapLines" to config.chunkOverlapLines
                )
            }
        )
    }

    fun reportChunkAnalysis(chunk: FileChunk, fileType: String) {
        EventStore.log(
            {
                add(
                    "       {} Analyzing chunk {}/{} (lines {}-{})",
                    LoggerColors.ANALYSIS_ICON,
                    LoggerColors.cyan((chunk.chunkIndex + 1).toString()),
                    LoggerColors.cyan(chunk.totalChunks.toString()),
                    LoggerColors.blue(chunk.startLine.toString()),
                    LoggerColors.blue(chunk.endLine.toString())
                )
            },
            {
                type(EventType.CHUNK)
                level(LogLevel.INFO)
                data(
                    "fileType" to fileType,
                    "chunkIndex" to chunk.chunkIndex,
                    "totalChunks" to chunk.totalChunks,
                    "startLine" to chunk.startLine,
                    "endLine" to chunk.endLine,
                    "chunkContent" to chunk.content.lines(),
                )
            }
        )
    }

    fun reportLLMRequest(parameters: Map<String, Any?>) {
        EventStore.log(
            {
                add("              {} Sending LLM request", LoggerColors.REQUEST_ICON)
            },
            {
                type(EventType.LLM)
                level(LogLevel.DEBUG)
                data(
                    "parameters" to parameters
                )
            }
        )
    }

    fun reportLLMTimeout(config: ScannerConfiguration, error: Throwable) {
        EventStore.log(
            {
                add(
                    "{} Model API timeout after {}s",
                    LoggerColors.WARNING_ICON,
                    config.chunkAnalysisTimeout
                )
            },
            {
                type(EventType.LLM)
                level(LogLevel.ERROR)
                data("timeoutSeconds" to config.chunkAnalysisTimeout)
                error(error)
            }
        )
    }

    fun reportLLMResponse(responseBody: String, analysisTime: Duration) {
        EventStore.log(
            {
                add("              {} Received LLM response ({}s)", LoggerColors.RESPONSE_ICON, LoggerColors.yellow(analysisTime.toSecondsString()))
            },
            {
                type(EventType.LLM)
                level(LogLevel.DEBUG)
                data(
                    "responseBody" to responseBody,
                    "responseBodyLength" to responseBody.length,
                    "analysisTimeSeconds" to analysisTime.toSecondsString()
                )
            }
        )
    }

    fun reportJsonParse(content: String) {
        EventStore.log(
            {
                add("              {} Extracted JSON content ({} chars)", LoggerColors.JSON_ICON, LoggerColors.cyan(content.length.toString()))
            },
            {
                type(EventType.JSON)
                level(LogLevel.DEBUG)
                data(
                    "content" to content,
                    "contentLength" to content.length
                )
            }
        )
    }

    fun reportJsonParseError(rawResponse: String, error: Throwable) {
        EventStore.log(
            {
                add("       {} JSON parse error", LoggerColors.ERROR_ICON)
            },
            {
                type(EventType.JSON)
                level(LogLevel.ERROR)
                data(
                    "rawResponse" to rawResponse,
                    "rawResponseLength" to rawResponse.length
                )
                error(error)
            }
        )
    }

    fun reportFileIssues(file: File, issues: List<Issue>, analysisTime: Duration, fileIndex: Int, totalFiles: Int) {
        EventStore.log(
            {
                add(
                    "       Found {} {} (analyzed in {}s)",
                    LoggerColors.boldYellow(issues.size.toString()),
                    LoggerColors.cyan("issues"),
                    LoggerColors.yellow(analysisTime.toSecondsString())
                )

                issues.forEachIndexed { index, issue ->
                    val issueColor = getIssueTypeColor(issue.issueType)
                    val secretDisplay = truncateSecret(issue.secretValue)

                    add(
                        "       {} #{} | Line {} | {} | {}",
                        LoggerColors.ISSUE_ICON,
                        LoggerColors.yellow((index + 1).toString()),
                        LoggerColors.blue(issue.lineNumber.toString()),
                        issueColor,
                        secretDisplay
                    )
                }
            },
            {
                type(EventType.ANALYSE)
                level(LogLevel.INFO)
                data(
                    "file" to file,
                    "issues" to issues,
                    "analysisTimeSeconds" to analysisTime.toSecondsString(),
                    "fileIndex" to fileIndex,
                    "totalFiles" to totalFiles
                )
            }
        )
    }

    fun reportScanComplete(fileScanResults: List<FileScanResult>, totalFiles: Int, totalTime: Duration) {
        EventStore.log(
            {
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
            },
            {
                type(EventType.SCANER)
                level(LogLevel.INFO)
                data(
                    "totalIssues" to fileScanResults.sumOf { it.issues.size },
                    "filesAnalyzed" to fileScanResults.size,
                    "totalFiles" to totalFiles,
                    "totalTimeSeconds" to totalTime.toSecondsString()
                )
            }
        )
    }

    fun reportWarning(message: String, data: Any? = null) {
        EventStore.log(
            {
                add("       {} {}", LoggerColors.WARNING_ICON, LoggerColors.yellow(message))
            },
            {
                type(EventType.SCANER)
                level(LogLevel.WARN)
                data("message" to message, "data" to data)
            }
        )
    }

    fun reportError(message: String, exception: Throwable? = null) {
        EventStore.log(
            {
                add("{} {}", LoggerColors.ERROR_ICON, LoggerColors.boldRed(message))
            },
            {
                type(EventType.SCANER)
                level(LogLevel.ERROR)
                data("message" to message)
                exception?.let { error(it) }
            }
        )
    }
}