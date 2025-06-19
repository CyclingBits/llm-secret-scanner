package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import org.codehaus.plexus.util.DirectoryScanner
import java.io.File

class FileScanner(private val config: ScannerConfiguration) {

    fun findFiles(): List<File> {

        val scanner = DirectoryScanner()
        scanner.setBasedir(config.sourceDirectory)
        scanner.setIncludes(config.includes.split(",").map { it.trim() }.toTypedArray())
        scanner.setExcludes(config.excludes.split(",").map { it.trim() }.toTypedArray())
        scanner.scan()

        val files = scanner.includedFiles.map {
            File(config.sourceDirectory, it)
        }

        if (files.isEmpty()) {
            ScanReporter.reportNoFilesFound()
            return emptyList()
        } else {
            ScanReporter.reportFilesFound(files.size)
            return files
        }
    }
}