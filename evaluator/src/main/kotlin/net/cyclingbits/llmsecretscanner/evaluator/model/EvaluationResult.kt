package net.cyclingbits.llmsecretscanner.evaluator.model

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration

data class EvaluationResult(
    val modelName: String,
    val detectionRate: Double,
    val falsePositiveRate: Double,
    val scanTime: Long,
    val config: ScannerConfiguration
)