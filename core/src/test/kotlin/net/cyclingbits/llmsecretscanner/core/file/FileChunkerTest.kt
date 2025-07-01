package net.cyclingbits.llmsecretscanner.core.file

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.logger.ScanLogger
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class FileChunkerTest {

    @TempDir
    lateinit var tempDir: Path
    
    private val logger = mockk<ScanLogger>(relaxed = true)
    
    private val config = ScannerConfiguration(
        sourceDirectories = listOf(File(".")),
        modelName = "ai/test-model:latest",
        maxLinesPerChunk = 3,
        chunkOverlapLines = 1
    )
    
    private val chunker = FileChunker(config, logger)

    @Test
    fun `should chunk file with overlap`() {
        val file = File(tempDir.toFile(), "test.kt")
        file.writeText("line1\nline2\nline3\nline4\nline5")
        
        val chunks = chunker.processFile(file)
        
        assertTrue(chunks.size >= 2)
        assertEquals("line1\nline2\nline3", chunks[0].content)
        assertEquals(1, chunks[0].startLine)
    }

    @Test
    fun `should handle file smaller than chunk size`() {
        val file = File(tempDir.toFile(), "small.kt")
        file.writeText("line1\nline2")
        
        val chunks = chunker.processFile(file)
        
        assertEquals(1, chunks.size)
        assertEquals("line1\nline2", chunks[0].content)
        assertEquals(1, chunks[0].startLine)
    }

    @Test
    fun `should handle empty file`() {
        val file = File(tempDir.toFile(), "empty.kt")
        file.writeText("")
        
        val chunks = chunker.processFile(file)
        
        assertEquals(1, chunks.size)
        assertEquals("", chunks[0].content)
    }
}