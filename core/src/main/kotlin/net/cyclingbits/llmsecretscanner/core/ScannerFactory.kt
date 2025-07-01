package net.cyclingbits.llmsecretscanner.core

import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.file.FileChunker
import net.cyclingbits.llmsecretscanner.core.file.FileFinder
import net.cyclingbits.llmsecretscanner.core.llm.ContainerManager
import net.cyclingbits.llmsecretscanner.core.llm.ModelClient
import net.cyclingbits.llmsecretscanner.core.logger.ScanLogger
import net.cyclingbits.llmsecretscanner.core.parser.ResponseParser
import net.cyclingbits.llmsecretscanner.core.service.CodeAnalyzer

object ScannerFactory {

    fun create(configuration: ScannerConfiguration): Scanner {
        val logger = ScanLogger()
        val containerManager = ContainerManager(configuration, logger)
        val modelClient = ModelClient(configuration, logger, containerManager)
        val fileChunker = FileChunker(configuration, logger)
        val fileFinder = FileFinder(configuration, logger)
        val responseParser = ResponseParser(logger)
        val codeAnalyzer = CodeAnalyzer(configuration, logger, fileChunker, modelClient, responseParser)

        return Scanner(configuration, logger, containerManager, modelClient, fileFinder, codeAnalyzer)
    }
}