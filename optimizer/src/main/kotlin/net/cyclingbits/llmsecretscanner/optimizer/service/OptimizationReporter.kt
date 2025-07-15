package net.cyclingbits.llmsecretscanner.optimizer.service

import net.cyclingbits.llmsecretscanner.events.EventSource
import net.cyclingbits.llmsecretscanner.events.Formatter
import net.cyclingbits.llmsecretscanner.events.JsonSupport
import net.cyclingbits.llmsecretscanner.optimizer.config.OptimizerConfiguration
import net.cyclingbits.llmsecretscanner.optimizer.model.LLMSuggestion
import net.cyclingbits.llmsecretscanner.optimizer.model.OptimizationResult
import java.io.File

class OptimizationReporter {

    fun saveResults(result: OptimizationResult, config: OptimizerConfiguration) {
        val outputDir = config.outputDir
        outputDir.mkdirs()

        val filePrefix = generateFilePrefix(result)

        saveEventsJson(result, outputDir, filePrefix)
        saveResultsMarkdown(result, outputDir, filePrefix)
        savePrompt(result, outputDir, filePrefix)
    }

    private fun generateFilePrefix(result: OptimizationResult): String {
        val timestamp = Formatter.formatForFileName()
        val modelName = result.bestRun.evaluationResult.modelName.substringAfterLast("/").substringBeforeLast(":")
        val fileType = extractFileType(result.bestRun.config.includes)
        val llmProvider = result.llmProvider.name.lowercase()
        return "${timestamp}_${modelName}_${fileType}_${llmProvider}"
    }

    private fun extractFileType(includes: String): String {
        return includes.substringAfterLast("*.").takeIf {
            !it.contains(" ") && !it.contains(",") && it != includes
        } ?: "mixed"
    }

    private fun saveEventsJson(result: OptimizationResult, outputDir: File, filePrefix: String) {
        val allEvents = result.runs.flatMap { it.events }.filter { it.source == EventSource.OPTIMIZER }
        val content = JsonSupport.objectMapper.writeValueAsString(allEvents)
        File(outputDir, "${filePrefix}_optimization_events.json").writeText(content)
    }

    private fun saveResultsMarkdown(result: OptimizationResult, outputDir: File, filePrefix: String) {
        val content = buildMarkdownContent(result, filePrefix)
        File(outputDir, "${filePrefix}_optimization_results.md").writeText(content)
    }

    private fun buildMarkdownContent(result: OptimizationResult, filePrefix: String): String {
        return """
# Optimization Results - $filePrefix

## Summary
- **Total Iterations**: ${result.totalIterations}
- **Best Detection Rate**: ${Formatter.formatPercent(result.bestRun.evaluationResult.detectionRate)}%
- **Best False Positive Rate**: ${Formatter.formatPercent(result.bestRun.evaluationResult.falsePositiveRate)}%

## Performance Comparison
| Iteration | Detection Rate | False Positive Rate | Scan Time | Errors |
|-----------|----------------|-------------------|-----------|--------|
${buildPerformanceTable(result)}

## Improvement Analysis
${buildImprovementAnalysis(result)}

## Best System Prompt
${result.bestRun.config.systemPrompt}

        """.trimIndent()
    }

    private fun buildPerformanceTable(result: OptimizationResult): String {
        return result.runs.mapIndexed { index, run ->
            "| $index | ${Formatter.formatPercent(run.evaluationResult.detectionRate)}% | ${Formatter.formatPercent(run.evaluationResult.falsePositiveRate)}% | ${Formatter.formatTime(run.evaluationResult.scanTime)} | ${run.evaluationResult.errors.size} |"
        }.joinToString("\n")
    }

    private fun buildImprovementAnalysis(result: OptimizationResult): String {
        val firstRun = result.runs.first()
        val bestRun = result.bestRun

        val detectionImprovement = bestRun.evaluationResult.detectionRate - firstRun.evaluationResult.detectionRate
        val falsePositiveChange = bestRun.evaluationResult.falsePositiveRate - firstRun.evaluationResult.falsePositiveRate
        val errorChange = bestRun.evaluationResult.errors.size - firstRun.evaluationResult.errors.size

        return """
- **Detection Rate Improvement**: ${Formatter.formatPercent(detectionImprovement)}%
- **False Positive Rate Change**: ${Formatter.formatPercent(falsePositiveChange)}%
- **Error Count Change**: $errorChange (${firstRun.evaluationResult.errors.size} → ${bestRun.evaluationResult.errors.size})
        """.trimIndent()
    }


    private fun savePrompt(result: OptimizationResult, outputDir: File, filePrefix: String) {
        File(outputDir, "${filePrefix}_best_system_prompt.md").writeText(result.bestRun.config.systemPrompt)
    }

    fun saveLLMPrompt(prompt: String, config: OptimizerConfiguration) {
        val timestamp = Formatter.formatForFileName()
        val llmProvider = config.llmProvider.name.lowercase()
        val filePrefix = "${timestamp}_${llmProvider}"

        File(config.outputDir, "${filePrefix}_llm_prompt.md").writeText(prompt)
    }

    fun saveLLMRawResponse(response: String, config: OptimizerConfiguration) {
        val timestamp = Formatter.formatForFileName()
        val llmProvider = config.llmProvider.name.lowercase()
        val filePrefix = "${timestamp}_${llmProvider}"

        File(config.outputDir, "${filePrefix}_llm_raw_response.md").writeText(response)
    }

    fun saveLLMSuggestion(llmSuggestion: LLMSuggestion, config: OptimizerConfiguration) {
        val timestamp = Formatter.formatForFileName()
        val llmProvider = config.llmProvider.name.lowercase()
        val filePrefix = "${timestamp}_${llmProvider}"

        val content = """
## Reasoning
${llmSuggestion.reasoning}

## Suggested System Prompt
${llmSuggestion.newSystemPrompt}
        """.trimIndent()

        File(config.outputDir, "${filePrefix}_llm_suggestion.md").writeText(content)
    }
}