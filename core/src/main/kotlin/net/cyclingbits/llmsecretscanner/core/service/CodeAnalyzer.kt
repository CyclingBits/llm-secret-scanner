package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.exception.AnalysisException
import net.cyclingbits.llmsecretscanner.core.model.FileChunk
import net.cyclingbits.llmsecretscanner.core.model.Issue
import org.testcontainers.containers.DockerModelRunnerContainer
import java.io.File
import java.io.FileNotFoundException
import kotlin.math.abs

class CodeAnalyzer(
    private val config: ScannerConfiguration,
    private val container: DockerModelRunnerContainer
) {

    private val modelClient = ModelClient(config, container)
    private val systemPrompt: String by lazy { config.systemPrompt ?: readSystemPrompt() }

    fun analyzeFile(file: File, fileIndex: Int, totalFiles: Int): List<Issue> {
        ScanReporter.reportFileAnalysisStart(file, fileIndex, totalFiles, config.sourceDirectory)
        
        val startTime = System.currentTimeMillis()

        val issues = if (config.enableChunking && FileChunker.shouldChunkFile(file, config.maxLinesPerChunk)) {
            analyzeFileInChunks(file)
        } else {
            analyzeFullFile(file)
        }

        val analysisTime = System.currentTimeMillis() - startTime
        ScanReporter.reportFileIssues(file, issues, analysisTime, fileIndex, totalFiles, config.sourceDirectory)

        return issues
    }
    
    private fun analyzeFileInChunks(file: File): List<Issue> {
        val chunks = FileChunker.chunkFile(file, config.maxLinesPerChunk, config.chunkOverlapLines)
        ScanReporter.reportChunk(chunks.size, config.maxLinesPerChunk, config.chunkOverlapLines)
        val allIssues = mutableListOf<Issue>()
        
        chunks.forEach { chunk ->
            ScanReporter.reportChunkAnalysis(chunk.chunkIndex + 1, chunk.totalChunks, chunk.startLine, chunk.endLine)
            val userPrompt = createChunkPrompt(file, chunk)
            
            val jsonResponse = modelClient.analyze(systemPrompt, userPrompt)
            val chunkIssues = AnalysisResultMapper.parseAnalysisResult(jsonResponse)
            
            allIssues.addAll(chunkIssues)
        }
        
        return deduplicateIssues(allIssues)
    }
    
    private fun analyzeFullFile(file: File): List<Issue> {
        val userPrompt = createUserPrompt(file)

        val jsonResponse = modelClient.analyze(systemPrompt, userPrompt)
        return AnalysisResultMapper.parseAnalysisResult(jsonResponse)
    }

    private fun createUserPrompt(file: File): String {
        if (file.length() > config.maxFileSizeBytes) {
            throw AnalysisException("File ${file.path} is too large (${file.length()} bytes). Maximum size is ${config.maxFileSizeBytes} bytes.")
        }

        val fileContent = file.readText()
        val numberedLines = fileContent.lines().mapIndexed { index, line ->
            "${(index + 1).toString().padStart(3, ' ')}: $line"
        }.joinToString("\n")

        return "Analyze the following code from file '${file.path}':\n```\n$numberedLines\n```"
    }
    
    private fun createChunkPrompt(file: File, chunk: FileChunk): String {
        val numberedLines = chunk.content.lines().mapIndexed { index, line ->
            "${(chunk.startLine + index).toString().padStart(3, ' ')}: $line"
        }.joinToString("\n")

        return "Analyze the following code from file '${file.path}':\n```\n$numberedLines\n```"
    }

    private fun readSystemPrompt(): String {
        return this::class.java.classLoader.getResourceAsStream("system_prompt.md")?.use { stream ->
            stream.bufferedReader().use { it.readText() }
        } ?: throw FileNotFoundException("system_prompt.md not found in resources")
    }

    
    private fun deduplicateIssues(issues: List<Issue>): List<Issue> {
        val uniqueIssues = mutableListOf<Issue>()
        
        for (issue in issues) {
            val isDuplicate = uniqueIssues.any { existing ->
                abs(existing.lineNumber - issue.lineNumber) <= 2 &&
                existing.filePath == issue.filePath &&
                (existing.secretValue?.take(10) ?: "") == (issue.secretValue?.take(10) ?: "")
            }
            
            if (!isDuplicate) {
                uniqueIssues.add(issue)
            }
        }
        
        return uniqueIssues.sortedBy { it.lineNumber }
    }
}