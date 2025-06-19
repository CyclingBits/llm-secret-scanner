package net.cyclingbits.llmsecretscanner.evaluator.service

import net.cyclingbits.llmsecretscanner.core.Scanner
import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.service.FileScanner
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration
import net.cyclingbits.llmsecretscanner.evaluator.model.EvaluationResult
import java.io.File

object EvaluationService {

    fun evaluateModels(models: List<String>, includes: String, fileAnalysisTimeout: Int): List<EvaluationResult> {
        return models.map { model -> evaluateModel(model, includes, fileAnalysisTimeout) }
    }

    private fun evaluateModel(modelName: String, includes: String, fileAnalysisTimeout: Int): EvaluationResult {
        val config = createConfiguration(modelName, includes, fileAnalysisTimeout)
        val scanner = Scanner(config)
        val fileScanner = FileScanner(config).findFiles()

        val startTime = System.currentTimeMillis()

        val scanResult = scanner.executeScan(fileScanner)

        val detectionRate = DetectionRateCalculator.calculate(scanResult)

        return EvaluationResult(
            modelName = modelName,
            detectionRate = detectionRate,
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