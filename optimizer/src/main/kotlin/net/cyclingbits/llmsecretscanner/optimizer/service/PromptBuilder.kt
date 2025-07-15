package net.cyclingbits.llmsecretscanner.optimizer.service

import net.cyclingbits.llmsecretscanner.evaluator.model.EvaluationResult
import net.cyclingbits.llmsecretscanner.events.ScanEvent
import net.cyclingbits.llmsecretscanner.events.Utils
import net.cyclingbits.llmsecretscanner.events.toFormattedJsonString
import net.cyclingbits.llmsecretscanner.optimizer.model.OptimizationRun

class PromptBuilder {

    fun buildOptimizationPrompt(
        currentPrompt: String,
        evaluationResult: EvaluationResult,
        events: List<ScanEvent>,
        previousRuns: List<OptimizationRun> = emptyList(),
        currentIteration: Int
    ): String {
        val template = Utils.loadResource("optimizer_prompt_template.md")
        val evaluationResultJson = evaluationResult.toFormattedJsonString()
        val eventsJson = events.joinToString(",\n") { it.toFormattedJsonString() }
//        val previousSuggestionsSection = buildPreviousSuggestionsSection(previousRuns)

        return template
            .replace("\${currentIteration}", currentIteration.toString())
            .replace("\${currentPrompt}", Utils.stripMarkdown(currentPrompt))
            .replace("\${evaluationData}", evaluationResultJson)
            .replace("\${eventsData}", eventsJson)
//            .replace("\${previousAttempts}", previousSuggestionsSection)
    }

//    private fun buildPreviousSuggestionsSection(previousRuns: List<OptimizationRun>): String {
//        if (previousRuns.isEmpty()) {
//            return "No previous optimization attempts."
//        }
//
//        val template = Utils.loadResource("previous_attempts_template.md")
//        return previousRuns.filter { it.iteration > 0 }.joinToString("\n") { run ->
//            template
//                .replace("\${iteration}", run.iteration.toString())
//                .replace("\${detectionRate}", Utils.formatPercent(run.evaluationResult.detectionRate))
//                .replace("\${falsePositiveRate}", Utils.formatPercent(run.evaluationResult.falsePositiveRate))
//                .replace("\${errorCount}", run.evaluationResult.errors.size.toString())
//                .replace("\${reasoning}", run.llmSuggestion.reasoning)
//                .replace("\${appliedPrompt}", Utils.stripMarkdown(run.llmSuggestion.newSystemPrompt))
//        }
//    }
}