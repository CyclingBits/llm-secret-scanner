package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.model.FileChunk
import java.io.File

object PromptGenerator {

    fun createFilePrompt(file: File): String {
        val fileContent = file.readText()
        val numberedLines = formatLinesWithNumbers(fileContent.lines(), 1)
        return "Analyze the following code from file '${file.path}':\n```\n$numberedLines\n```"
    }

    fun createChunkPrompt(file: File, chunk: FileChunk): String {
        val numberedLines = formatLinesWithNumbers(chunk.content.lines(), chunk.startLine)
        return "Analyze the following code from file '${file.path}':\n```\n$numberedLines\n```"
    }

    private fun formatLinesWithNumbers(lines: List<String>, startLineNumber: Int): String {
        return lines.mapIndexed { index, line ->
            "${(startLineNumber + index).toString().padStart(3, ' ')}: $line"
        }.joinToString("\n")
    }
}