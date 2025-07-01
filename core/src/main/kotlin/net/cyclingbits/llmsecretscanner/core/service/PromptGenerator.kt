package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.file.FileChunk
import java.io.File

internal object PromptGenerator {

    fun createFilePrompt(file: File): String =
        createPrompt(file.path, file.readText().lines(), 1)

    fun createChunkPrompt(file: File, chunk: FileChunk): String =
        createPrompt(file.path, chunk.content.lines(), chunk.startLine)

    private fun createPrompt(filePath: String, lines: List<String>, startLine: Int): String {
        val numberedLines = formatLinesWithNumbers(lines, startLine)
        return "Analyze the following code from file '$filePath':\n```\n$numberedLines\n```"
    }

    private fun formatLinesWithNumbers(lines: List<String>, startLineNumber: Int): String {
        return lines.mapIndexed { index, line ->
            "${(startLineNumber + index).toString().padStart(3, ' ')}: $line"
        }.joinToString("\n")
    }
}