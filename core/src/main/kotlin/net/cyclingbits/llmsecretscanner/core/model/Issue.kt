package net.cyclingbits.llmsecretscanner.core.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Issue(
    @JsonProperty("lineNumber")
    val lineNumber: Int = 0,

    @JsonProperty("issueType")
    val issueType: String = "Unknown",

    @JsonProperty("secretValue")
    val secretValue: String? = null
)