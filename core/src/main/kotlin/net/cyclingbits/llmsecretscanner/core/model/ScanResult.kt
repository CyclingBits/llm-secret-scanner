package net.cyclingbits.llmsecretscanner.core.model

data class ScanResult(
    val issues: List<Issue>,
    val filesAnalyzed: Int,
    val totalFiles: Int
)