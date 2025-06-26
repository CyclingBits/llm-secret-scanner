package net.cyclingbits.llmsecretscanner.core.model

data class ScanResult(
    val fileScanResults: List<FileScanResult>
) {
    val allIssues: List<Issue> get() = fileScanResults.flatMap { it.issues }
    val hasIssues: Boolean get() = fileScanResults.any { it.issues.isNotEmpty() }
    val totalIssues: Int get() = fileScanResults.sumOf { it.issues.size }
    val filesAnalyzed: Int get() = fileScanResults.size
}