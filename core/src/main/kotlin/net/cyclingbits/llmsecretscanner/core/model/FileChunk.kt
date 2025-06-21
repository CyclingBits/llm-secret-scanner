package net.cyclingbits.llmsecretscanner.core.model

data class FileChunk(
    val content: String,
    val startLine: Int,
    val endLine: Int,
    val chunkIndex: Int,
    val totalChunks: Int
)