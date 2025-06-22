package net.cyclingbits.llmsecretscanner.evaluator.service

import net.cyclingbits.llmsecretscanner.core.Scanner
import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.model.ScanResult
import net.cyclingbits.llmsecretscanner.core.files.FileFinder
import net.cyclingbits.llmsecretscanner.core.util.ScanReporter
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration
import net.cyclingbits.llmsecretscanner.evaluator.model.EvaluationResult
import java.io.File

class EvaluationService(private val config: ScannerConfiguration) {
    
    private val scanner = Scanner(config)

    fun evaluateModel(): EvaluationResult {
        val startTime = System.currentTimeMillis()
        
        val positiveResults = evaluateTestCases(EvaluatorConfiguration.POSITIVE_CASES_DIR)
        val negativeResults = evaluateTestCases(EvaluatorConfiguration.NEGATIVE_CASES_DIR)
        
        val negativeFiles = FileFinder(config.copy(sourceDirectories = listOf(File(EvaluatorConfiguration.NEGATIVE_CASES_DIR)))).findFiles(File(EvaluatorConfiguration.NEGATIVE_CASES_DIR))
        val detectionMetrics = DetectionRateCalculator.calculate(positiveResults.issues, negativeResults.issues, negativeFiles)
        val totalFiles = positiveResults.totalFiles + negativeResults.totalFiles
        val filesAnalyzed = positiveResults.filesAnalyzed + negativeResults.filesAnalyzed
        
        val scanSuccessRate = if (totalFiles > 0) {
            filesAnalyzed.toDouble() / totalFiles.toDouble() * 100.0
        } else {
            0.0
        }

        return EvaluationResult(
            modelName = config.modelName,
            detectionRate = detectionMetrics.detectionRate,
            falsePositiveRate = detectionMetrics.falsePositiveRate,
            scanSuccessRate = scanSuccessRate,
            scanTime = System.currentTimeMillis() - startTime
        )
    }

    private fun evaluateTestCases(sourceDir: String): ScanResult {
        val testConfig = config.copy(sourceDirectories = listOf(File(sourceDir)))
        val directory = File(sourceDir)
        val files = FileFinder(testConfig).findFiles(directory)
        
        ScanReporter.reportAnalysisStartForDirectory(directory, files.size)
        return scanner.executeScan(files)
    }
}