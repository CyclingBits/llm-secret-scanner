package net.cyclingbits.llmsecretscanner.core.file

data class FileChunk(
    val content: String,
    val startLine: Int = 1,
    val endLine: Int,
    val chunkIndex: Int = 0,
    val totalChunks: Int = 1
)