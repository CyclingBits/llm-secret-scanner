package net.cyclingbits.llmsecretscanner.evaluator.service

import net.cyclingbits.llmsecretscanner.core.ScannerFactory
import net.cyclingbits.llmsecretscanner.core.config.ScannerConfiguration
import net.cyclingbits.llmsecretscanner.core.file.FileFinder
import net.cyclingbits.llmsecretscanner.core.logger.ScanLogger
import net.cyclingbits.llmsecretscanner.core.model.FileScanResult
import net.cyclingbits.llmsecretscanner.core.model.Issue
import net.cyclingbits.llmsecretscanner.core.model.ScanResult
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration.FALSE_POSITIVE_CASES_DIR
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration.POSITIVE_CASES_DIR
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration.POSITIVE_EXPECTED_ISSUES_DIR
import net.cyclingbits.llmsecretscanner.evaluator.logger.EvaluatorLogger
import net.cyclingbits.llmsecretscanner.evaluator.model.EvaluationError
import net.cyclingbits.llmsecretscanner.evaluator.model.EvaluationResult
import net.cyclingbits.llmsecretscanner.evaluator.model.ExpectedIssue
import net.cyclingbits.llmsecretscanner.events.EventStore
import net.cyclingbits.llmsecretscanner.events.JsonSupport
import net.cyclingbits.llmsecretscanner.events.LogLevel
import java.io.File
import java.time.Duration
import java.time.Instant

class EvaluationService(
    private val config: ScannerConfiguration,
    private val logger: EvaluatorLogger,
    private val scanLogger: ScanLogger = ScanLogger()
) {

    private val scanner = ScannerFactory.create(config)
    private val detectionRateCalculator = DetectionRateCalculator(logger)

    fun evaluateModel(): EvaluationResult {
        val startTime = Instant.now()

        val positiveResults = evaluateTestCases(POSITIVE_CASES_DIR)
        val expectedPositiveIssues = loadExpectedIssues()
        val negativeResults = evaluateTestCases(FALSE_POSITIVE_CASES_DIR)

        val detectionMetrics = detectionRateCalculator.calculate(positiveResults.fileScanResults, expectedPositiveIssues, negativeResults.fileScanResults)

        val evaluationResult = EvaluationResult(
            modelName = config.modelName,
            detectionRate = detectionMetrics.detectionRate,
            falsePositiveRate = detectionMetrics.falsePositiveRate,
            scanTime = Duration.between(startTime, Instant.now()).toMillis(),
            config = config,
            errors = collectErrors()
        )

        ResultsSaver(logger).saveResultsToMarkdown(listOf(evaluationResult))
        scanner.close()
        return evaluationResult
    }

    private fun evaluateTestCases(sourceDir: File): ScanResult {
        val testConfig = config.copy(sourceDirectories = listOf(sourceDir))
        val files = FileFinder(testConfig, scanLogger).findFiles(listOf(sourceDir))

        logger.reportAnalysisStartForDirectory(sourceDir, files.size)
        return scanner.executeScan(files)
    }

    private fun loadExpectedIssues(): List<FileScanResult> {
        val configCopy = config.copy(sourceDirectories = listOf(POSITIVE_CASES_DIR))
        val actualScannedFiles = FileFinder(configCopy, scanLogger).findFiles(listOf(POSITIVE_CASES_DIR))

        val result = POSITIVE_EXPECTED_ISSUES_DIR.listFiles { it.extension == "json" }
            ?.flatMap { file ->
                try {
                    JsonSupport.objectMapper.readValue(file, Array<ExpectedIssue>::class.java).toList()
                } catch (e: Exception) {
                    logger.reportWarning("Failed to load ${file.name}: ${e.message}")
                    emptyList()
                }
            }
            ?.filter { expectedIssue ->
                actualScannedFiles.any { scannedFile -> scannedFile.name == expectedIssue.filePath }
            }
            ?.groupBy { File(it.filePath) }
            ?.map { (file, expectedIssues) ->
                FileScanResult(
                    file,
                    expectedIssues.map { expected ->
                        Issue(
                            lineNumber = expected.lineNumber,
                            secretValue = expected.secretValue
                        )
                    }
                )
            } ?: emptyList()

        logger.reportExpectedIssuesLoaded(result.sumOf { it.issues.size }, result.size)
        return result
    }

    internal fun collectErrors(): List<EvaluationError> {
        return EventStore.getAll()
            .filter { it.level == LogLevel.ERROR || it.level == LogLevel.WARN }
            .map { event ->
                EvaluationError(
                    timestamp = event.timestamp,
                    type = event.type,
                    level = event.level,
                    message = event.rawMessages.joinToString(" "),
                    errorType = event.error?.javaClass?.simpleName
                )
            }
    }
}