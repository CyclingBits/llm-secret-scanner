package net.cyclingbits.llmsecretscanner.events

interface Logger {
    val eventSource: EventSource

    fun truncateAndClean(text: String, maxLength: Int = 100): String {
        val cleaned = text.replace("\n", " ").replace("\r", "")
        return if (cleaned.length > maxLength) {
            cleaned.take(maxLength) + "..."
        } else cleaned
    }

    fun reportWarning(message: String, data: Any? = null) {
        EventStore.log(
            source = eventSource,
            type = EventType.SCANNER,
            level = LogLevel.WARN,
            data = mapOf("message" to message, "data" to data)
        ) {
            add("       {} {}", LoggerColors.WARNING_ICON, LoggerColors.yellow(message))
        }
    }

    fun reportError(message: String, exception: Throwable? = null) {
        EventStore.log(
            source = eventSource,
            type = EventType.SCANNER,
            level = LogLevel.ERROR,
            data = mapOf("message" to message),
            error = exception
        ) {
            add("{} {}", LoggerColors.ERROR_ICON, LoggerColors.boldRed(message))
        }
    }


}