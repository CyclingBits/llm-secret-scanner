package net.cyclingbits.llmsecretscanner.evaluator.model

import net.cyclingbits.llmsecretscanner.core.model.Issue

data class DetectionResults(
    val correctCount: Int,
    val detectedIssues: List<Issue>,
    val incorrectIssues: List<Issue>,
    val falsePositiveIssues: List<Issue>
)