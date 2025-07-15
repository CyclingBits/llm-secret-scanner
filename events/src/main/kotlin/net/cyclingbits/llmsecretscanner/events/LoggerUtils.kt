package net.cyclingbits.llmsecretscanner.events

import java.io.File
import java.time.Duration

object LoggerUtils {

    private const val SECRET_DISPLAY_LENGTH = 25

    fun Duration.toSecondsString(): String =
        String.format("%.1f", this.toMillis() / 1000.0)

    fun truncateSecret(secretValue: String?): String {
        val value = secretValue ?: "No secret value"
        return if (value.length > SECRET_DISPLAY_LENGTH) {
            value.take(SECRET_DISPLAY_LENGTH) + "..."
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