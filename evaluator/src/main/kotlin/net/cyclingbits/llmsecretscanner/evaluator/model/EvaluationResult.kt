package net.cyclingbits.llmsecretscanner.evaluator.model

data class EvaluationResult(
    val modelName: String,
    val detectionRate: Double,
    val scanTime: Long
)