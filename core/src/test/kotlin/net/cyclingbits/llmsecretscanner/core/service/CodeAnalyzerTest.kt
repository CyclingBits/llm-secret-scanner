package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.exception.AnalysisException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
    private lateinit var mockReporter: ScanReporter

    @BeforeEach
    fun setUp() {
        testDir = createTempDir()
        testDir.mkdirs()
        
        config = ScannerConfiguration(
            sourceDirectory = testDir,
            modelName = "ai/phi4:latest",
            timeout = 60_000,
            maxFileSizeBytes = 10_000
        )
        
        mockContainer = mockk<DockerModelRunnerContainer>()
        every { mockContainer.openAIEndpoint } returns "http://localhost:8080"
        
        mockReporter = mockk<ScanReporter>(relaxed = true)
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
        
        val analyzer = CodeAnalyzer(config, mockContainer, mockReporter)
        
        try {
            analyzer.analyzeFiles(files)
        } catch (e: Exception) {
            // Expected to fail due to mock container, but should report progress
        }
        
        verify { mockReporter.reportAnalysisStart(2) }
    }

    @Test
    fun analyzeFiles_withEmptyFileList_reportsZeroFiles() {
        val analyzer = CodeAnalyzer(config, mockContainer, mockReporter)
        
        val issues = analyzer.analyzeFiles(emptyList())
        
        verify { mockReporter.reportAnalysisStart(0) }
        assertTrue(issues.isEmpty())
    }

    @Test
    fun analyzeFiles_withFileTooLarge_throwsException() {
        val largeFile = File(testDir, "Large.java")
        val largeContent = "// ".repeat(10_000) + "password=\"secret\""
        largeFile.writeText(largeContent)
        
        val analyzer = CodeAnalyzer(config, mockContainer, mockReporter)
        
        assertThrows<AnalysisException> {
            analyzer.analyzeFiles(listOf(largeFile))
        }
    }

    @Test
    fun analyzeFiles_withCustomSystemPrompt_createsAnalyzer() {
        val customPrompt = "Find API keys only"
        val configWithPrompt = config.copy(systemPrompt = customPrompt)
        
        val file = File(testDir, "Test.java")
        file.writeText("public class Test { private String key = \"secret\"; }")
        
        val analyzer = CodeAnalyzer(configWithPrompt, mockContainer, mockReporter)
        
        assertEquals(customPrompt, configWithPrompt.systemPrompt)
        assertNotNull(analyzer)
    }

    @Test
    fun analyzeFiles_withNullSystemPrompt_createsAnalyzer() {
        val file = File(testDir, "Test.java")
        file.writeText("public class Test { private String key = \"secret\"; }")
        
        val analyzer = CodeAnalyzer(config, mockContainer, mockReporter)
        
        assertNull(config.systemPrompt)
        assertNotNull(analyzer)
    }

    @Test
    fun analyzeFiles_withConnectionFailure_throwsException() {
        val file = File(testDir, "Test.java")
        file.writeText("public class Test { private String key = \"secret\"; }")
        
        val analyzer = CodeAnalyzer(config, mockContainer, mockReporter)
        
        assertThrows<Exception> {
            analyzer.analyzeFiles(listOf(file))
        }
    }

    @Test
    fun analyzeFiles_withInvalidContainer_throwsException() {
        val file = File(testDir, "Test.java")
        file.writeText("public class Test { private String key = \"secret\"; }")
        
        every { mockContainer.openAIEndpoint } returns "invalid-url"
        
        val analyzer = CodeAnalyzer(config, mockContainer, mockReporter)
        
        assertThrows<Exception> {
            analyzer.analyzeFiles(listOf(file))
        }
    }

    private fun createTempDir(): File {
        val tempDir = File.createTempFile("analyzer-test", "dir")
        tempDir.delete()
        return tempDir
    }
}