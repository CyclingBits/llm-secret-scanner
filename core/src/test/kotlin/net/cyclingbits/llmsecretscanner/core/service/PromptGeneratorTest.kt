package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.model.FileChunk
import net.cyclingbits.llmsecretscanner.core.service.PromptGenerator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import java.io.File

class PromptGeneratorTest {

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
    fun createFilePrompt_includesFilePathAndContent() {
        val file = File(testDir, "example.java")
        file.writeText("class Test {}")
        
        val result = PromptGenerator.createFilePrompt(file)
        
        assertTrue(result.contains(file.path))
        assertTrue(result.contains("Analyze the following code from file"))
        assertTrue(result.contains("```"))
        assertTrue(result.contains("class Test"))
    }

    @Test
    fun createChunkPrompt_includesCorrectLineNumbers() {
        val file = File(testDir, "chunk.java")
        val chunk = FileChunk(
            content = "line1\nline2\nline3",
            startLine = 10,
            endLine = 12,
            chunkIndex = 0,
            totalChunks = 1
        )
        
        val result = PromptGenerator.createChunkPrompt(file, chunk)
        
        assertTrue(result.contains(" 10: line1"))
        assertTrue(result.contains(" 11: line2"))
        assertTrue(result.contains(" 12: line3"))
    }

    @Test
    fun createChunkPrompt_includesFilePath() {
        val file = File(testDir, "chunk.kt")
        val chunk = FileChunk(
            content = "content",
            startLine = 1,
            endLine = 1,
            chunkIndex = 0,
            totalChunks = 1
        )
        
        val result = PromptGenerator.createChunkPrompt(file, chunk)
        
        assertTrue(result.contains(file.path))
        assertTrue(result.contains("Analyze the following code from file"))
    }

    private fun createTempDir(): File {
        val tempDir = File.createTempFile("test", "dir")
        tempDir.delete()
        tempDir.mkdirs()
        return tempDir
    }
}