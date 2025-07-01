package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.file.FileChunk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class PromptGeneratorTest {

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `should create file prompt with content`() {
        val file = File(tempDir.toFile(), "test.java")
        file.writeText("public class Test {\n    private String apiKey = \"sk-123456\";\n}")
        
        val prompt = PromptGenerator.createFilePrompt(file)
        
        assertTrue(prompt.contains("test.java"))
        assertTrue(prompt.contains("sk-123456"))
        assertTrue(prompt.contains("  1:"))
    }

    @Test
    fun `should create chunk prompt with line numbers`() {
        val file = File("config.properties")
        val chunk = FileChunk(
            content = "api.key=secret123\ndb.password=admin",
            startLine = 42,
            endLine = 43
        )
        
        val prompt = PromptGenerator.createChunkPrompt(file, chunk)
        
        assertTrue(prompt.contains("config.properties"))
        assertTrue(prompt.contains("secret123"))
        assertTrue(prompt.contains(" 42:"))
        assertTrue(prompt.contains(" 43:"))
    }
}