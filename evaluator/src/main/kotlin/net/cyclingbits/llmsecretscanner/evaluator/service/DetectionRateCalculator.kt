package net.cyclingbits.llmsecretscanner.evaluator.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.cyclingbits.llmsecretscanner.core.model.Issue
import net.cyclingbits.llmsecretscanner.core.service.ScanReporter
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration
import java.io.File
import kotlin.math.abs

object DetectionRateCalculator {

    private val objectMapper = ObjectMapper().registerKotlinModule()

    fun calculate(issues: List<Issue>): Double {
        val expectedIssues = loadExpectedIssues()
        val scannedFiles = extractScannedFiles(issues)
        val relevantExpected = filterRelevantExpected(expectedIssues, scannedFiles)
        
        val detectionResults = analyzeDetections(issues, relevantExpected)
        logAllResults(detectionResults, relevantExpected)
        
        return calculateDetectionRate(detectionResults.correctCount, relevantExpected.size)
    }

    private data class DetectionResults(
        val correctCount: Int,
        val detectedIssues: List<Issue>,
        val incorrectIssues: List<Issue>
    )

    private fun extractScannedFiles(issues: List<Issue>): Set<String> {
        return issues.map { it.filePath.substringAfterLast("/") }.toSet()
    }

    private fun filterRelevantExpected(expectedIssues: List<Issue>, scannedFiles: Set<String>): List<Issue> {
        return expectedIssues.filter { it.filePath in scannedFiles }
    }

    private fun analyzeDetections(issues: List<Issue>, relevantExpected: List<Issue>): DetectionResults {
        val detectedIssues = mutableListOf<Issue>()
        val incorrectIssues = mutableListOf<Issue>()
        var correctCount = 0

        issues.forEach { detected ->
            val found = relevantExpected.any { expected ->
                isMatchingSecret(detected, expected)
            }
            if (found) {
                detectedIssues.add(detected)
                correctCount++
            } else {
                incorrectIssues.add(detected)
            }
        }

        return DetectionResults(correctCount, detectedIssues, incorrectIssues)
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