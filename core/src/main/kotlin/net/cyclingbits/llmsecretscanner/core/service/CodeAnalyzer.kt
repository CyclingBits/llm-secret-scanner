package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.config.ScannerDefaults
import net.cyclingbits.llmsecretscanner.core.exception.AnalysisException
import net.cyclingbits.llmsecretscanner.core.files.FileChunker
import net.cyclingbits.llmsecretscanner.core.llm.IssueParser
import net.cyclingbits.llmsecretscanner.core.llm.ModelClient
import net.cyclingbits.llmsecretscanner.core.model.Issue
import net.cyclingbits.llmsecretscanner.core.util.ScanReporter
import org.testcontainers.containers.DockerModelRunnerContainer
import java.io.File

class CodeAnalyzer(
    private val config: ScannerConfiguration,
    private val container: DockerModelRunnerContainer
) {

    private val modelClient = ModelClient(config, container)

    fun analyzeFile(file: File, fileIndex: Int, totalFiles: Int): List<Issue> {
        ScanReporter.reportFileAnalysisStart(file, fileIndex, totalFiles, config.sourceDirectories)
        
        val startTime = System.currentTimeMillis()

        val issues = if (config.enableChunking && FileChunker.shouldChunkFile(file, config.maxLinesPerChunk)) {
            analyzeFileInChunks(file)
        } else {
            analyzeFullFile(file)
        }

        val analysisTime = System.currentTimeMillis() - startTime
        ScanReporter.reportFileIssues(file, issues, analysisTime, fileIndex, totalFiles, config.sourceDirectories)

        return issues
    }
    
    private fun analyzeFileInChunks(file: File): List<Issue> {
        val chunks = FileChunker.chunkFile(file, config.maxLinesPerChunk, config.chunkOverlapLines)
        ScanReporter.reportChunk(chunks.size, config.maxLinesPerChunk, config.chunkOverlapLines)
        val allIssues = mutableListOf<Issue>()
        
        chunks.forEach { chunk ->
            ScanReporter.reportChunkAnalysis(chunk.chunkIndex + 1, chunk.totalChunks, chunk.startLine, chunk.endLine)
            val userPrompt = PromptGenerator.createChunkPrompt(file, chunk)
            
            val jsonResponse = modelClient.analyze(config.systemPrompt, userPrompt)
            val chunkIssues = IssueParser.parseIssuesFromJson(jsonResponse)
            
            allIssues.addAll(chunkIssues)
        }
        
        return IssueDeduplicator.deduplicate(allIssues)
    }
    
    private fun analyzeFullFile(file: File): List<Issue> {
        if (file.length() > config.maxFileSizeBytes) {
            throw AnalysisException("File ${file.path} is too large (${file.length()} bytes). Maximum size is ${config.maxFileSizeBytes} bytes.")
        }
        
        val userPrompt = PromptGenerator.createFilePrompt(file)

        val jsonResponse = modelClient.analyze(config.systemPrompt, userPrompt)
        return IssueParser.parseIssuesFromJson(jsonResponse)
    }
}