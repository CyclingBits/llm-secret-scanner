package net.cyclingbits.llmsecretscanner.core.logger

import net.cyclingbits.llmsecretscanner.core.event.EventType
import net.cyclingbits.llmsecretscanner.core.event.LogLevel
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class EventStoreTest {

    @BeforeEach
    fun setUp() {
        EventStore.clear()
    }

    @Test
    fun `should store events`() {
        EventStore.log(
            { add("Test message") },
            { 
                type(EventType.FILE)
                level(LogLevel.INFO)
            }
        )
        
        val events = EventStore.getAll()
        assertEquals(1, events.size)
        assertEquals(EventType.FILE, events[0].type)
        assertEquals(LogLevel.INFO, events[0].level)
    }

    @Test
    fun `should clear all events`() {
        EventStore.log(
            { add("Test message") },
            { 
                type(EventType.FILE)
                level(LogLevel.INFO)
            }
        )
        
        assertEquals(1, EventStore.size())
        
        EventStore.clear()
        
        assertEquals(0, EventStore.size())
        assertTrue(EventStore.getAll().isEmpty())
    }

    @Test
    fun `should track event count`() {
        EventStore.log(
            { add("Test message") },
            { 
                type(EventType.FILE)
                level(LogLevel.INFO)
            }
        )
        
        assertEquals(1, EventStore.size())
        
        EventStore.log(
            { add("Another message") },
            { 
                type(EventType.SCANER)
                level(LogLevel.WARN)
            }
        )
        
        assertEquals(2, EventStore.size())
    }

    @Test
    fun `should maintain order of events`() {
        EventStore.log(
            { add("First message") },
            { 
                type(EventType.FILE)
                level(LogLevel.INFO)
            }
        )
        
        EventStore.log(
            { add("Second message") },
            { 
                type(EventType.SCANER)
                level(LogLevel.WARN)
            }
        )
        
        val events = EventStore.getAll()
        assertEquals(2, events.size)
        assertEquals(EventType.FILE, events[0].type)
        assertEquals(EventType.SCANER, events[1].type)
    }
}