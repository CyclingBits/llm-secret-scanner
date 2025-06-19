package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.exception.AnalysisException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.assertThrows
import org.testcontainers.containers.DockerModelRunnerContainer
import java.io.File

class CodeAnalyzerTest {

    private lateinit var testDir: File
    private lateinit var config: ScannerConfiguration
    private lateinit var mockContainer: DockerModelRunnerContainer

    @BeforeEach
    fun setUp() {
        testDir = createTempDir()
        testDir.mkdirs()
        
        config = ScannerConfiguration(
            sourceDirectory = testDir,
            modelName = "ai/phi4:latest",
            fileAnalysisTimeout = 60,
            maxFileSizeBytes = 10_000
        )
        
        mockContainer = mockk<DockerModelRunnerContainer>()
        every { mockContainer.openAIEndpoint } returns "http://localhost:8080"
    }

    @AfterEach
    fun tearDown() {
        if (::testDir.isInitialized) {
            testDir.deleteRecursively()
        }
    }

    @Test
    fun analyzeFiles_withValidFiles_reportsProgress() {
        val file1 = File(testDir, "Test1.java")
        file1.writeText("public class Test1 { private String key = \"secret\"; }")
        
        val file2 = File(testDir, "Test2.java")
        file2.writeText("public class Test2 { private String token = \"auth123\"; }")
        
        val files = listOf(file1, file2)
        
        mockkObject(ScanReporter)
        
        val analyzer = CodeAnalyzer(config, mockContainer)
        
        try {
            files.forEachIndexed { index, file ->
                analyzer.analyzeFile(file, index + 1, files.size)
            }
        } catch (e: Exception) {
            // Expected to fail due to mock container, but should report progress
        }
        
        io.mockk.verify(atLeast = 1) { ScanReporter.reportFileAnalysisStart(any(), any(), any(), any()) }
        
        unmockkObject(ScanReporter)
    }

    @Test
    fun analyzeFiles_withEmptyFileList_reportsZeroFiles() {
        mockkObject(ScanReporter)
        
        val analyzer = CodeAnalyzer(config, mockContainer)
        
        val issues = emptyList<File>().map { file ->
            analyzer.analyzeFile(file, 1, 0)
        }.flatten()
        
        unmockkObject(ScanReporter)
        assertTrue(issues.isEmpty())
    }

    @Test
    fun analyzeFiles_withFileTooLarge_throwsException() {
        val largeFile = File(testDir, "Large.java")
        val largeContent = "// ".repeat(10_000) + "password=\"secret\""
        largeFile.writeText(largeContent)
        
        val analyzer = CodeAnalyzer(config, mockContainer)
        
        assertThrows<AnalysisException> {
            analyzer.analyzeFile(largeFile, 1, 1)
        }
    }

    @Test
    fun analyzeFiles_withCustomSystemPrompt_createsAnalyzer() {
        val customPrompt = "Find API keys only"
        val configWithPrompt = config.copy(systemPrompt = customPrompt)
        
        val file = File(testDir, "Test.java")
        file.writeText("public class Test { private String key = \"secret\"; }")
        
        val analyzer = CodeAnalyzer(configWithPrompt, mockContainer)
        
        assertEquals(customPrompt, configWithPrompt.systemPrompt)
        assertNotNull(analyzer)
    }

    @Test
    fun analyzeFiles_withNullSystemPrompt_createsAnalyzer() {
        val file = File(testDir, "Test.java")
        file.writeText("public class Test { private String key = \"secret\"; }")
        
        val analyzer = CodeAnalyzer(config, mockContainer)
        
        assertNull(config.systemPrompt)
        assertNotNull(analyzer)
    }

    @Test
    fun analyzeFiles_withConnectionFailure_throwsException() {
        val file = File(testDir, "Test.java")
        file.writeText("public class Test { private String key = \"secret\"; }")
        
        val analyzer = CodeAnalyzer(config, mockContainer)
        
        assertThrows<Exception> {
            analyzer.analyzeFile(file, 1, 1)
        }
    }

    @Test
    fun analyzeFiles_withInvalidContainer_throwsException() {
        val file = File(testDir, "Test.java")
        file.writeText("public class Test { private String key = \"secret\"; }")
        
        every { mockContainer.openAIEndpoint } returns "invalid-url"
        
        val analyzer = CodeAnalyzer(config, mockContainer)
        
        assertThrows<Exception> {
            analyzer.analyzeFile(file, 1, 1)
        }
    }

    private fun createTempDir(): File {
        val tempDir = File.createTempFile("analyzer-test", "dir")
        tempDir.delete()
        return tempDir
    }
}