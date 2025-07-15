package net.cyclingbits.llmsecretscanner.optimizer.model

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.optimizer.config.OptimizerConfiguration.LLMProviderType
import java.time.Instant

class OptimizationState(
    val baseConfig: ScannerConfiguration,
    val startTime: Instant = Instant.now()
) {
    private val _runs = mutableListOf<OptimizationRun>()
    val runs: List<OptimizationRun> get() = _runs.toList()
    
    val currentIteration: Int get() = _runs.size - 1
    val lastRun: OptimizationRun? get() = _runs.lastOrNull()
    val baselineRun: OptimizationRun? get() = _runs.firstOrNull { it.isBaseline }
    
    fun addRun(run: OptimizationRun) {
        require(run.iteration == _runs.size) { "Run iteration must be sequential" }
        _runs.add(run)
    }
    
    fun hasRuns(): Boolean = _runs.isNotEmpty()
    
    fun toResult(llmProvider: LLMProviderType): OptimizationResult = OptimizationResult(
        runs = runs,
        llmProvider = llmProvider,
        startTime = startTime
    )
}