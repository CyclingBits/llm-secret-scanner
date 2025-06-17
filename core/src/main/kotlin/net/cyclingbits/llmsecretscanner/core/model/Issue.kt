package net.cyclingbits.llmsecretscanner.core.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Issue(
    @JsonProperty("filePath")
    val filePath: String = "",

    @JsonProperty("lineNumber")
    val lineNumber: Int = 0,

    @JsonProperty("issueType")
    val issueType: String = "Unknown",

    @JsonProperty("description")
    val description: String = "No description provided"
)