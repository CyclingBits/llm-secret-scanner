package net.cyclingbits.llmsecretscanner.events

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue
import org.slf4j.helpers.MessageFormatter as Slf4jFormatter

object EventStore {
    private val events = ConcurrentLinkedQueue<ScanEvent>()
    private val logger: Logger = LoggerFactory.getLogger("ScanLogger")
    private const val MAX_EVENTS = 1_000_000

    fun log(
        type: EventType,
        level: LogLevel = LogLevel.INFO,
        data: Map<String, Any?> = emptyMap(),
        error: Throwable? = null,
        messageBuilder: MessageBuilder.() -> Unit
    ) {
        val builder = MessageBuilder().apply(messageBuilder)
        val event = scanEvent(
            type = type,
            level = level,
            messages = builder.build(),
            data = data,
            error = error
        )
        add(event)
    }

    fun add(event: ScanEvent) {
        when (event.level) {
            LogLevel.DEBUG -> event.messages.forEach { logger.debug(it) }
            LogLevel.INFO -> event.messages.forEach { logger.info(it) }
            LogLevel.WARN -> event.messages.forEach { logger.warn(it) }
            LogLevel.ERROR -> event.messages.forEach { logger.error(it) }
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