package net.cyclingbits.llmsecretscanner.evaluator.model

import net.cyclingbits.llmsecretscanner.core.model.FileScanResult
import net.cyclingbits.llmsecretscanner.core.model.Issue

data class DetectionResults(
    val detectedIssues: List<FileScanResult>,
    val expectedIssues: List<FileScanResult>,

    val correctIssues: List<Issue>,
    val incorrectIssues: List<Issue>,

    val falsePositiveIssues: List<Issue>
)