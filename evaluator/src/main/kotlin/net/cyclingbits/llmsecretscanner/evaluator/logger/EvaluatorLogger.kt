package net.cyclingbits.llmsecretscanner.evaluator.logger

import net.cyclingbits.llmsecretscanner.core.event.EventType
import net.cyclingbits.llmsecretscanner.core.event.LogLevel
import net.cyclingbits.llmsecretscanner.core.logger.EventStore
import net.cyclingbits.llmsecretscanner.core.logger.LoggerColors
import net.cyclingbits.llmsecretscanner.core.logger.ScanLogger
import net.cyclingbits.llmsecretscanner.evaluator.model.DetectionResults

class EvaluatorLogger : ScanLogger() {

    fun reportEvaluationStart() {
        EventStore.log(
            {
                addEmpty()
                add("{} {}", LoggerColors.SCANNER_ICON, LoggerColors.boldCyan("Starting model evaluation"))
            },
            {
                type(EventType.SCANER)
                level(LogLevel.INFO)
            }
        )
    }

    fun reportEvaluationComplete(totalTimeSeconds: Long) {
        val minutes = totalTimeSeconds / 60
        val seconds = totalTimeSeconds % 60
        val timeDisplay = if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"

        EventStore.log(
            {
                add("{} {} in {}", LoggerColors.SUCCESS_ICON, LoggerColors.boldGreen("Evaluation completed successfully"), LoggerColors.cyan(timeDisplay))
            },
            {
                type(EventType.SCANER)
                level(LogLevel.INFO)
                data("totalTimeSeconds" to totalTimeSeconds)
            }
        )
    }

    fun reportFileSaved(filePath: String) {
        EventStore.log(
            {
                add("{} Results saved to: {}", LoggerColors.SUCCESS_ICON, LoggerColors.boldGreen(filePath))
            },
            {
                type(EventType.FILE)
                level(LogLevel.INFO)
                data("filePath" to filePath)
            }
        )
    }

    fun reportExpectedIssuesLoaded(totalIssues: Int, fileCount: Int) {
        EventStore.log(
            {
                add("{} Loaded {} expected issues for {} files", LoggerColors.INFO_ICON, LoggerColors.cyan(totalIssues.toString()), LoggerColors.cyan(fileCount.toString()))
            },
            {
                type(EventType.FILE)
                level(LogLevel.INFO)
                data(
                    "totalExpectedIssues" to totalIssues,
                    "fileCount" to fileCount
                )
            }
        )
    }

    fun reportDetectionResults(detectionResults: DetectionResults) {
        EventStore.log(
            {
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
            },
            {
                type(EventType.SCANER)
                level(LogLevel.INFO)
                data(
                    "detectedIssues" to detectionResults.detectedIssues,
                    "expectedIssues" to detectionResults.expectedIssues,
                    "correctIssues" to detectionResults.correctIssues,
                    "incorrectIssues" to detectionResults.incorrectIssues,
                    "falsePositives" to detectionResults.falsePositiveIssues
                )
            }
        )
    }
}