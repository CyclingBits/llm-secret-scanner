package net.cyclingbits.llmsecretscanner.core.config

import net.cyclingbits.llmsecretscanner.events.Utils

object ScannerDefaults {
    const val MAX_FILE_SIZE_BYTES = 100 * 1024
    const val MAX_LINES_PER_CHUNK = 40
    const val CHUNK_OVERLAP_LINES = 5
    const val MAX_TOKENS = 16_000
    const val TEMPERATURE = 0.0
    const val CONNECTION_TIMEOUT = 1
    const val CHUNK_ANALYSIS_TIMEOUT = 600
    const val MAX_CHUNK_SIZE = 1000
    const val MAX_CHUNK_ANALYSIS_TIMEOUT = 3600
    const val MAX_CONNECTION_TIMEOUT = 30
    const val MAX_TOKENS_LIMIT = 100_000
    const val MIN_TEMPERATURE = 0.0
    const val MAX_TEMPERATURE = 2.0
    const val MAX_FILE_SIZE_LIMIT = 10 * 1024 * 1024

    const val TOP_P = 0.05
    const val MIN_TOP_P = 0.0
    const val MAX_TOP_P = 1.0
    const val SEED = 42
    const val FREQUENCY_PENALTY = 0.3
    const val MIN_FREQUENCY_PENALTY = -2.0
    const val MAX_FREQUENCY_PENALTY = 2.0

    const val DEFAULT_INCLUDES =
        "**/*.java,**/*.kt,**/*.xml,**/*.properties,**/*.yml,**/*.yaml,**/*.json,**/*.md,**/*.sql,**/*.gradle,**/*.kts,**/*.env,**/*.sh,**/*.bat,**/*.html,**/*.css,**/*.js,**/*.ts,**/*.dockerfile"
    const val DEFAULT_EXCLUDES = "**/target/**"
    const val DEFAULT_MODEL_NAME = "ai/phi4:latest"
    const val DEFAULT_DOCKER_IMAGE = "alpine/socat:1.7.4.3-r0"

    const val ENABLE_CHUNKING = true

    fun loadSystemPrompt(): String {
        return Utils.loadResource("system_prompt.md")
    }


}