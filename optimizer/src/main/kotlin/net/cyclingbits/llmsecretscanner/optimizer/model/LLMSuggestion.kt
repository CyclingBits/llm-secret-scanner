package net.cyclingbits.llmsecretscanner.optimizer.model

data class LLMSuggestion(
    val newSystemPrompt: String,
    val reasoning: String
)