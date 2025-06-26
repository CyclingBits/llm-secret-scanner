package net.cyclingbits.llmsecretscanner.core.file

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.logger.ScanLogger
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class FileFinderTest {

    @TempDir
    lateinit var tempDir: Path
    
    private val logger = mockk<ScanLogger>(relaxed = true)

    @Test
    fun `should find files matching include pattern`() {
        val srcDir = File(tempDir.toFile(), "src").apply { mkdirs() }
        File(srcDir, "Main.java").writeText("public class Main {}")
        File(srcDir, "Test.kt").writeText("class Test")
        File(srcDir, "config.xml").writeText("<config/>")
        
        val config = ScannerConfiguration(
            sourceDirectories = listOf(srcDir),
            modelName = "ai/test:latest",
            includes = "**/*.java,**/*.kt"
        )
        
        val finder = FileFinder(config, logger)
        val files = finder.findFiles(listOf(srcDir))
        
        assertEquals(2, files.size)
        assertTrue(files.any { it.name == "Main.java" })
        assertTrue(files.any { it.name == "Test.kt" })
        assertFalse(files.any { it.name == "config.xml" })
    }

    @Test
    fun `should exclude files matching exclude pattern`() {
        val srcDir = File(tempDir.toFile(), "src").apply { mkdirs() }
        val targetDir = File(srcDir, "target").apply { mkdirs() }
        
        File(srcDir, "Main.java").writeText("public class Main {}")
        File(targetDir, "Generated.java").writeText("public class Generated {}")
        
        val config = ScannerConfiguration(
            sourceDirectories = listOf(srcDir),
            modelName = "ai/test:latest",
            includes = "**/*.java",
            excludes = "**/target/**"
        )
        
        val finder = FileFinder(config, logger)
        val files = finder.findFiles(listOf(srcDir))
        
        assertEquals(1, files.size)
        assertTrue(files.any { it.name == "Main.java" })
        assertFalse(files.any { it.name == "Generated.java" })
    }

    @Test
    fun `should handle empty directory`() {
        val emptyDir = File(tempDir.toFile(), "empty").apply { mkdirs() }
        
        val config = ScannerConfiguration(
            sourceDirectories = listOf(emptyDir),
            modelName = "ai/test:latest"
        )
        
        val finder = FileFinder(config, logger)
        val files = finder.findFiles(listOf(emptyDir))
        
        assertTrue(files.isEmpty())
    }

    @Test
    fun `should combine files from multiple directories`() {
        val dir1 = File(tempDir.toFile(), "dir1").apply { mkdirs() }
        val dir2 = File(tempDir.toFile(), "dir2").apply { mkdirs() }
        
        File(dir1, "File1.java").writeText("class File1 {}")
        File(dir2, "File2.java").writeText("class File2 {}")
        
        val config = ScannerConfiguration(
            sourceDirectories = listOf(dir1, dir2),
            modelName = "ai/test:latest",
            includes = "**/*.java"
        )
        
        val finder = FileFinder(config, logger)
        val files = finder.findFiles(listOf(dir1, dir2))
        
        assertEquals(2, files.size)
        assertTrue(files.any { it.name == "File1.java" })
        assertTrue(files.any { it.name == "File2.java" })
    }
}