package net.cyclingbits.llmsecretscanner.core.config

import net.cyclingbits.llmsecretscanner.events.toFormattedJsonString
import java.io.File

data class ScannerConfiguration(
    val modelName: String = ScannerDefaults.DEFAULT_MODEL_NAME,
    val dockerImage: String = ScannerDefaults.DEFAULT_DOCKER_IMAGE,

    val sourceDirectories: List<File>,
    val includes: String = ScannerDefaults.DEFAULT_INCLUDES,
    val excludes: String = ScannerDefaults.DEFAULT_EXCLUDES,
    val maxFileSizeBytes: Int = ScannerDefaults.MAX_FILE_SIZE_BYTES,

    val systemPrompt: String = ScannerDefaults.loadSystemPrompt(),

    val maxTokens: Int = ScannerDefaults.MAX_TOKENS,
    val temperature: Double = ScannerDefaults.TEMPERATURE,
    val topP: Double = ScannerDefaults.TOP_P,
    val seed: Int = ScannerDefaults.SEED,
    val frequencyPenalty: Double = ScannerDefaults.FREQUENCY_PENALTY,

    val timeout: Int = ScannerDefaults.CONNECTION_TIMEOUT,
    val chunkAnalysisTimeout: Int = ScannerDefaults.CHUNK_ANALYSIS_TIMEOUT,

    val enableChunking: Boolean = ScannerDefaults.ENABLE_CHUNKING,
    val maxLinesPerChunk: Int = ScannerDefaults.MAX_LINES_PER_CHUNK,
    val chunkOverlapLines: Int = ScannerDefaults.CHUNK_OVERLAP_LINES
) {
    init {
        require(sourceDirectories.isNotEmpty()) {
            "Source directories list cannot be empty"
        }
        sourceDirectories.forEach { dir ->
            require(dir.exists()) {
                "Source directory must exist: ${dir.absolutePath}"
            }
            require(dir.isDirectory) {
                "Source path must be a directory: ${dir.absolutePath}"
            }
            require(dir.canRead()) {
                "Source directory must be readable: ${dir.absolutePath}"
            }
        }

        require(includes.isNotBlank()) {
            "Include patterns cannot be empty"
        }
        require(excludes.isNotBlank()) {
            "Exclude patterns cannot be empty"
        }
        require(maxFileSizeBytes > 0) {
            "Max file size must be positive: $maxFileSizeBytes"
        }
        require(maxFileSizeBytes <= ScannerDefaults.MAX_FILE_SIZE_LIMIT) {
            "Max file size too large (max ${ScannerDefaults.MAX_FILE_SIZE_LIMIT / (1024 * 1024)}MB): $maxFileSizeBytes"
        }

        require(modelName.isNotBlank()) {
            "Model name cannot be empty"
        }
        require(isValidModelName(modelName)) {
            "Invalid model name format: $modelName. Expected format: ai/model-name:tag or ai/model-name:version-quantization"
        }
        require(maxTokens > 0) {
            "Max tokens must be positive: $maxTokens"
        }
        require(maxTokens <= ScannerDefaults.MAX_TOKENS_LIMIT) {
            "Max tokens too large (max ${ScannerDefaults.MAX_TOKENS_LIMIT}): $maxTokens"
        }
        require(temperature >= ScannerDefaults.MIN_TEMPERATURE && temperature <= ScannerDefaults.MAX_TEMPERATURE) {
            "Temperature must be between ${ScannerDefaults.MIN_TEMPERATURE} and ${ScannerDefaults.MAX_TEMPERATURE}: $temperature"
        }
        require(topP >= ScannerDefaults.MIN_TOP_P && topP <= ScannerDefaults.MAX_TOP_P) {
            "Top P must be between ${ScannerDefaults.MIN_TOP_P} and ${ScannerDefaults.MAX_TOP_P}: $topP"
        }
        require(seed >= 0) {
            "Seed must be non-negative: $seed"
        }
        require(frequencyPenalty >= ScannerDefaults.MIN_FREQUENCY_PENALTY && frequencyPenalty <= ScannerDefaults.MAX_FREQUENCY_PENALTY) {
            "Frequency penalty must be between ${ScannerDefaults.MIN_FREQUENCY_PENALTY} and ${ScannerDefaults.MAX_FREQUENCY_PENALTY}: $frequencyPenalty"
        }
        require(dockerImage.isNotBlank()) {
            "Docker image cannot be empty"
        }

        require(timeout > 0) {
            "Timeout must be positive: $timeout"
        }
        require(timeout <= ScannerDefaults.MAX_CONNECTION_TIMEOUT) {
            "Connection timeout too large (max ${ScannerDefaults.MAX_CONNECTION_TIMEOUT}s): $timeout"
        }
        require(chunkAnalysisTimeout > 0) {
            "Chunk analysis timeout must be positive: $chunkAnalysisTimeout"
        }
        require(chunkAnalysisTimeout <= ScannerDefaults.MAX_CHUNK_ANALYSIS_TIMEOUT) {
            "Chunk analysis timeout too large (max 60 minutes): $chunkAnalysisTimeout"
        }

        require(maxLinesPerChunk > 0) {
            "Max lines per chunk must be positive: $maxLinesPerChunk"
        }
        require(maxLinesPerChunk <= ScannerDefaults.MAX_CHUNK_SIZE) {
            "Max lines per chunk too large (max ${ScannerDefaults.MAX_CHUNK_SIZE}): $maxLinesPerChunk"
        }
        require(chunkOverlapLines >= 0) {
            "Chunk overlap lines must be non-negative: $chunkOverlapLines"
        }
        require(chunkOverlapLines < maxLinesPerChunk) {
            "Chunk overlap lines must be less than max lines per chunk: $chunkOverlapLines >= $maxLinesPerChunk"
        }
    }

    private fun isValidModelName(modelName: String): Boolean {
        val pattern = Regex("^ai/[a-zA-Z0-9._-]+:[a-zA-Z0-9._-]+$")
        return pattern.matches(modelName)
    }

    override fun toString(): String {
        return this.toFormattedJsonString()
    }
}