package net.cyclingbits.llmsecretscanner.core.logger

import net.cyclingbits.llmsecretscanner.core.event.LogLevel
import net.cyclingbits.llmsecretscanner.core.event.ScanEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue

object EventStore {
    private val events = ConcurrentLinkedQueue<ScanEvent>()
    private val logger: Logger = LoggerFactory.getLogger("ScanLogger")
    private const val MAX_EVENTS = 1_000_000

    fun log(
        init: MessageBuilder.() -> Unit,
        eventInit: ScanEventBuilder.() -> Unit
    ) {
        val messageBuilder = MessageBuilder().apply(init)
        val event = scanEvent {
            eventInit()
            messageBuilder(messageBuilder)
        }
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