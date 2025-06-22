package net.cyclingbits.llmsecretscanner.evaluator

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.util.ScanReporter
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration
import net.cyclingbits.llmsecretscanner.evaluator.service.EvaluationService
import net.cyclingbits.llmsecretscanner.evaluator.service.ResultsSaver
import java.io.File

fun main() {
    ScanReporter.reportEvaluationStart("Full evaluation: All file types, all models")
    
    val includes = "**/*.java,**/*.kt,**/*.xml,**/*.properties,**/*.yml,**/*.yaml,**/*.json,**/*.md,**/*.sql,**/*.gradle,**/*.kts,**/*.env,**/*.sh,**/*.bat,**/*.html,**/*.css,**/*.js,**/*.ts,**/*.dockerfile"
    getAllModels().forEach { modelName ->
        val config = ScannerConfiguration(
            sourceDirectories = listOf(
                File(EvaluatorConfiguration.POSITIVE_CASES_DIR),
                File(EvaluatorConfiguration.NEGATIVE_CASES_DIR)
            ),
            modelName = modelName,
            includes = includes,
            excludes = "**/expected/**",
            chunkAnalysisTimeout = 600
        )
        val evaluationService = EvaluationService(config)
        val result = evaluationService.evaluateModel()
        ResultsSaver.saveResultsToMarkdown(listOf(result))
    }
    
    ScanReporter.reportEvaluationComplete("✅ All models evaluated and results saved incrementally")
}

private fun getAllModels(): List<String> = listOf(
    "ai/llama3.3:latest",
    "ai/phi4:latest",
    "ai/llama3.2:latest",
    "ai/deepcoder-preview:latest",
    "ai/mistral-nemo:latest",
    "ai/llama3.1:latest",
//    "ai/qwq:latest",
    "ai/qwen3:latest",
//    "ai/qwen2.5:latest",
    "ai/gemma3:latest",
    "ai/gemma3-qat:latest",
    "ai/deepseek-r1-distill-llama:latest",
    "ai/mistral:latest",
//    "ai/smollm2:latest"
)