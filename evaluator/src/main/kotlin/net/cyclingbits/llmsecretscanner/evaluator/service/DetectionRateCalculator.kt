package net.cyclingbits.llmsecretscanner.evaluator.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.cyclingbits.llmsecretscanner.core.model.Issue
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration
import java.io.File
import kotlin.math.abs

object DetectionRateCalculator {

    private val objectMapper = ObjectMapper().registerKotlinModule()

    fun calculate(issues: List<Issue>): Double {
        val expectedIssues = loadExpectedIssues()
        val scannedFiles = issues.map { it.filePath.substringAfterLast("/") }.toSet()
        val relevantExpected = expectedIssues.filter { it.filePath in scannedFiles }
        
        val correctlyDetected = issues.count { detected ->
            relevantExpected.any { expected ->
                detected.filePath.substringAfterLast("/") == expected.filePath &&
                abs(detected.lineNumber - expected.lineNumber) <= 1
            }
        }
        
        return if (relevantExpected.isNotEmpty()) {
            (correctlyDetected.toDouble() / relevantExpected.size) * 100
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
                    println("Warning: Failed to load ${file.name}: ${e.message}")
                    emptyList()
                }
            } ?: emptyList()
    }
}