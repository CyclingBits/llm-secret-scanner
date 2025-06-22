package net.cyclingbits.llmsecretscanner.core.files

import net.cyclingbits.llmsecretscanner.core.files.FileChunker
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import java.io.File

class FileChunkerTest {

    private lateinit var testDir: File

    @BeforeEach
    fun setUp() {
        testDir = createTempDir()
        testDir.mkdirs()
    }

    @AfterEach
    fun tearDown() {
        if (::testDir.isInitialized) {
            testDir.deleteRecursively()
        }
    }

    @Test
    fun shouldChunkFile_largeFile_returnsTrue() {
        val file = File(testDir, "large.java")
        val content = (1..50).map { "line $it" }.joinToString("\n")
        file.writeText(content)

        val result = FileChunker.shouldChunkFile(file, maxLinesPerChunk = 40)

        assertTrue(result)
    }

    @Test
    fun shouldChunkFile_smallFile_returnsFalse() {
        val file = File(testDir, "small.java")
        val content = (1..20).map { "line $it" }.joinToString("\n")
        file.writeText(content)

        val result = FileChunker.shouldChunkFile(file, maxLinesPerChunk = 40)

        assertFalse(result)
    }

    @Test
    fun shouldChunkFile_exactLimit_returnsFalse() {
        val file = File(testDir, "exact.java")
        val content = (1..40).map { "line $it" }.joinToString("\n")
        file.writeText(content)

        val result = FileChunker.shouldChunkFile(file, maxLinesPerChunk = 40)

        assertFalse(result)
    }

    @Test
    fun chunkFile_singleChunk_returnsOneChunk() {
        val file = File(testDir, "single.java")
        val content = (1..30).map { "line $it" }.joinToString("\n")
        file.writeText(content)

        val chunks = FileChunker.chunkFile(file, maxLinesPerChunk = 40, overlapLines = 5)

        assertEquals(1, chunks.size)
        assertEquals(1, chunks[0].startLine)
        assertEquals(30, chunks[0].endLine)
        assertEquals(0, chunks[0].chunkIndex)
        assertEquals(1, chunks[0].totalChunks)
    }

    @Test
    fun chunkFile_multipleChunks_returnsCorrectChunks() {
        val file = File(testDir, "multiple.java")
        val content = (1..100).map { "line $it" }.joinToString("\n")
        file.writeText(content)

        val chunks = FileChunker.chunkFile(file, maxLinesPerChunk = 40, overlapLines = 5)

        assertEquals(3, chunks.size)
        
        // First chunk
        assertEquals(1, chunks[0].startLine)
        assertEquals(40, chunks[0].endLine)
        assertEquals(0, chunks[0].chunkIndex)
        assertEquals(3, chunks[0].totalChunks)
        
        // Second chunk (with overlap)
        assertEquals(36, chunks[1].startLine) // 40 - 5 + 1
        assertEquals(75, chunks[1].endLine)   // 36 + 40 - 1
        assertEquals(1, chunks[1].chunkIndex)
        assertEquals(3, chunks[1].totalChunks)
        
        // Third chunk
        assertEquals(71, chunks[2].startLine) // 75 - 5 + 1
        assertEquals(100, chunks[2].endLine)
        assertEquals(2, chunks[2].chunkIndex)
        assertEquals(3, chunks[2].totalChunks)
    }

    @Test
    fun chunkFile_withOverlap_correctContent() {
        val file = File(testDir, "overlap.java")
        val content = (1..60).map { "line $it" }.joinToString("\n")
        file.writeText(content)

        val chunks = FileChunker.chunkFile(file, maxLinesPerChunk = 40, overlapLines = 5)

        assertEquals(2, chunks.size)
        
        // Verify content overlap
        val firstChunkLines = chunks[0].content.lines()
        val secondChunkLines = chunks[1].content.lines()
        
        assertEquals("line 40", firstChunkLines.last())
        assertEquals("line 36", secondChunkLines.first())
        assertTrue(secondChunkLines.contains("line 40"))
    }

    @Test
    fun chunkFile_emptyFile_returnsEmptyChunk() {
        val file = File(testDir, "empty.java")
        file.writeText("")

        val chunks = FileChunker.chunkFile(file, maxLinesPerChunk = 40, overlapLines = 5)

        assertEquals(1, chunks.size)
        assertEquals(1, chunks[0].startLine)
        assertEquals(0, chunks[0].endLine)
        assertTrue(chunks[0].content.isEmpty())
    }

    private fun createTempDir(): File {
        val tempDir = File.createTempFile("test", "dir")
        tempDir.delete()
        tempDir.mkdirs()
        return tempDir
    }
}