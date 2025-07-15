package net.cyclingbits.llmsecretscanner.optimizer

import net.cyclingbits.llmsecretscanner.optimizer.config.OptimizerConfiguration
import net.cyclingbits.llmsecretscanner.optimizer.config.OptimizerConfiguration.FileType.*
import net.cyclingbits.llmsecretscanner.optimizer.config.OptimizerConfiguration.LLMProviderType.*
import net.cyclingbits.llmsecretscanner.optimizer.logger.OptimizerLogger
import net.cyclingbits.llmsecretscanner.optimizer.service.OptimizationEngine
import net.cyclingbits.llmsecretscanner.optimizer.service.OptimizationReporter

fun main() {
    val logger = OptimizerLogger()

    val optimizerConfig = OptimizerConfiguration(
        maxIterations = 20,
        fileType = XML,
        llmProvider = CLAUDE_CODE
    )

    val result = OptimizationEngine(optimizerConfig, logger).optimize()

    OptimizationReporter().saveResults(result, optimizerConfig)

    logger.reportBestConfigurationSaved(optimizerConfig)
}