package net.cyclingbits.llmsecretscanner.evaluator.logger

import net.cyclingbits.llmsecretscanner.evaluator.model.DetectionResults
import net.cyclingbits.llmsecretscanner.events.*
import java.io.File

class EvaluatorLogger(override val eventSource: EventSource = EventSource.EVALUATOR) : Logger {

    fun reportEvaluationStart() {
        EventStore.log(
            source = eventSource,
            type = EventType.EVALUATION_START,
            level = LogLevel.INFO
        ) {
            addEmpty()
            add("{} {}", LoggerColors.SCANNER_ICON, LoggerColors.boldCyan("Starting model evaluation"))
        }
    }

    fun reportEvaluationComplete(totalTimeSeconds: Long) {
        val minutes = totalTimeSeconds / 60
        val seconds = totalTimeSeconds % 60
        val timeDisplay = if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"

        EventStore.log(
            source = eventSource,
            type = EventType.EVALUATION_COMPLETE,
            level = LogLevel.INFO,
            data = mapOf("totalTimeSeconds" to totalTimeSeconds)
        ) {
            add("{} {} in {}", LoggerColors.SUCCESS_ICON, LoggerColors.boldGreen("Evaluation completed successfully"), LoggerColors.cyan(timeDisplay))
        }
    }

    fun reportFileSaved(filePath: String) {
        EventStore.log(
            source = eventSource,
            type = EventType.RESULTS_SAVED,
            level = LogLevel.DEBUG,
            data = mapOf("filePath" to filePath)
        ) {
            add("   Results saved to: {}", LoggerColors.boldGreen(filePath))
        }
    }

    fun reportExpectedIssuesLoaded(totalIssues: Int, fileCount: Int) {
        EventStore.log(
            source = eventSource,
            type = EventType.DATA_LOADED,
            level = LogLevel.DEBUG,
            data = mapOf(
                "totalExpectedIssues" to totalIssues,
                "fileCount" to fileCount
            )
        ) {
            addEmpty()
            add(
                "{} Loaded {} expected issues for {} files", LoggerColors.INFO_ICON, LoggerColors.cyan(totalIssues.toString()),
                LoggerColors.cyan(fileCount.toString())
            )
        }
    }

    fun reportDetectionResults(detectionResults: DetectionResults) {
        EventStore.log(
            source = eventSource,
            type = EventType.DETECTION_ANALYSIS,
            level = LogLevel.INFO,
            data = mapOf(
                "detectedIssues" to detectionResults.detectedIssues,
                "expectedIssues" to detectionResults.expectedIssues,
                "correctIssues" to detectionResults.correctIssues,
                "incorrectIssues" to detectionResults.incorrectIssues,
                "falsePositives" to detectionResults.falsePositiveIssues,
                "errors" to EventStore.getAll().filter { it.level in listOf(LogLevel.ERROR, LogLevel.WARN) }.joinToString { it.messages.joinToString { it } }
            )
        ) {
            addEmpty()
            add(
                "{} Detection analysis: {} expected, {} detected, {} correct, {} incorrect, {} false positives, {} errors",
                LoggerColors.SUCCESS_ICON,
                LoggerColors.boldCyan(detectionResults.expectedIssues.sumOf { it.issues.size }.toString()),
                LoggerColors.boldCyan(detectionResults.detectedIssues.sumOf { it.issues.size }.toString()),
                LoggerColors.boldGreen(detectionResults.correctIssues.size.toString()),
                LoggerColors.boldYellow(detectionResults.incorrectIssues.size.toString()),
                LoggerColors.boldRed(detectionResults.falsePositiveIssues.size.toString()),
                LoggerColors.boldRed(EventStore.getAll().count { it.level in listOf(LogLevel.ERROR, LogLevel.WARN) }.toString())
            )
        }
    }

    fun reportAnalysisStartForDirectory(directory: File, fileCount: Int) {
        EventStore.log(
            source = eventSource,
            type = EventType.CONTAINER,
            level = LogLevel.INFO,
            data = mapOf(
                "directory" to directory,
                "fileCount" to fileCount
            )
        ) {
            addEmpty()
            add(
                "{} Starting analysis of {} file in {}",
                LoggerColors.SCANNER_ICON,
                LoggerColors.boldYellow(fileCount.toString()),
                LoggerColors.blue(directory.path)
            )
        }
    }
}