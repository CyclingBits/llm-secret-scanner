package net.cyclingbits.llmsecretscanner.evaluator.model

data class ExpectedIssue(
    val filePath: String,
    val issueNumber: Int,
    val lineNumber: Int,
    val secretValue: String
)