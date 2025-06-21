package net.cyclingbits.llmsecretscanner.core.config

import net.cyclingbits.llmsecretscanner.core.service.ScanReporter
import java.io.File

data class ScannerConfiguration(
    val sourceDirectory: File,
    val includes: String = "**/*.java,**/*.kt,**/*.xml,**/*.properties,**/*.yml,**/*.yaml,**/*.json,**/*.md,**/*.sql,**/*.gradle,**/*.kts,**/*.env,**/*.sh,**/*.bat,**/*.html,**/*.css,**/*.js,**/*.ts,**/*.dockerfile",
    val excludes: String = "**/target/**",
    val modelName: String = "ai/phi4:latest",
    val timeout: Int = 1, // connection timeout in seconds
    val chunkAnalysisTimeout: Int = 60,
    val maxTokens: Int = 16_000,
    val temperature: Double = 0.0,
    val dockerImage: String = "alpine/socat:1.7.4.3-r0",
    val maxFileSizeBytes: Int = 100 * 1024, // 100KB
    val systemPrompt: String? = null,
    val enableChunking: Boolean = true,
    val maxLinesPerChunk: Int = 40,
    val chunkOverlapLines: Int = 5
) {
    init {
        require(sourceDirectory.exists()) {
            "Source directory must exist: ${sourceDirectory.absolutePath}"
        }
        require(sourceDirectory.isDirectory) {
            "Source path must be a directory: ${sourceDirectory.absolutePath}"
        }
        require(sourceDirectory.canRead()) {
            "Source directory must be readable: ${sourceDirectory.absolutePath}"
        }
        require(includes.isNotBlank()) {
            "Include patterns cannot be empty"
        }
        require(excludes.isNotBlank()) {
            "Exclude patterns cannot be empty"
        }
        require(modelName.isNotBlank()) {
            "Model name cannot be empty"
        }
        require(isValidModelName(modelName)) {
            "Invalid model name format: $modelName. Expected format: ai/model-name:tag or ai/model-name:version-quantization"
        }
        require(timeout > 0) {
            "Timeout must be positive: $timeout"
        }
        require(timeout <= 30) {
            "Connection timeout too large (max 30s): $timeout"
        }
        require(chunkAnalysisTimeout > 0) {
            "Chunk analysis timeout must be positive: $chunkAnalysisTimeout"
        }
        require(chunkAnalysisTimeout <= 3600) {
            "Chunk analysis timeout too large (max 60 minutes): $chunkAnalysisTimeout"
        }
        require(maxTokens > 0) {
            "Max tokens must be positive: $maxTokens"
        }
        require(maxTokens <= 100_000) {
            "Max tokens too large (max 100k): $maxTokens"
        }
        require(temperature >= 0.0 && temperature <= 2.0) {
            "Temperature must be between 0.0 and 2.0: $temperature"
        }
        require(dockerImage.isNotBlank()) {
            "Docker image cannot be empty"
        }
        require(maxFileSizeBytes > 0) {
            "Max file size must be positive: $maxFileSizeBytes"
        }
        require(maxFileSizeBytes <= 10 * 1024 * 1024) {
            "Max file size too large (max 10MB): $maxFileSizeBytes"
        }
        require(maxLinesPerChunk > 0) {
            "Max lines per chunk must be positive: $maxLinesPerChunk"
        }
        require(maxLinesPerChunk <= 1000) {
            "Max lines per chunk too large (max 1000): $maxLinesPerChunk"
        }
        require(chunkOverlapLines >= 0) {
            "Chunk overlap lines must be non-negative: $chunkOverlapLines"
        }
        require(chunkOverlapLines < maxLinesPerChunk) {
            "Chunk overlap lines must be less than max lines per chunk: $chunkOverlapLines >= $maxLinesPerChunk"
        }
        ScanReporter.reportScanStart(this)
    }

    companion object {
        private fun isValidModelName(modelName: String): Boolean {
            // Format: ai/model-name:tag or ai/model-name:version-quantization
            val pattern = Regex("^ai/[a-zA-Z0-9._-]+:[a-zA-Z0-9._-]+$")
            return pattern.matches(modelName)
        }
    }
}