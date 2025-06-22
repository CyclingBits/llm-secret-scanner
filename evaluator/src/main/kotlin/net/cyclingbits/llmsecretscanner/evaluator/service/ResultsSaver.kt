package net.cyclingbits.llmsecretscanner.evaluator.service

import net.cyclingbits.llmsecretscanner.core.util.ScanReporter
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration
import net.cyclingbits.llmsecretscanner.evaluator.model.EvaluationResult
import java.io.File
import java.time.Duration

object ResultsSaver {

    fun saveResultsToMarkdown(results: List<EvaluationResult>) {
        val outputFile = File(EvaluatorConfiguration.RESULTS_FILE)

        val newRows = buildString {
            results.forEach { result ->
                val detectionRateDisplay = "${String.format("%.1f", result.detectionRate)}%"
                val falsePositiveRateDisplay = "${String.format("%.1f", result.falsePositiveRate)}%"
                val scanSuccessRateDisplay = "${String.format("%.1f", result.scanSuccessRate)}%"
                val timeDisplay = formatTime(result.scanTime)
                val modelData = ModelDataProvider.findModelData(result.modelName)
                val parameters = modelData?.parameters ?: "N/A"
                val contextWindow = modelData?.contextWindow ?: "N/A"
                val size = modelData?.size ?: "N/A"
                appendLine("| ${result.modelName} | $detectionRateDisplay | $falsePositiveRateDisplay | $scanSuccessRateDisplay | $timeDisplay | $parameters | $contextWindow | $size |")
            }
        }

        if (outputFile.exists()) {
            outputFile.appendText(newRows)
        } else {
            val markdown = buildString {
                appendLine("# LLM Evaluation Results")
                appendLine()
                    appendLine("| LLM Image | Detection<br>Rate | False Positive<br>Rate | Files Successfully<br>Analyzed | Time | Parameters | Context<br>Window | Size |")
                appendLine("|-----------|----------------|---------------------|----------------------|------|------------|----------------|------|")
                append(newRows)
            }
            outputFile.writeText(markdown)
        }

        ScanReporter.reportFileSaved(outputFile.absolutePath)
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