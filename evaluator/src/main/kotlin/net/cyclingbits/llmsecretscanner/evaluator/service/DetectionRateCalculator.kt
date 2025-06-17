package net.cyclingbits.llmsecretscanner.evaluator.service

import net.cyclingbits.llmsecretscanner.core.model.Issue

object DetectionRateCalculator {

    fun calculate(issues: List<Issue>): Double {
        var totalExpected = 0

        // Group issues by file path to avoid counting expected issues multiple times per file
        val issuesByFile = issues.groupBy { it.filePath }
        
        issuesByFile.keys.forEach { filePath ->
            val expectedCount = extractExpectedIssueCount(filePath)
            if (expectedCount != null) {
                totalExpected += expectedCount
            }
        }

        return if (totalExpected > 0) {
            (issues.size.toDouble() / totalExpected) * 100
        } else 0.0
    }

    private fun extractExpectedIssueCount(fileName: String): Int? {
        val regex = """.*_(\d+)\.\w+$""".toRegex()
        return regex.find(fileName)?.groupValues?.get(1)?.toIntOrNull()
    }
}