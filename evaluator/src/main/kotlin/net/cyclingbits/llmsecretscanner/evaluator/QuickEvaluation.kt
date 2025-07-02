package net.cyclingbits.llmsecretscanner.evaluator

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration.FALSE_POSITIVE_CASES_DIR
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration.POSITIVE_CASES_DIR
import net.cyclingbits.llmsecretscanner.evaluator.logger.EvaluatorLogger
import net.cyclingbits.llmsecretscanner.evaluator.service.EvaluationService
import net.cyclingbits.llmsecretscanner.evaluator.service.ResultsSaver

fun main() {

    val logger = EvaluatorLogger().also { it.reportEvaluationStart() }

    val config = ScannerConfiguration(
        sourceDirectories = listOf(POSITIVE_CASES_DIR, FALSE_POSITIVE_CASES_DIR),
        modelName = "ai/phi4:latest",
        includes = "**/*.java",
        excludes = "**/expected/**",
        chunkAnalysisTimeout = 120
    )
    val evaluationService = EvaluationService(config, logger)
    val result = evaluationService.evaluateModel()

    ResultsSaver(logger).saveResultsToMarkdown(listOf(result))

    logger.reportEvaluationComplete(result.scanTime / 1000)
}