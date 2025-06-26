package net.cyclingbits.llmsecretscanner.evaluator.config

import java.io.File

object EvaluatorConfiguration {
    val DOCKER_MODELS_FILE = File("evaluator/src/main/resources/docker_models.json")
    val RESULTS_DIR = File("evaluator/src/main/resources/evaluation_results")
    val POSITIVE_CASES_DIR = File("evaluator/src/main/resources/test_cases/positive")
    val NEGATIVE_CASES_DIR = File("evaluator/src/main/resources/test_cases/false_positive")
    val EXPECTED_ISSUES_DIR = File("evaluator/src/main/resources/test_cases/positive/expected")
}