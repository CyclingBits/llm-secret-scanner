package net.cyclingbits.llmsecretscanner.evaluator.service

import net.cyclingbits.llmsecretscanner.core.Scanner
import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration
import net.cyclingbits.llmsecretscanner.evaluator.model.EvaluationResult
import java.io.File

object EvaluationService {

    fun evaluateModels(models: List<String>, includes: String): List<EvaluationResult> {
        return models.map { model -> evaluateModel(model, includes) }
    }

    private fun evaluateModel(modelName: String, includes: String): EvaluationResult {
        val config = createConfiguration(modelName, includes)
        
        return Scanner(config).use { scanner ->
            val startTime = System.currentTimeMillis()
            val scanResult = scanner.executeScan()
            val endTime = System.currentTimeMillis()
            val scanTime = endTime - startTime

            val detectionRate = DetectionRateCalculator.calculate(scanResult)

            EvaluationResult(
                modelName = modelName,
                detectionRate = detectionRate,
                scanTime = scanTime
            )
        }
    }

    private fun createConfiguration(modelName: String, includes: String) = ScannerConfiguration(
        sourceDirectory = File(EvaluatorConfiguration.FIXTURES_DIR),
        includes = includes,
        modelName = modelName
    )
}