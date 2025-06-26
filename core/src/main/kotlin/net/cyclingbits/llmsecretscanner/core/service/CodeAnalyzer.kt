package net.cyclingbits.llmsecretscanner.core.service

import kotlinx.coroutines.runBlocking
import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.file.FileChunker
import net.cyclingbits.llmsecretscanner.core.llm.ModelClient
import net.cyclingbits.llmsecretscanner.core.logger.ScanLogger
import net.cyclingbits.llmsecretscanner.core.model.FileScanResult
import net.cyclingbits.llmsecretscanner.core.model.Issue
import net.cyclingbits.llmsecretscanner.core.parser.ResponseParser
import java.io.File
import java.time.Duration
import java.time.Instant

class CodeAnalyzer(
    private val config: ScannerConfiguration,
    private val logger: ScanLogger,
    private val fileChunker: FileChunker,
    private val modelClient: ModelClient,
    private val responseParser: ResponseParser
) {

    fun analyzeFile(file: File, fileIndex: Int, totalFiles: Int): FileScanResult? = runBlocking {
        if (!validateFileSize(file)) return@runBlocking null

        logger.reportFileAnalysisStart(file, fileIndex, totalFiles, config)
        val startTime = Instant.now()

        val result = if (config.enableChunking) analyzeWithChunking(file) else analyzeSinglePrompt(file, PromptGenerator.createFilePrompt(file))

        val analysisTime = Duration.between(startTime, Instant.now())
        result?.let { logger.reportFileIssues(file, it.issues, analysisTime, fileIndex, totalFiles) }
        result
    }

    private suspend fun analyzeWithChunking(file: File): FileScanResult? {
        val chunks = fileChunker.processFile(file)
        if (chunks.size == 1) return analyzeSinglePrompt(file, PromptGenerator.createFilePrompt(file))

        logger.reportChunk(file, chunks.size, config)

        val allIssues = chunks.mapNotNull { chunk ->
            logger.reportChunkAnalysis(chunk, file.extension)
            analyzeLLM(PromptGenerator.createChunkPrompt(file, chunk))
        }.flatten()

        return FileScanResult(file, IssueDeduplicator.deduplicate(allIssues))
    }

    private suspend fun analyzeSinglePrompt(file: File, prompt: String): FileScanResult? =
        analyzeLLM(prompt)?.let { FileScanResult(file, it) }

    private suspend fun analyzeLLM(prompt: String): List<Issue>? {
        val rawResponse = modelClient.analyze(prompt)
        return rawResponse?.let { responseParser.parseResponse(it) }
    }

    private fun validateFileSize(file: File): Boolean {
        return (file.length() <= config.maxFileSizeBytes).also { isValid ->
            if (!isValid) logger.reportError("File ${file.path} is too large (${file.length()} bytes). Maximum size is ${config.maxFileSizeBytes} bytes.")
        }
    }
}