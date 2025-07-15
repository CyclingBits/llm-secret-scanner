package net.cyclingbits.llmsecretscanner.evaluator.model

import com.fasterxml.jackson.annotation.JsonProperty

data class DockerModel(
    @param:JsonProperty("model_name")
    val modelName: String,
    val parameters: String,
    val quantization: String,
    @param:JsonProperty("context_window")
    val contextWindow: String,
    val vram: String,
    val size: String
)