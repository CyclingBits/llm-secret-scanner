package net.cyclingbits.llmsecretscanner.core.model

import java.io.File

data class FileScanResult(
    val file: File,
    val issues: List<Issue>,
)
