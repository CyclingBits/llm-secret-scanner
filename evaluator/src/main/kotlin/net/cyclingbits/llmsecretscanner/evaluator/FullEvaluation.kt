package net.cyclingbits.llmsecretscanner.evaluator

import net.cyclingbits.llmsecretscanner.evaluator.service.EvaluationService
import net.cyclingbits.llmsecretscanner.evaluator.service.ResultsSaver

fun main() {
    println("🔍 Full evaluation: All file types, all models")
    val results = EvaluationService.evaluateModels(
        models = getAllModels(),
        includes = "**/*.java,**/*.kt,**/*.xml,**/*.properties,**/*.yml,**/*.yaml,**/*.json,**/*.md,**/*.sql,**/*.gradle,**/*.kts,**/*.env,**/*.sh,**/*.bat,**/*.html,**/*.css,**/*.js,**/*.ts,**/*.dockerfile",
        fileAnalysisTimeout = 120
    )
    ResultsSaver.saveResultsToMarkdown(results)
}

private fun getAllModels(): List<String> = listOf(
    "ai/gemma3:latest",
    "ai/deepseek-r1-distill-llama:latest",
    "ai/llama3.1:latest",
    "ai/phi4:latest",
    "ai/mistral-nemo:latest",
    "ai/deepcoder-preview:latest"
)