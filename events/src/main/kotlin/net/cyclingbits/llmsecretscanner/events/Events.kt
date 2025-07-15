package net.cyclingbits.llmsecretscanner.events

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant

enum class EventType {
    SCANNER,
    FILE,
    CHUNK,
    LLM,
    ANALYSE,
    JSON,
    ISSUE,
    CONTAINER,
    OPTIMIZATION_START,
    OPTIMIZATION_COMPLETE,
    ITERATION_START,
    LLM_SUGGESTION,
    CONFIG_UPDATE,
    CONFIG_SAVED,
    EVAL_PROGRESS,
    OPTIMIZATION_ERROR,
    EVALUATION_START,
    EVALUATION_COMPLETE,
    DATA_LOADED,
    RESULTS_SAVED,
    DETECTION_ANALYSIS
}

enum class EventSource {
    CORE,
    EVALUATOR,
    OPTIMIZER
}

enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
}

data class ScanEvent(
    val timestamp: Instant = Instant.now(),
    val source: EventSource,
    val level: LogLevel,
    val type: EventType,
    @JsonIgnore
    val messages: List<String>,
    val rawMessages: List<String>,
    val data: Map<String, Any?> = emptyMap(),
    val error: Throwable? = null
) {
    override fun toString(): String {
        return this.toFormattedJsonString()
    }
}

fun scanEvent(
    source: EventSource,
    type: EventType,
    level: LogLevel = LogLevel.INFO,
    messages: List<String> = emptyList(),
    data: Map<String, Any?> = emptyMap(),
    error: Throwable? = null
): ScanEvent {
    return ScanEvent(
        source = source,
        type = type,
        level = level,
        messages = messages,
        rawMessages = messages.map { it.stripAnsiCodes() },
        data = data,
        error = error
    )
}

private fun String.stripAnsiCodes(): String {
    return this
        .replace(Regex("\\u001B\\[[0-9;]*m"), "")
        .filter { char ->
            char.code in 32..126 || char.code in 128..255 || char.isWhitespace()
        }
        .trim().replace(Regex("\\s+"), " ")
}