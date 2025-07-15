package net.cyclingbits.llmsecretscanner.evaluator

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration.FALSE_POSITIVE_CASES_DIR
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration.POSITIVE_CASES_DIR
import net.cyclingbits.llmsecretscanner.evaluator.logger.EvaluatorLogger
import net.cyclingbits.llmsecretscanner.evaluator.model.EvaluationResult
import net.cyclingbits.llmsecretscanner.evaluator.service.EvaluationService
import net.cyclingbits.llmsecretscanner.evaluator.service.ResultsSaver

fun main() {

    val logger = EvaluatorLogger().also { it.reportEvaluationStart() }
    val results = mutableListOf<EvaluationResult>()

    getAllModels().forEach { modelName ->
        getAllIncludesPatterns().forEach { includesPattern ->
            val config = ScannerConfiguration(
                sourceDirectories = listOf(POSITIVE_CASES_DIR, FALSE_POSITIVE_CASES_DIR),
                modelName = modelName,
                includes = includesPattern,
                excludes = "**/expected/**",
                chunkAnalysisTimeout = 600
            )
            val evaluationService = EvaluationService(config, logger)
            val result = evaluationService.evaluateModel()

            results.add(result)
        }
    }

    ResultsSaver(logger).saveResultsToMarkdown(results)
    logger.reportEvaluationComplete(results.sumOf { it.scanTime } / 1000)
}

private fun getAllModels(): List<String> = listOf(
//    "ai/llama3.3:latest",
    "ai/phi4:latest",
//    "ai/llama3.2:latest",
//    "ai/deepcoder-preview:latest",
//    "ai/mistral-nemo:latest",
//    "ai/llama3.1:latest",
//    "ai/qwq:latest",
//    "ai/qwen3:latest",
//    "ai/qwen2.5:latest",
//    "ai/gemma3n:latest",
//    "ai/gemma3:latest",
//    "ai/gemma3-qat:latest",
//    "ai/deepseek-r1-distill-llama:latest",
//    "ai/mistral:latest",
//    "ai/smollm2:latest"
//    "ai/smollm3:latest"
)

private fun getAllIncludesPatterns(): List<String> = listOf(
    "**/*.java",
//    "**/*.kt",
//    "**/*.xml",
//    "**/*.properties",
//    "**/*.yml"
)