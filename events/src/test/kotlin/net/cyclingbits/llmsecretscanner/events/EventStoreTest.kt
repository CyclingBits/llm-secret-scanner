package net.cyclingbits.llmsecretscanner.events

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EventStoreTest {

    @BeforeEach
    fun setUp() {
        EventStore.clear()
    }

    @Test
    fun `should store events`() {
        EventStore.log(
            type = EventType.FILE,
            level = LogLevel.INFO
        ) {
            add("Test message")
        }

        val events = EventStore.getAll()
        assertEquals(1, events.size)
        assertEquals(EventType.FILE, events[0].type)
        assertEquals(LogLevel.INFO, events[0].level)
    }

    @Test
    fun `should clear all events`() {
        EventStore.log(
            type = EventType.FILE,
            level = LogLevel.INFO
        ) {
            add("Test message")
        }

        assertEquals(1, EventStore.size())

        EventStore.clear()

        assertEquals(0, EventStore.size())
        assertTrue(EventStore.getAll().isEmpty())
    }

    @Test
    fun `should track event count`() {
        EventStore.log(
            type = EventType.FILE,
            level = LogLevel.INFO
        ) {
            add("Test message")
        }

        assertEquals(1, EventStore.size())

        EventStore.log(
            type = EventType.SCANNER,
            level = LogLevel.WARN
        ) {
            add("Another message")
        }

        assertEquals(2, EventStore.size())
    }

    @Test
    fun `should maintain order of events`() {
        EventStore.log(
            type = EventType.FILE,
            level = LogLevel.INFO
        ) {
            add("First message")
        }

        EventStore.log(
            type = EventType.SCANNER,
            level = LogLevel.WARN
        ) {
            add("Second message")
        }

        val events = EventStore.getAll()
        assertEquals(2, events.size)
        assertEquals(EventType.FILE, events[0].type)
        assertEquals(EventType.SCANNER, events[1].type)
    }
}