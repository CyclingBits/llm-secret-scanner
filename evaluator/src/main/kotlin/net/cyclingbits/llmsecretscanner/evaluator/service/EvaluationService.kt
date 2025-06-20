package net.cyclingbits.llmsecretscanner.evaluator.service

import net.cyclingbits.llmsecretscanner.core.Scanner
import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.service.FileScanner
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration
import net.cyclingbits.llmsecretscanner.evaluator.model.EvaluationResult
import java.io.File

object EvaluationService {

    fun evaluateModels(models: List<String>, includes: String, fileAnalysisTimeout: Int): List<EvaluationResult> {
        return models.map { model -> 
            val result = evaluateModel(model, includes, fileAnalysisTimeout)
            ResultsSaver.saveResultsToMarkdown(listOf(result))
            result
        }
    }

    private fun evaluateModel(modelName: String, includes: String, fileAnalysisTimeout: Int): EvaluationResult {
        val config = createConfiguration(modelName, includes, fileAnalysisTimeout)
        val fileScanner = FileScanner(config).findFiles()

        val startTime = System.currentTimeMillis()

        val scanResult = Scanner(config).use { scanner ->
            scanner.executeScan(fileScanner)
        }

        val detectionRate = DetectionRateCalculator.calculate(scanResult.issues)
        val scanSuccessRate = if (scanResult.totalFiles > 0) {
            scanResult.filesAnalyzed.toDouble() / scanResult.totalFiles.toDouble() * 100.0
        } else {
            0.0
        }

        return EvaluationResult(
            modelName = modelName,
            detectionRate = detectionRate,
            scanSuccessRate = scanSuccessRate,
            scanTime = System.currentTimeMillis() - startTime
        )
    }

    private fun createConfiguration(modelName: String, includes: String, fileAnalysisTimeout: Int) = ScannerConfiguration(
        sourceDirectory = File(EvaluatorConfiguration.FIXTURES_DIR),
        includes = includes,
        modelName = modelName,
        fileAnalysisTimeout = fileAnalysisTimeout
    )
}