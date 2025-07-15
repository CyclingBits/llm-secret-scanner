package net.cyclingbits.llmsecretscanner.optimizer.logger

import net.cyclingbits.llmsecretscanner.evaluator.model.EvaluationResult
import net.cyclingbits.llmsecretscanner.events.*
import net.cyclingbits.llmsecretscanner.optimizer.config.OptimizerConfiguration
import net.cyclingbits.llmsecretscanner.optimizer.config.OptimizerConfiguration.LLMProviderType
import net.cyclingbits.llmsecretscanner.optimizer.model.OptimizationResult

class OptimizerLogger(override val eventSource: EventSource = EventSource.OPTIMIZER) : Logger {

    private fun estimateTokens(text: String): Int = text.length / 4

    fun reportOptimizationStart(optimizerConfig: OptimizerConfiguration) {
        EventStore.log(
            source = eventSource,
            type = EventType.OPTIMIZATION_START,
            level = LogLevel.INFO,
            data = mapOf(
                "maxIterations" to optimizerConfig.maxIterations, 
                "llmProvider" to optimizerConfig.llmProvider.name, 
                "model" to optimizerConfig.llmProvider.modelName,
                "fileType" to optimizerConfig.fileType.name
            )
        ) {
            addEmpty()
            add("{} {}", LoggerColors.OPTIMIZER_ICON, LoggerColors.boldCyan("Starting configuration optimization"))
            add("       Max iterations: {}", LoggerColors.cyan(optimizerConfig.maxIterations.toString()))
            add("       LLM provider: {}", LoggerColors.cyan(optimizerConfig.llmProvider.name))
            add("       Model: {}", LoggerColors.cyan(optimizerConfig.llmProvider.modelName))
            add("       File type: {}", LoggerColors.cyan(optimizerConfig.fileType.name))
        }
    }

    fun reportIterationStart(iteration: Int) {
        val iterationLabel = "Iteration $iteration"

        EventStore.log(
            source = eventSource,
            type = EventType.ITERATION_START,
            level = LogLevel.INFO,
            data = mapOf("iteration" to iteration)
        ) {
            addEmpty()
            add("{} {}", LoggerColors.GEAR_ICON, LoggerColors.boldBlue(iterationLabel))
        }
    }

    fun reportLLMSuggestion(iteration: Int, suggestion: String, reasoning: String) {
        EventStore.log(
            source = eventSource,
            type = EventType.LLM_SUGGESTION,
            level = LogLevel.INFO,
            data = mapOf("iteration" to iteration, "suggestion" to suggestion, "reasoning" to reasoning)
        ) {
            add("       Reasoning: {}", LoggerColors.cyan(truncateAndClean(reasoning)))
            add("       LLM suggestion: {}", LoggerColors.yellow(truncateAndClean(suggestion)))
        }
    }

    fun reportOptimizationEarlyTermination(iteration: Int, result: EvaluationResult) {
        EventStore.log(
            source = eventSource,
            type = EventType.OPTIMIZATION_COMPLETE,
            level = LogLevel.INFO,
            data = mapOf(
                "iteration" to iteration,
                "detectionRate" to result.detectionRate,
                "falsePositiveRate" to result.falsePositiveRate,
                "earlyTermination" to true
            )
        ) {
            addEmpty()
            add("{} {} after {} iterations", LoggerColors.SUCCESS_ICON, LoggerColors.boldGreen("Perfect result achieved - terminating early"), LoggerColors.cyan(iteration.toString()))
            add(
                "       Result: {}% detection, {}% false positives",
                LoggerColors.boldGreen("%.1f".format(result.detectionRate)),
                LoggerColors.boldRed("%.1f".format(result.falsePositiveRate))
            )
        }
    }

    fun reportOptimizationComplete(result: OptimizationResult, totalTimeSeconds: Long) {
        val minutes = totalTimeSeconds / 60
        val seconds = totalTimeSeconds % 60
        val timeDisplay = if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"

        EventStore.log(
            source = eventSource,
            type = EventType.OPTIMIZATION_COMPLETE,
            level = LogLevel.INFO,
            data = mapOf(
                "totalIterations" to result.totalIterations,
                "bestDetectionRate" to result.bestRun.evaluationResult.detectionRate,
                "bestFalsePositiveRate" to result.bestRun.evaluationResult.falsePositiveRate,
                "totalTimeSeconds" to totalTimeSeconds
            )
        ) {
            addEmpty()
            add("{} {} in {}", LoggerColors.SUCCESS_ICON, LoggerColors.boldGreen("Optimization completed successfully"), LoggerColors.cyan(timeDisplay))
            add(
                "       Best result: {}% detection, {}% false positives",
                LoggerColors.boldGreen("%.1f".format(result.bestRun.evaluationResult.detectionRate)),
                LoggerColors.boldRed("%.1f".format(result.bestRun.evaluationResult.falsePositiveRate))
            )
            add("       Total iterations: {}", LoggerColors.cyan(result.totalIterations.toString()))
        }
    }

    fun reportBestConfigurationSaved(optimizerConfig: OptimizerConfiguration) {
        EventStore.log(
            source = eventSource,
            type = EventType.CONFIG_SAVED,
            level = LogLevel.INFO,
            data = mapOf("filePath" to optimizerConfig.outputDir.toString(), "llmProvider" to optimizerConfig.llmProvider.name)
        ) {
            add("{} Best configuration saved to: {}", LoggerColors.SUCCESS_ICON, LoggerColors.boldGreen(optimizerConfig.outputDir.toString()))
        }
    }

    fun reportOptimizationError(iteration: Int, error: String) {
        EventStore.log(
            source = eventSource,
            type = EventType.OPTIMIZATION_ERROR,
            level = LogLevel.ERROR,
            data = mapOf("iteration" to iteration, "error" to error)
        ) {
            add("{} Optimization error at iteration {}: {}", LoggerColors.ERROR_ICON, iteration, LoggerColors.red(error))
        }
    }

    fun reportLLMProviderError(error: String, optimizerConfig: OptimizerConfiguration) {
        EventStore.log(
            source = eventSource,
            type = EventType.OPTIMIZATION_ERROR,
            level = LogLevel.ERROR,
            data = mapOf("provider" to optimizerConfig.llmProvider.name, "error" to error)
        ) {
            add("{} LLM provider {} error: {}", LoggerColors.ERROR_ICON, optimizerConfig.llmProvider.name, LoggerColors.red(error))
        }
    }

    fun reportLLMRequest(provider: LLMProviderType, prompt: String) {
        val tokenCount = estimateTokens(prompt)

        EventStore.log(
            source = eventSource,
            type = EventType.LLM,
            level = LogLevel.INFO,
            data = mapOf("provider" to provider.name, "model" to provider.modelName, "prompt" to prompt)
        ) {
            addEmpty()
            add("{} Making request to {} API", LoggerColors.AI_ICON, LoggerColors.boldCyan(provider.name))
            add("       Prompt length: {} tokens ({} characters)", LoggerColors.cyan(tokenCount.toString()), LoggerColors.cyan(prompt.length.toString()))
        }
    }

    fun reportLLMResponse(provider: LLMProviderType, response: String) {
        EventStore.log(
            source = eventSource,
            type = EventType.LLM,
            level = LogLevel.INFO,
            data = mapOf("provider" to provider.name, "response" to response)
        ) {
            add("       Response received: {} characters", LoggerColors.cyan(response.length.toString()))
        }
    }
}