package net.cyclingbits.llmsecretscanner.evaluator

import net.cyclingbits.llmsecretscanner.evaluator.service.EvaluationService
import net.cyclingbits.llmsecretscanner.evaluator.service.ResultsSaver

fun main() {
    println("🚀 Quick evaluation: Java files only, single model")
    val results = EvaluationService.evaluateModels(
        models = listOf("ai/gemma3:latest"),
        includes = "**/*.java"
    )
    ResultsSaver.saveResultsToMarkdown(results)
}