package net.cyclingbits.llmsecretscanner.optimizer.model

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.evaluator.model.EvaluationResult
import net.cyclingbits.llmsecretscanner.events.ScanEvent

data class OptimizationRun(
    val iteration: Int,
    val config: ScannerConfiguration,
    val evaluationResult: EvaluationResult,
    val events: List<ScanEvent>,
    val llmSuggestion: LLMSuggestion
) {
    val isBaseline: Boolean get() = iteration == 0
    
    fun score(): Double = evaluationResult.detectionRate - evaluationResult.falsePositiveRate - if (evaluationResult.errors.isNotEmpty()) 10.0 else 0.0
}