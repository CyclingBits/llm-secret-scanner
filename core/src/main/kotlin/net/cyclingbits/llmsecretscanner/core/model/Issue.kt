package net.cyclingbits.llmsecretscanner.core.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Issue(
    @JsonProperty("lineNumber")
    val lineNumber: Int = 0,

    @JsonProperty("secretValue")
    val secretValue: String? = null
)