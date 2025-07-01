package net.cyclingbits.llmsecretscanner.core

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.file.FileFinder
import net.cyclingbits.llmsecretscanner.core.llm.ContainerManager
import net.cyclingbits.llmsecretscanner.core.llm.ModelClient
import net.cyclingbits.llmsecretscanner.core.logger.ScanLogger
import net.cyclingbits.llmsecretscanner.core.model.ScanResult
import net.cyclingbits.llmsecretscanner.core.service.CodeAnalyzer
import java.io.File
import java.time.Duration
import java.time.Instant

class Scanner(
    private val configuration: ScannerConfiguration,
    private val logger: ScanLogger,
    private val containerManager: ContainerManager,
    private val modelClient: ModelClient,
    private val fileFinder: FileFinder,
    private val codeAnalyzer: CodeAnalyzer,
) : AutoCloseable {

    init {
        logger.reportScanStart()
        logger.reportScanConfiguration(configuration)
        containerManager.getContainer()
    }

    fun scan(): ScanResult {
        val filesToScan = fileFinder.findFiles(configuration.sourceDirectories)
        return executeScan(filesToScan)
    }

    fun executeScan(filesToScan: List<File>): ScanResult {
        val startTime = Instant.now()

        val fileScanResults = filesToScan.mapIndexedNotNull { index, file ->
            codeAnalyzer.analyzeFile(file, index + 1, filesToScan.size)
        }

        val totalTime = Duration.between(startTime, Instant.now())

        logger.reportScanComplete(fileScanResults, filesToScan.size, totalTime)

        return ScanResult(fileScanResults = fileScanResults)
    }

    override fun close() {
        modelClient.close()
        containerManager.close()
    }
}