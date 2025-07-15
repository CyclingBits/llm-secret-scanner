package net.cyclingbits.llmsecretscanner.optimizer.model

import net.cyclingbits.llmsecretscanner.optimizer.config.OptimizerConfiguration.LLMProviderType
import java.time.Duration
import java.time.Instant

data class OptimizationResult(
    val runs: List<OptimizationRun>,
    val llmProvider: LLMProviderType,
    val startTime: Instant,
    val endTime: Instant = Instant.now()
) {
    init {
        require(runs.isNotEmpty()) { "Optimization result must contain at least one run" }
    }
    
    val bestRun: OptimizationRun get() = runs.maxByOrNull { it.score() } ?: runs.first()
    val baselineRun: OptimizationRun? get() = runs.firstOrNull { it.isBaseline }
    val optimizationRuns: List<OptimizationRun> get() = runs.filter { !it.isBaseline }
    val totalIterations: Int get() = optimizationRuns.size
    val totalDuration: Duration get() = Duration.between(startTime, endTime)
    
    fun getImprovement(): Double = baselineRun?.let { bestRun.score() - it.score() } ?: 0.0
    
    fun wasSuccessful(): Boolean = getImprovement() > 0
}