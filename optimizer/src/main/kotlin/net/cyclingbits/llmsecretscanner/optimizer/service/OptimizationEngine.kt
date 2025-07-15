package net.cyclingbits.llmsecretscanner.optimizer.service

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration.FALSE_POSITIVE_CASES_DIR
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration.POSITIVE_CASES_DIR
import net.cyclingbits.llmsecretscanner.evaluator.logger.EvaluatorLogger
import net.cyclingbits.llmsecretscanner.evaluator.model.EvaluationResult
import net.cyclingbits.llmsecretscanner.evaluator.service.EvaluationService
import net.cyclingbits.llmsecretscanner.events.*
import net.cyclingbits.llmsecretscanner.optimizer.config.OptimizerConfiguration
import net.cyclingbits.llmsecretscanner.optimizer.llm.LLMProvider
import net.cyclingbits.llmsecretscanner.optimizer.logger.OptimizerLogger
import net.cyclingbits.llmsecretscanner.optimizer.model.LLMSuggestion
import net.cyclingbits.llmsecretscanner.optimizer.model.OptimizationResult
import net.cyclingbits.llmsecretscanner.optimizer.model.OptimizationRun
import net.cyclingbits.llmsecretscanner.optimizer.model.OptimizationState

class OptimizationEngine(
    private val config: OptimizerConfiguration,
    private val logger: OptimizerLogger
) {
    private val promptBuilder = PromptBuilder()

    private val llmProvider: LLMProvider = config.llmProvider.createProvider(config, logger)

    private val reporter = OptimizationReporter()

    fun optimize(): OptimizationResult {
        val state = initializeOptimizationState()

        try {
            // Step 1: Run baseline evaluation
            val baselineRun = runBaselineEvaluation(state)
            state.addRun(baselineRun)

            // Step 2: Run optimization loop
            runOptimizationLoop(state)

            // Step 3: Build and save result
            return buildAndSaveResult(state)
        } catch (e: Exception) {
            handleOptimizationError(e, state)
            throw e
        }
    }

    private fun initializeOptimizationState(): OptimizationState {
        val baseConfig = createBaseScannerConfiguration()
        EventStore.setEnabledLogSources(config.logSources)
        EventStore.setMinLogLevel(config.minLogLevel)
        logger.reportOptimizationStart(config)
        return OptimizationState(baseConfig)
    }

    private fun runBaselineEvaluation(state: OptimizationState): OptimizationRun {
        EventStore.clear()
        logger.reportIterationStart(0)
        
        val evaluatorLogger = EvaluatorLogger()
        val evaluationResult = evaluateCurrentConfig(state.baseConfig, evaluatorLogger)
        val events = collectEvaluationData()
        
        return OptimizationRun(
            iteration = 0,
            config = state.baseConfig,
            evaluationResult = evaluationResult,
            events = events,
            llmSuggestion = LLMSuggestion("", "Baseline run - no optimization")
        )
    }

    private fun runOptimizationLoop(state: OptimizationState) {
        val evaluatorLogger = EvaluatorLogger()
        var currentConfig = state.baseConfig
        
        for (iteration in 1..config.maxIterations) {
            try {
                EventStore.clear()
                logger.reportIterationStart(iteration)
                
                // Get suggestion from LLM based on previous run
                val previousRun = state.lastRun ?: throw IllegalStateException("No previous run found")
                val suggestion = getLLMSuggestion(
                    previousRun.config,
                    previousRun.evaluationResult,
                    previousRun.events,
                    state.runs,
                    iteration
                )
                
                // Apply optimization
                currentConfig = applyOptimization(iteration, currentConfig, suggestion)
                
                // Evaluate new configuration
                val evaluationResult = evaluateCurrentConfig(currentConfig, evaluatorLogger)
                val events = collectEvaluationData()
                
                // Create and add new run
                val run = OptimizationRun(
                    iteration = iteration,
                    config = currentConfig,
                    evaluationResult = evaluationResult,
                    events = events,
                    llmSuggestion = suggestion
                )
                state.addRun(run)
                
                // Check for early termination
                if (isPerfectResult(evaluationResult)) {
                    logger.reportOptimizationEarlyTermination(iteration, evaluationResult)
                    break
                }
            } catch (e: IllegalArgumentException) {
                logger.reportOptimizationError(iteration, "Invalid LLM response format: ${e.message}")
                break
            }
        }
    }

    private fun buildAndSaveResult(state: OptimizationState): OptimizationResult {
        val result = state.toResult(config.llmProvider)
        logger.reportOptimizationComplete(result, result.totalDuration.seconds)
        reporter.saveResults(result, config)
        return result
    }

    private fun handleOptimizationError(e: Exception, state: OptimizationState) {
        if (state.hasRuns()) {
            val partialResult = state.toResult(config.llmProvider)
            reporter.saveResults(partialResult, config)
            logger.reportOptimizationError(0, "Optimization failed, partial results saved: ${e.message}")
        } else {
            logger.reportOptimizationError(0, e.message ?: "Unknown optimization error")
        }
    }

    private fun createBaseScannerConfiguration(): ScannerConfiguration {
        return ScannerConfiguration(
            sourceDirectories = listOf(POSITIVE_CASES_DIR, FALSE_POSITIVE_CASES_DIR),
            includes = config.fileType.includePattern,
            enableChunking = true,
            maxLinesPerChunk = 100,
            systemPrompt = Utils.loadResource("default_system_prompt.md")
        )
    }

    private fun evaluateCurrentConfig(config: ScannerConfiguration, evaluatorLogger: EvaluatorLogger) = EvaluationService(config, evaluatorLogger).evaluateModel()

    private fun getLLMSuggestion(
        scannerConfiguration: ScannerConfiguration,
        evaluationResult: EvaluationResult,
        events: List<ScanEvent>,
        previousRuns: List<OptimizationRun>,
        currentIteration: Int
    ): LLMSuggestion {
        val prompt = promptBuilder.buildOptimizationPrompt(scannerConfiguration.systemPrompt, evaluationResult, events, previousRuns, currentIteration)
        reporter.saveLLMPrompt(prompt, config)
        return try {
            val rawResponse = llmProvider.getRawResponse(prompt)
            reporter.saveLLMRawResponse(rawResponse, config)
            val llmSuggestion = llmProvider.parseResponse(rawResponse)
            reporter.saveLLMSuggestion(llmSuggestion, config)
            llmSuggestion
        } catch (e: Exception) {
            logger.reportLLMProviderError(e.message ?: "Unknown error", this.config)
            throw e
        }
    }

    private fun applyOptimization(iteration: Int, currentConfig: ScannerConfiguration, suggestion: LLMSuggestion): ScannerConfiguration {
        logger.reportLLMSuggestion(iteration, suggestion.newSystemPrompt, suggestion.reasoning)
        val optimizedConfig = currentConfig.copy(systemPrompt = suggestion.newSystemPrompt)
        return optimizedConfig
    }

    private fun collectEvaluationData(): List<ScanEvent> {
        return EventStore.getAll().filter { it.source in listOf(EventSource.CORE, EventSource.EVALUATOR) }
    }

    private fun isPerfectResult(evaluationResult: EvaluationResult): Boolean =
        evaluationResult.detectionRate == 100.0 && evaluationResult.falsePositiveRate == 0.0 && evaluationResult.errors.isEmpty()
}