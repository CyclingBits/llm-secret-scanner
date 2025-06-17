package net.cyclingbits.llmsecretscanner.core.config

import java.io.File

data class ScannerConfiguration(
    val sourceDirectory: File,
    val includes: String = "**/*.java,**/*.kt,**/*.xml,**/*.properties,**/*.yml,**/*.yaml,**/*.json,**/*.md,**/*.sql,**/*.gradle,**/*.kts,**/*.env,**/*.sh,**/*.bat,**/*.html,**/*.css,**/*.js,**/*.ts,**/*.dockerfile",
    val excludes: String = "**/target/**",
    val modelName: String = "ai/phi4:latest",
    val timeout: Int = 60_000,
    val maxTokens: Int = 10_000,
    val temperature: Double = 0.0,
    val dockerImage: String = "alpine/socat:1.7.4.3-r0",
    val maxFileSizeBytes: Int = 100 * 1024, // 100KB
    val connectionTimeoutMs: Int = 1_000, // 1 second
    val systemPrompt: String? = null
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
        require(timeout <= 300_000) {
            "Timeout too large (max 5 minutes): $timeout"
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
        require(connectionTimeoutMs > 0) {
            "Connection timeout must be positive: $connectionTimeoutMs"
        }
        require(connectionTimeoutMs <= 30_000) {
            "Connection timeout too large (max 30s): $connectionTimeoutMs"
        }
    }

    companion object {
        private fun isValidModelName(modelName: String): Boolean {
            // Format: ai/model-name:tag or ai/model-name:version-quantization
            val pattern = Regex("^ai/[a-zA-Z0-9._-]+:[a-zA-Z0-9._-]+$")
            return pattern.matches(modelName)
        }
    }
}