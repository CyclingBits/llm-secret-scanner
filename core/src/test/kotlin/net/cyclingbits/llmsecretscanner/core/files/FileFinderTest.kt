package net.cyclingbits.llmsecretscanner.core.files

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.files.FileFinder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.File

class FileFinderTest {

    @Test
    fun findFiles_javaFiles_returnsMatching() {
        val tempDir = createTempDir()
        val javaFile = File(tempDir, "Test.java").apply { writeText("class Test {}") }
        val kotlinFile = File(tempDir, "Test.kt").apply { writeText("class Test") }

        val config = ScannerConfiguration(
            sourceDirectories = listOf(tempDir),
            modelName = "ai/phi4:latest",
            includes = "**/*.java"
        )
        val scanner = FileFinder(config)
        
        val result = scanner.findFiles(tempDir)
        
        assertEquals(1, result.size)
        assertEquals(javaFile.name, result[0].name)
        
        tempDir.deleteRecursively()
    }

    @Test
    fun findFiles_excludePattern_filtersOut() {
        val tempDir = createTempDir()
        val targetDir = File(tempDir, "target").apply { mkdirs() }
        val srcFile = File(tempDir, "src.java").apply { writeText("class Src {}") }
        val targetFile = File(targetDir, "target.java").apply { writeText("class Target {}") }

        val config = ScannerConfiguration(
            sourceDirectories = listOf(tempDir),
            modelName = "ai/phi4:latest",
            includes = "**/*.java",
            excludes = "**/target/**"
        )
        val scanner = FileFinder(config)
        
        val result = scanner.findFiles(tempDir)
        
        assertEquals(1, result.size)
        assertEquals(srcFile.name, result[0].name)
        
        tempDir.deleteRecursively()
    }

    @Test
    fun findFiles_multipleIncludes_returnsAll() {
        val tempDir = createTempDir()
        val javaFile = File(tempDir, "Test.java").apply { writeText("class Test {}") }
        val kotlinFile = File(tempDir, "Test.kt").apply { writeText("class Test") }
        val txtFile = File(tempDir, "readme.txt").apply { writeText("readme") }

        val config = ScannerConfiguration(
            sourceDirectories = listOf(tempDir),
            modelName = "ai/phi4:latest",
            includes = "**/*.java,**/*.kt"
        )
        val scanner = FileFinder(config)
        
        val result = scanner.findFiles(tempDir)
        
        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "Test.java" })
        assertTrue(result.any { it.name == "Test.kt" })
        
        tempDir.deleteRecursively()
    }

    @Test
    fun findFiles_emptyDirectory_returnsEmpty() {
        val tempDir = createTempDir()
        
        val config = ScannerConfiguration(
            sourceDirectories = listOf(tempDir),
            modelName = "ai/phi4:latest"
        )
        val scanner = FileFinder(config)
        
        val result = scanner.findFiles(tempDir)
        
        assertEquals(0, result.size)
        
        tempDir.deleteRecursively()
    }

    @Test
    fun findFiles_noMatching_returnsEmpty() {
        val tempDir = createTempDir()
        File(tempDir, "readme.txt").apply { writeText("readme") }
        
        val config = ScannerConfiguration(
            sourceDirectories = listOf(tempDir),
            modelName = "ai/phi4:latest",
            includes = "**/*.java"
        )
        val scanner = FileFinder(config)
        
        val result = scanner.findFiles(tempDir)
        
        assertEquals(0, result.size)
        
        tempDir.deleteRecursively()
    }

    private fun createTempDir(): File {
        val tempDir = File.createTempFile("test", "dir")
        tempDir.delete()
        tempDir.mkdirs()
        return tempDir
    }
}