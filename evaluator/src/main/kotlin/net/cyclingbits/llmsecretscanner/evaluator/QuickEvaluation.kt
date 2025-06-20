package net.cyclingbits.llmsecretscanner.evaluator

import net.cyclingbits.llmsecretscanner.evaluator.service.EvaluationService

fun main() {
    println("🚀 Quick evaluation: Java files only, single model")
    EvaluationService.evaluateModels(
        models = listOf("ai/llama3.2:latest"),
        includes = "**/*.java",
        fileAnalysisTimeout = 60
    )
    println("✅ Model evaluated and results saved")
}