package net.cyclingbits.llmsecretscanner.evaluator

import net.cyclingbits.llmsecretscanner.core.service.ScanReporter
import net.cyclingbits.llmsecretscanner.evaluator.service.EvaluationService

fun main() {
    ScanReporter.reportEvaluationStart("🚀 Quick evaluation: Java files only, single model")
    EvaluationService.evaluateModels(
        models = listOf("ai/llama3.2:latest"),
//        models = listOf("ai/phi4:latest"),
        includes = "**/*.java",
        chunkAnalysisTimeout = 120
    )
}