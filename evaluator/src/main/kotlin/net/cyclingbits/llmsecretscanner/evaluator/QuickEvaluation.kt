package net.cyclingbits.llmsecretscanner.evaluator

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.util.ScanReporter
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration
import net.cyclingbits.llmsecretscanner.evaluator.service.EvaluationService
import net.cyclingbits.llmsecretscanner.evaluator.service.ResultsSaver
import java.io.File

fun main() {
    ScanReporter.reportEvaluationStart("🚀 Quick evaluation: Java files only, single model")
    val config = ScannerConfiguration(
        sourceDirectories = listOf(
            File(EvaluatorConfiguration.POSITIVE_CASES_DIR),
            File(EvaluatorConfiguration.NEGATIVE_CASES_DIR)
        ),
        modelName = "ai/phi4:latest",
        includes = "**/*.java",
        excludes = "**/expected/**",
        chunkAnalysisTimeout = 120
    )
    val evaluationService = EvaluationService(config)
    val result = evaluationService.evaluateModel()
    ResultsSaver.saveResultsToMarkdown(listOf(result))
}