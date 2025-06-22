package net.cyclingbits.llmsecretscanner.evaluator.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.cyclingbits.llmsecretscanner.core.model.Issue
import net.cyclingbits.llmsecretscanner.core.util.ScanReporter
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration
import net.cyclingbits.llmsecretscanner.evaluator.model.DetectionMetrics
import net.cyclingbits.llmsecretscanner.evaluator.model.DetectionResults
import java.io.File
import kotlin.math.abs

object DetectionRateCalculator {

    private val objectMapper = ObjectMapper().registerKotlinModule()

    fun calculate(positiveIssues: List<Issue>, negativeIssues: List<Issue>, negativeFiles: List<File>): DetectionMetrics {
        val expectedIssues = loadExpectedIssues()
        val scannedFiles = extractScannedFiles(positiveIssues)
        val relevantExpected = filterRelevantExpected(expectedIssues, scannedFiles)
        
        val detectionResults = analyzeDetections(positiveIssues, relevantExpected, negativeIssues)
        
        logAllResults(detectionResults, relevantExpected)
        
        val detectionRate = calculateDetectionRate(detectionResults.correctCount, relevantExpected.size)
        val falsePositiveRate = calculateFalsePositiveRate(detectionResults.falsePositiveIssues.size, negativeFiles)
        
        return DetectionMetrics(detectionRate, falsePositiveRate)
    }


    private fun extractScannedFiles(issues: List<Issue>): Set<String> {
        return issues.map { it.filePath.substringAfterLast("/") }.toSet()
    }

    private fun filterRelevantExpected(expectedIssues: List<Issue>, scannedFiles: Set<String>): List<Issue> {
        return expectedIssues.filter { it.filePath in scannedFiles }
    }

    private fun analyzeDetections(issues: List<Issue>, relevantExpected: List<Issue>, negativeIssues: List<Issue>): DetectionResults {
        val detectedIssues = mutableListOf<Issue>()
        val incorrectIssues = mutableListOf<Issue>()
        val matchedExpected = mutableSetOf<Issue>()

        issues.forEach { detected ->
            val matchingExpected = relevantExpected.find { expected ->
                isMatchingSecret(detected, expected)
            }
            if (matchingExpected != null) {
                detectedIssues.add(detected)
                matchedExpected.add(matchingExpected)
            } else {
                incorrectIssues.add(detected)
            }
        }

        return DetectionResults(matchedExpected.size, detectedIssues, incorrectIssues, negativeIssues)
    }

    private fun isMatchingSecret(detected: Issue, expected: Issue): Boolean {
        return detected.filePath.substringAfterLast("/") == expected.filePath &&
                abs(detected.lineNumber - expected.lineNumber) <= 2 &&
                (detected.secretValue?.take(10) ?: "") == (expected.secretValue?.take(10) ?: "")
    }

    private fun logAllResults(detectionResults: DetectionResults, relevantExpected: List<Issue>) {
        val missedSecrets = relevantExpected.filter { expected ->
            detectionResults.detectedIssues.none { detected -> isMatchingSecret(detected, expected) }
        }
        
        ScanReporter.reportDetectionResults(detectionResults.detectedIssues.size, detectionResults.incorrectIssues.size, missedSecrets.size)
        
        if (detectionResults.falsePositiveIssues.isNotEmpty()) {
            ScanReporter.reportFalsePositives(detectionResults.falsePositiveIssues.size)
        }
        
        detectionResults.detectedIssues.forEach { issue ->
            ScanReporter.reportCorrectDetection(issue)
        }
        detectionResults.incorrectIssues.forEach { issue ->
            ScanReporter.reportIncorrectDetection(issue)
        }
        
        if (missedSecrets.isNotEmpty()) {
            missedSecrets.forEach { missed ->
                ScanReporter.reportMissedSecret(missed)
            }
        }
    }

    private fun calculateDetectionRate(correctCount: Int, totalExpected: Int): Double {
        return if (totalExpected > 0) {
            (correctCount.toDouble() / totalExpected) * 100
        } else 0.0
    }

    private fun calculateFalsePositiveRate(falsePositiveCount: Int, negativeFiles: List<File>): Double {
        val totalLines = countLinesInFiles(negativeFiles)
        return if (totalLines > 0) {
            (falsePositiveCount.toDouble() / totalLines) * 100
        } else 100.0
    }

    private fun countLinesInFiles(files: List<File>): Int {
        return files.sumOf { file ->
            try {
                file.readLines().size
            } catch (e: Exception) {
                ScanReporter.reportWarning("Failed to count lines in ${file.name}: ${e.message}")
                0
            }
        }
    }


    private fun loadExpectedIssues(): List<Issue> {
        val expectedResultsDir = File(EvaluatorConfiguration.EXPECTED_RESULTS_DIR)
        if (!expectedResultsDir.exists()) return emptyList()

        return expectedResultsDir.listFiles { it.extension == "json" }
            ?.flatMap { file ->
                try {
                    objectMapper.readValue(file, Array<Issue>::class.java).toList()
                } catch (e: Exception) {
                    ScanReporter.reportWarning("Failed to load ${file.name}: ${e.message}")
                    emptyList()
                }
            } ?: emptyList()
    }
}