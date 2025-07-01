package net.cyclingbits.llmsecretscanner.core.event

import com.fasterxml.jackson.annotation.JsonIgnore
import net.cyclingbits.llmsecretscanner.core.logger.MessageBuilder
import net.cyclingbits.llmsecretscanner.core.parser.toFormattedJsonString
import java.time.Instant

data class ScanEvent(
    val timestamp: Instant = Instant.now(),
    val type: EventType,
    @get:JsonIgnore
    val messageBuilder: MessageBuilder = MessageBuilder(),
    val level: LogLevel,
    val data: Map<String, Any?> = emptyMap(),
    val error: Throwable? = null
) {
    @get:JsonIgnore
    val messages: List<String> get() = messageBuilder.build()

    val rawMessages: List<String> get() = messageBuilder.buildRaw()

    override fun toString(): String {
        return this.toFormattedJsonString()
    }
}

enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
}