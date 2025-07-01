package net.cyclingbits.llmsecretscanner.evaluator.service

import net.cyclingbits.llmsecretscanner.core.model.FileScanResult
import net.cyclingbits.llmsecretscanner.core.model.Issue
import net.cyclingbits.llmsecretscanner.evaluator.logger.EvaluatorLogger
import net.cyclingbits.llmsecretscanner.evaluator.model.DetectionMetrics
import net.cyclingbits.llmsecretscanner.evaluator.model.DetectionResults
import java.io.File
import kotlin.math.abs

class DetectionRateCalculator(
    private val logger: EvaluatorLogger
) {

    fun calculate(positiveFileScanResult: List<FileScanResult>, expectedPositiveFileScanResult: List<FileScanResult>, negativeFileScanResult: List<FileScanResult>): DetectionMetrics {

        val detectionResults = analyzeDetections(positiveFileScanResult, expectedPositiveFileScanResult, negativeFileScanResult)

        val detectionRate = calculateDetectionRate(
            correctCount = detectionResults.correctIssues.size,
            totalExpected = expectedPositiveFileScanResult.sumOf { it.issues.size })

        val negativeFilesLines = countLinesInFiles(negativeFileScanResult.map { it.file })
        val falsePositiveRate = calculateFalsePositiveRate(detectionResults.falsePositiveIssues.size, negativeFilesLines)

        return DetectionMetrics(detectionRate, falsePositiveRate)
    }

    private fun analyzeDetections(
        positiveFileScanResult: List<FileScanResult>,
        expectedPositiveFileScanResult: List<FileScanResult>,
        negativeFileScanResult: List<FileScanResult>
    ): DetectionResults {
        val incorrectIssues = mutableListOf<Issue>()
        val correctIssues = mutableListOf<Issue>()

        positiveFileScanResult.forEach { fileScanResult ->
            val expectedForFile = expectedPositiveFileScanResult.find { it.file.name == fileScanResult.file.name }

            fileScanResult.issues.forEach { detectedIssue ->
                val matchingExpected = expectedForFile?.issues?.find { expectedIssue ->
                    isMatchingSecret(detectedIssue, expectedIssue)
                }

                if (matchingExpected != null) {
                    correctIssues.add(detectedIssue)
                } else {
                    incorrectIssues.add(detectedIssue)
                }
            }
        }

        val detectionResults = DetectionResults(
            detectedIssues = positiveFileScanResult,
            expectedIssues = expectedPositiveFileScanResult,
            correctIssues = correctIssues,
            incorrectIssues = incorrectIssues,
            falsePositiveIssues = negativeFileScanResult
        )

        logger.reportDetectionResults(detectionResults)

        return detectionResults
    }

    private fun isMatchingSecret(detected: Issue, expected: Issue): Boolean {
        return abs(detected.lineNumber - expected.lineNumber) <= 2 &&
                (detected.secretValue?.take(10) ?: "") == (expected.secretValue?.take(10) ?: "")
    }

    private fun calculateDetectionRate(correctCount: Int, totalExpected: Int): Double {
        return if (totalExpected > 0) {
            (correctCount.toDouble() / totalExpected) * 100
        } else 0.0
    }

    private fun calculateFalsePositiveRate(falsePositiveCount: Int, negativeFilesLines: Int): Double {
        return if (negativeFilesLines > 0) {
            (falsePositiveCount.toDouble() / negativeFilesLines) * 100
        } else 100.0
    }

    private fun countLinesInFiles(files: List<File>): Int {
        return files.sumOf { file ->
            try {
                file.readLines().size
            } catch (e: Exception) {
                logger.reportWarning("Failed to count lines in ${file.name}: ${e.message}")
                0
            }
        }
    }

}