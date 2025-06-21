package net.cyclingbits.llmsecretscanner.evaluator

import net.cyclingbits.llmsecretscanner.core.service.ScanReporter
import net.cyclingbits.llmsecretscanner.evaluator.service.EvaluationService

fun main() {
    ScanReporter.reportEvaluationStart("🔍 Full evaluation: All file types, all models")
    EvaluationService.evaluateModels(
        models = getAllModels(),
        includes = "**/*.java,**/*.kt,**/*.xml,**/*.properties,**/*.yml,**/*.yaml,**/*.json,**/*.md,**/*.sql,**/*.gradle,**/*.kts,**/*.env,**/*.sh,**/*.bat,**/*.html,**/*.css,**/*.js,**/*.ts,**/*.dockerfile",
        chunkAnalysisTimeout = 600
    )
    ScanReporter.reportEvaluationComplete("✅ All models evaluated and results saved incrementally")
}

private fun getAllModels(): List<String> = listOf(
    "ai/gemma3:latest",
    "ai/llama3.1:latest",
    "ai/qwen2.5:latest",
    "ai/phi4:latest",
    "ai/deepseek-r1-distill-llama:latest",
    "ai/mistral-nemo:latest",
    "ai/deepcoder-preview:latest",
    "ai/gemma3-qat:latest",
    "ai/llama3.3:latest",
    "ai/llama3.2:latest",
    "ai/mistral:latest",
    "ai/qwen3:latest",
    "ai/qwq:latest",
    "ai/smollm2:latest"
)