package net.cyclingbits.llmsecretscanner.evaluator.model

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.events.EventType
import net.cyclingbits.llmsecretscanner.events.LogLevel
import java.time.Instant

data class EvaluationError(
    val timestamp: Instant,
    val type: EventType,
    val level: LogLevel,
    val message: String,
    val errorType: String? = null
)

data class EvaluationResult(
    val modelName: String,
    val detectionRate: Double,
    val falsePositiveRate: Double,
    val scanTime: Long,
    val config: ScannerConfiguration,
    val errors: List<EvaluationError> = emptyList()
)