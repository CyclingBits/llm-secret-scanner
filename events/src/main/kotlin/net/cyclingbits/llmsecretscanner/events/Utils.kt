package net.cyclingbits.llmsecretscanner.events

import java.util.Locale

object Utils {
    fun loadResource(resourcePath: String): String {
        return Thread.currentThread().contextClassLoader
            .getResourceAsStream(resourcePath)
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: throw IllegalStateException("Could not load resource: $resourcePath")
    }
    
    fun loadResourceOrNull(resourcePath: String): String? {
        return Thread.currentThread().contextClassLoader
            .getResourceAsStream(resourcePath)
            ?.bufferedReader()
            ?.use { it.readText() }
    }
    
    fun formatPercent(value: Double): String = "%.1f".format(Locale.US, value)
    
    fun stripMarkdown(text: String): String {
        return text
            // Remove headers
            .replace(Regex("^#{1,6}\\s+", RegexOption.MULTILINE), "")
            // Remove bold
            .replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1")
            // Remove italic  
            .replace(Regex("\\*([^*]+)\\*"), "$1")
            // Remove code blocks
            .replace(Regex("```[^`]*```", RegexOption.DOT_MATCHES_ALL), "")
            // Remove inline code
            .replace(Regex("`([^`]+)`"), "$1")
            // Remove bullet points
            .replace(Regex("^[-*]\\s+", RegexOption.MULTILINE), "")
            // Clean up extra newlines
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()
    }
}