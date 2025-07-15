package net.cyclingbits.llmsecretscanner.events

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue
import org.slf4j.helpers.MessageFormatter as Slf4jFormatter

object EventStore {
    private val events = ConcurrentLinkedQueue<ScanEvent>()
    private const val MAX_EVENTS = 1_000_000
    private var enabledLogSources: Set<EventSource> = EventSource.entries.toSet()
    private var minLogLevel: LogLevel = LogLevel.DEBUG

    fun log(
        source: EventSource,
        type: EventType,
        level: LogLevel = LogLevel.INFO,
        data: Map<String, Any?> = emptyMap(),
        error: Throwable? = null,
        messageBuilder: MessageBuilder.() -> Unit
    ) {
        val builder = MessageBuilder().apply(messageBuilder)
        val event = scanEvent(
            source = source,
            type = type,
            level = level,
            messages = builder.build(),
            data = data,
            error = error
        )
        add(event, source)
    }

    fun add(event: ScanEvent, source: EventSource) {
        if (source in enabledLogSources && isLogLevelEnabled(event.level)) {
            when (event.level) {
                LogLevel.DEBUG -> event.messages.forEach { getLoggerForSource(source).debug(it) }
                LogLevel.INFO -> event.messages.forEach { getLoggerForSource(source).info(it) }
                LogLevel.WARN -> event.messages.forEach { getLoggerForSource(source).warn(it) }
                LogLevel.ERROR -> event.messages.forEach { getLoggerForSource(source).error(it) }
            }
        }
        events.offer(event)
        while (events.size > MAX_EVENTS) {
            events.poll()
        }
    }

    fun getAll(): List<ScanEvent> = events.toList()

    fun clear() {
        events.clear()
    }

    fun size(): Int = events.size

    fun setEnabledLogSources(sources: Set<EventSource>) {
        enabledLogSources = sources
    }
    
    fun setMinLogLevel(level: LogLevel) {
        minLogLevel = level
    }
    
    private fun isLogLevelEnabled(level: LogLevel): Boolean {
        return level.ordinal >= minLogLevel.ordinal
    }

    fun getLoggerForSource(source: EventSource): Logger =
        LoggerFactory.getLogger(source.name.padEnd(9))
}

class MessageBuilder {
    private val messages = mutableListOf<String>()

    fun add(pattern: String, vararg args: Any?) {
        val formatted = format(pattern, *args)
        messages.add(formatted)
    }

    fun addEmpty() {
        messages.add("")
    }

    fun build(): List<String> = messages.toList()

    private fun format(pattern: String, vararg args: Any?): String =
        Slf4jFormatter.arrayFormat(pattern, args).message
}