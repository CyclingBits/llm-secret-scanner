package net.cyclingbits.llmsecretscanner.core.file

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.logger.ScanLogger
import org.codehaus.plexus.util.DirectoryScanner
import java.io.File

class FileFinder(
    private val config: ScannerConfiguration,
    private val logger: ScanLogger
) {

    fun findFiles(sourceDirectories: List<File>): List<File> {
        val allFiles = sourceDirectories.flatMap { findFiles(it) }

        if (allFiles.isEmpty()) {
            logger.reportNoFilesFound(config, sourceDirectories)
            return emptyList()
        } else {
            logger.reportFilesFound(sourceDirectories, allFiles, config)
            return allFiles
        }
    }

    private fun findFiles(sourceDirectory: File): List<File> {
        val scanner = DirectoryScanner()
        scanner.setBasedir(sourceDirectory)
        scanner.setIncludes(config.includes.split(",").map { it.trim() }.toTypedArray())
        scanner.setExcludes(config.excludes.split(",").map { it.trim() }.toTypedArray())
        scanner.scan()

        return scanner.includedFiles.map {
            File(sourceDirectory, it)
        }
    }
}