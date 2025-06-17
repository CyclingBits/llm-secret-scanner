package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import org.codehaus.plexus.util.DirectoryScanner
import org.slf4j.LoggerFactory
import java.io.File

class FileScanner(private val config: ScannerConfiguration) {
    
    private val logger = LoggerFactory.getLogger(FileScanner::class.java)
    
    fun findFiles(): List<File> {
        logger.debug("Setting up directory scanner for: {}", config.sourceDirectory.absolutePath)
        
        val scanner = DirectoryScanner()
        scanner.setBasedir(config.sourceDirectory)
        
        val includePatterns = config.includes.split(",").map { it.trim() }.toTypedArray()
        val excludePatterns = config.excludes.split(",").map { it.trim() }.toTypedArray()
        
        logger.debug("Include patterns: {}", includePatterns.joinToString(", "))
        logger.debug("Exclude patterns: {}", excludePatterns.joinToString(", "))
        
        scanner.setIncludes(includePatterns)
        scanner.setExcludes(excludePatterns)
        scanner.scan()

        val files = scanner.includedFiles.map {
            File(config.sourceDirectory, it)
        }
        
        logger.debug("Found {} files matching patterns", files.size)
        if (logger.isDebugEnabled && files.isNotEmpty()) {
            logger.debug("Files to scan:")
            files.forEach { file ->
                logger.debug("  - {}", file.path)
            }
        }
        
        return files
    }
}