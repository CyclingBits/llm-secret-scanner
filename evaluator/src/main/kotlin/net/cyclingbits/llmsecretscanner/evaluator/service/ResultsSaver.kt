package net.cyclingbits.llmsecretscanner.evaluator.service

import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration
import net.cyclingbits.llmsecretscanner.evaluator.model.EvaluationResult
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ResultsSaver {

    fun saveResultsToMarkdown(results: List<EvaluationResult>) {
        val testExecutionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val outputFile = File(EvaluatorConfiguration.RESULTS_FILE)

        val newRows = buildString {
            results.forEach { result ->
                val detectionRateDisplay = "${String.format("%.1f", result.detectionRate)}%"
                val timeDisplay = formatTime(result.scanTime)
                val modelData = ModelDataProvider.findModelData(result.modelName)
                val parameters = modelData?.parameters ?: "N/A"
                val contextWindow = modelData?.contextWindow ?: "N/A"
                val size = modelData?.size ?: "N/A"
                appendLine("| $testExecutionTime | ${result.modelName} | $detectionRateDisplay | $timeDisplay | $parameters | $contextWindow | $size |")
            }
        }

        if (outputFile.exists()) {
            outputFile.appendText(newRows)
        } else {
            val markdown = buildString {
                appendLine("# LLM Evaluation Results")
                appendLine()
                appendLine("| Date | LLM Image | Detection Rate | Time | Parameters | Context Window | Size |")
                appendLine("|------|-----------|----------------|------|------------|----------------|------|")
                append(newRows)
            }
            outputFile.writeText(markdown)
        }

        println("💾 Results saved to ${outputFile.absolutePath}")
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