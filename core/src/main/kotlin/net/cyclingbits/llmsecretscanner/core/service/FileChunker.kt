package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.model.FileChunk
import java.io.File

object FileChunker {
    
    fun chunkFile(file: File, maxLinesPerChunk: Int, overlapLines: Int = 0): List<FileChunk> {
        val lines = file.readLines()
        
        if (lines.size <= maxLinesPerChunk) {
            return listOf(
                FileChunk(
                    content = lines.joinToString("\n"),
                    startLine = 1,
                    endLine = lines.size,
                    chunkIndex = 0,
                    totalChunks = 1
                )
            )
        }
        
        val chunks = mutableListOf<FileChunk>()
        val step = maxLinesPerChunk - overlapLines
        val totalChunks = (lines.size + step - 1) / step
        
        for (chunkIndex in 0 until totalChunks) {
            val startIndex = chunkIndex * step
            val endIndex = minOf(startIndex + maxLinesPerChunk, lines.size)
            val chunkLines = lines.subList(startIndex, endIndex)
            
            chunks.add(
                FileChunk(
                    content = chunkLines.joinToString("\n"),
                    startLine = startIndex + 1,
                    endLine = endIndex,
                    chunkIndex = chunkIndex,
                    totalChunks = totalChunks
                )
            )
        }
        
        return chunks
    }
    
    fun shouldChunkFile(file: File, maxLinesPerChunk: Int): Boolean {
        return file.readLines().size > maxLinesPerChunk
    }
}