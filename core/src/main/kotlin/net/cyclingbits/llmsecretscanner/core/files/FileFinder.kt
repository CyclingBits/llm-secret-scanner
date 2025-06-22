package net.cyclingbits.llmsecretscanner.core.files

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.util.ScanReporter
import org.codehaus.plexus.util.DirectoryScanner
import java.io.File

class FileFinder(private val config: ScannerConfiguration) {

    fun findFiles(sourceDirectory: File): List<File> {
        val scanner = DirectoryScanner()
        scanner.setBasedir(sourceDirectory)
        scanner.setIncludes(config.includes.split(",").map { it.trim() }.toTypedArray())
        scanner.setExcludes(config.excludes.split(",").map { it.trim() }.toTypedArray())
        scanner.scan()

        return scanner.includedFiles.map {
            File(sourceDirectory, it)
        }
    }
    
    fun findFiles(sourceDirectories: List<File>): List<File> {
        val allFiles = sourceDirectories.flatMap { findFiles(it) }
        
        if (allFiles.isEmpty()) {
            ScanReporter.reportNoFilesFound()
            return emptyList()
        } else {
            ScanReporter.reportFilesFound(allFiles.size)
            return allFiles
        }
    }
}