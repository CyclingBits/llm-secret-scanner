package net.cyclingbits.llmsecretscanner.evaluator.service

import net.cyclingbits.llmsecretscanner.events.EventStore
import net.cyclingbits.llmsecretscanner.events.JsonSupport
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration.RESULTS_DIR
import net.cyclingbits.llmsecretscanner.evaluator.logger.EvaluatorLogger
import net.cyclingbits.llmsecretscanner.evaluator.model.EvaluationResult
import net.cyclingbits.llmsecretscanner.evaluator.service.DockerModelProvider
import net.cyclingbits.llmsecretscanner.events.Formatter
import java.io.File
import java.time.Duration
import java.time.LocalDateTime

class ResultsSaver(
    private val logger: EvaluatorLogger
) {

    fun saveResultsToMarkdown(results: List<EvaluationResult>) {
        val timestamp = Formatter.formatForFileName()
        val outputFile = File(RESULTS_DIR, "${timestamp}_evaluation_results.md")

        RESULTS_DIR.mkdirs()

        val newRows = buildString {
            results.forEach { result ->
                val fileTypes = getFileTypesFromIncludes(result.config.includes)
                val detectionRateDisplay = "${String.format("%.1f", result.detectionRate)}%"
                val falsePositiveRateDisplay = "${String.format("%.1f", result.falsePositiveRate)}%"
                val timeDisplay = formatTime(result.scanTime)
                val modelData = DockerModelProvider.findModelData(result.modelName)
                val parameters = modelData?.parameters ?: "N/A"
                val contextWindow = modelData?.contextWindow ?: "N/A"
                val size = modelData?.size ?: "N/A"
                appendLine("| ${result.modelName} | $fileTypes | $detectionRateDisplay | $falsePositiveRateDisplay | $timeDisplay | $parameters | $contextWindow | $size |")
            }
        }

        val markdown = buildString {
            appendLine("# LLM Evaluation Results")
            appendLine()
            appendLine("Generated on: ${Formatter.formatForReport()}")
            appendLine()
            appendLine("| LLM Image | File Types | Detection<br>Rate | False Positive<br>Rate | Time | Parameters | Context<br>Window | Size |")
            appendLine("|-----------|------------|----------------|---------------------|------|------------|----------------|------|")
            append(newRows)
        }
        outputFile.writeText(markdown)
        logger.reportFileSaved(outputFile.absolutePath)

        val eventsFile = File(RESULTS_DIR, "${timestamp}_evaluation_events.json")
        val events = EventStore.getAll()
        eventsFile.writeText(JsonSupport.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(events))
        logger.reportFileSaved(eventsFile.absolutePath)
    }

    private fun getFileTypesFromIncludes(includes: String): String {
        val patterns = includes.split(",").map { it.trim() }
        val extensions = patterns.mapNotNull { pattern ->
            when {
                pattern.startsWith("**/*.") -> pattern.substringAfterLast(".").lowercase()
                pattern.startsWith("*.") -> pattern.substringAfterLast(".").lowercase()
                else -> null
            }
        }.sorted()

        return extensions.joinToString(", ")
    }

    private fun formatTime(timeInMs: Long): String {
        val duration = Duration.ofMillis(timeInMs)
        val minutes = duration.toMinutes()
        val seconds = duration.minusMinutes(minutes).seconds

        return when {
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }
}