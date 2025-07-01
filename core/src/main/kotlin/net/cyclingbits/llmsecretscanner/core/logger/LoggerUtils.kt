package net.cyclingbits.llmsecretscanner.core.logger

import net.cyclingbits.llmsecretscanner.core.config.ScannerDefaults
import net.cyclingbits.llmsecretscanner.core.event.EventType
import net.cyclingbits.llmsecretscanner.core.event.LogLevel
import net.cyclingbits.llmsecretscanner.core.event.ScanEvent
import java.io.File
import java.time.Duration
import org.slf4j.helpers.MessageFormatter as Slf4jFormatter

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// MESSAGE BUILDING
// ═══════════════════════════════════════════════════════════════════════════════════════════════

class MessageBuilder {
    private val messages = mutableListOf<String>()
    private val rawMessages = mutableListOf<String>()

    fun add(pattern: String, vararg args: Any?) {
        val formatted = format(pattern, *args)
        rawMessages.add(formatted.stripAnsiCodes())
        messages.add(formatted)
    }

    private fun String.stripAnsiCodes(): String {
        return this
            .replace(Regex("\u001B\\[[0-9;]*m"), "")
            .filter { char ->
                char.code in 32..126 || char.code in 128..255 || char.isWhitespace()
            }
            .trim().replace(Regex("\\s+"), " ")
    }

    fun addEmpty() {
        rawMessages.add("")
        messages.add("")
    }

    fun build(): List<String> = messages.toList()
    fun buildRaw(): List<String> = rawMessages.toList()

    fun format(pattern: String, vararg args: Any?): String =
        Slf4jFormatter.arrayFormat(pattern, args).message
}

// ═══════════════════════════════════════════════════════════════════════════════════════════════
// EVENT BUILDING
// ═══════════════════════════════════════════════════════════════════════════════════════════════

class ScanEventBuilder {
    private var type: EventType? = null
    private var level: LogLevel = LogLevel.INFO
    private var messageBuilder: MessageBuilder = MessageBuilder()
    private var data: Map<String, Any?> = emptyMap()
    private var error: Throwable? = null

    fun type(type: EventType) = apply { this.type = type }
    fun level(level: LogLevel) = apply { this.level = level }
    fun messageBuilder(messageBuilder: MessageBuilder) = apply { this.messageBuilder = messageBuilder }
    fun data(data: Map<String, Any?>) = apply { this.data = data }
    fun data(vararg pairs: Pair<String, Any?>) = apply { this.data = mapOf(*pairs) }
    fun error(error: Throwable) = apply { this.error = error }

    fun build(): ScanEvent = ScanEvent(
        type = type ?: throw IllegalStateException("Event type must be specified"),
        level = level,
        messageBuilder = messageBuilder,
        data = data,
        error = error
    )
}

fun scanEvent(init: ScanEventBuilder.() -> Unit): ScanEvent =
    ScanEventBuilder().apply(init).build()


// ═══════════════════════════════════════════════════════════════════════════════════════════════
// DOMAIN HELPERS
// ═══════════════════════════════════════════════════════════════════════════════════════════════

internal object LoggerUtils {

    fun Duration.toSecondsString(): String =
        String.format("%.1f", this.toMillis() / 1000.0)

    fun getIssueTypeColor(issueType: String): String = when (issueType.lowercase()) {
        "password" -> LoggerColors.boldRed(issueType)
        "api key", "token" -> LoggerColors.boldYellow(issueType)
        "database credentials" -> LoggerColors.boldRed(issueType)
        "private key" -> LoggerColors.boldRed(issueType)
        "secret" -> LoggerColors.boldYellow(issueType)
        else -> LoggerColors.cyan(issueType)
    }

    fun truncateSecret(secretValue: String?): String {
        val value = secretValue ?: "No secret value"
        return if (value.length > ScannerDefaults.SECRET_DISPLAY_LENGTH) {
            value.take(ScannerDefaults.SECRET_DISPLAY_LENGTH) + "..."
        } else value
    }

    fun getRelativePath(file: File, sourceDirectories: List<File>): String = try {
        findBaseDirectory(file, sourceDirectories).toPath().relativize(file.toPath()).toString()
    } catch (_: IllegalArgumentException) {
        file.name
    }

    private fun findBaseDirectory(file: File, sourceDirectories: List<File>): File {
        return sourceDirectories.find { dir ->
            file.absolutePath.startsWith(dir.absolutePath)
        } ?: sourceDirectories.firstOrNull() ?: File(".")
    }
}