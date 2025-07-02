package net.cyclingbits.llmsecretscanner.evaluator.logger

import net.cyclingbits.llmsecretscanner.core.logger.ScanLogger
import net.cyclingbits.llmsecretscanner.events.EventStore
import net.cyclingbits.llmsecretscanner.events.EventType
import net.cyclingbits.llmsecretscanner.events.LogLevel
import net.cyclingbits.llmsecretscanner.events.LoggerColors
import net.cyclingbits.llmsecretscanner.evaluator.model.DetectionResults

class EvaluatorLogger : ScanLogger() {

    fun reportEvaluationStart() {
        EventStore.log(
            type = EventType.SCANNER,
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
            type = EventType.SCANNER,
            level = LogLevel.INFO,
            data = mapOf("totalTimeSeconds" to totalTimeSeconds)
        ) {
            add("{} {} in {}", LoggerColors.SUCCESS_ICON, LoggerColors.boldGreen("Evaluation completed successfully"), LoggerColors.cyan(timeDisplay))
        }
    }

    fun reportFileSaved(filePath: String) {
        EventStore.log(
            type = EventType.FILE,
            level = LogLevel.INFO,
            data = mapOf("filePath" to filePath)
        ) {
            add("{} Results saved to: {}", LoggerColors.SUCCESS_ICON, LoggerColors.boldGreen(filePath))
        }
    }

    fun reportExpectedIssuesLoaded(totalIssues: Int, fileCount: Int) {
        EventStore.log(
            type = EventType.FILE,
            level = LogLevel.INFO,
            data = mapOf(
                "totalExpectedIssues" to totalIssues,
                "fileCount" to fileCount
            )
        ) {
            add("{} Loaded {} expected issues for {} files", LoggerColors.INFO_ICON, LoggerColors.cyan(totalIssues.toString()), LoggerColors.cyan(fileCount.toString()))
        }
    }

    fun reportDetectionResults(detectionResults: DetectionResults) {
        EventStore.log(
            type = EventType.SCANNER,
            level = LogLevel.INFO,
            data = mapOf(
                "detectedIssues" to detectionResults.detectedIssues,
                "expectedIssues" to detectionResults.expectedIssues,
                "correctIssues" to detectionResults.correctIssues,
                "incorrectIssues" to detectionResults.incorrectIssues,
                "falsePositives" to detectionResults.falsePositiveIssues
            )
        ) {
            addEmpty()
            add(
                "{} Detection analysis: {} expected, {} detected, {} correct, {} incorrect, {} false positives",
                LoggerColors.SUCCESS_ICON,
                LoggerColors.boldCyan(detectionResults.expectedIssues.sumOf { it.issues.size }.toString()),
                LoggerColors.boldCyan(detectionResults.detectedIssues.sumOf { it.issues.size }.toString()),
                LoggerColors.boldGreen(detectionResults.correctIssues.size.toString()),
                LoggerColors.boldYellow(detectionResults.incorrectIssues.size.toString()),
                LoggerColors.boldRed(detectionResults.falsePositiveIssues.sumOf { it.issues.size }.toString())
            )
        }
    }
}