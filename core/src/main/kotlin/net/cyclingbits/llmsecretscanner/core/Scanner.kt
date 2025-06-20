package net.cyclingbits.llmsecretscanner.core

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.exception.ScannerException
import net.cyclingbits.llmsecretscanner.core.model.Issue
import net.cyclingbits.llmsecretscanner.core.model.ScanResult
import net.cyclingbits.llmsecretscanner.core.service.CodeAnalyzer
import net.cyclingbits.llmsecretscanner.core.service.ContainerManager
import net.cyclingbits.llmsecretscanner.core.service.ScanReporter
import java.io.File

/**
 * Main scanner orchestrator that coordinates file discovery, container management,
 * code analysis, and progress reporting.
 */
class Scanner(config: ScannerConfiguration) {

    private val container = ContainerManager(config).startContainer()
    private val codeAnalyzer = CodeAnalyzer(config, container)

    fun executeScan(filesToScan: List<File>): ScanResult {
        val analysisStartTime = System.currentTimeMillis()

        ScanReporter.reportAnalysisStart(filesToScan.size)

        val results = filesToScan.mapIndexed { fileIndex, file ->
            scanFile(file, fileIndex + 1, filesToScan.size)
        }
        
        val issues = results.mapNotNull { it.getOrNull() }.flatten()
        val filesAnalyzed = results.count { it.isSuccess }

        ScanReporter.reportScanComplete(issues.size, filesAnalyzed, filesToScan.size, System.currentTimeMillis() - analysisStartTime)

        return ScanResult(issues, filesAnalyzed, filesToScan.size)
    }

    private fun scanFile(file: File, fileIndex: Int, totalFiles: Int): Result<List<Issue>> {
        return try {
            Result.success(codeAnalyzer.analyzeFile(file, fileIndex, totalFiles))
        } catch (e: ScannerException) {
            ScanReporter.reportError("Error analyzing file ${file.name}: ${e.message}")
            Result.failure(RuntimeException(e.message))
        }
    }
}