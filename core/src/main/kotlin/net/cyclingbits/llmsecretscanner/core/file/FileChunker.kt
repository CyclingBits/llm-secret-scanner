package net.cyclingbits.llmsecretscanner.core.file

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.logger.ScanLogger
import java.io.File

class FileChunker(
    private val config: ScannerConfiguration,
    private val logger: ScanLogger
) {

    fun processFile(file: File): List<FileChunk> {
        val lines = file.readLines()

        // one chunk
        if (lines.size <= config.maxLinesPerChunk) {
            return listOf(
                FileChunk(
                    content = lines.joinToString("\n"),
                    endLine = lines.size,
                )
            )
        }

        // many chunks
        return createChunks(lines)
    }

    private fun createChunks(lines: List<String>): List<FileChunk> {
        val chunks = mutableListOf<FileChunk>()
        val step = config.maxLinesPerChunk - config.chunkOverlapLines
        val totalChunks = (lines.size + step - 1) / step

        for (chunkIndex in 0 until totalChunks) {
            val startIndex = chunkIndex * step
            val endIndex = minOf(startIndex + config.maxLinesPerChunk, lines.size)
            val chunkLines = lines.subList(startIndex, endIndex)
            val content = chunkLines.joinToString("\n")

            chunks.add(
                FileChunk(
                    content = content,
                    startLine = startIndex + 1,
                    endLine = endIndex,
                    chunkIndex = chunkIndex,
                    totalChunks = totalChunks
                )
            )
        }

        return chunks
    }
}