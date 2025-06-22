package net.cyclingbits.llmsecretscanner.core.llm

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import net.cyclingbits.llmsecretscanner.core.exception.JsonParserException
import net.cyclingbits.llmsecretscanner.core.model.Issue
import net.cyclingbits.llmsecretscanner.core.util.ScanReporter

object IssueParser {

    private val objectMapper = ObjectMapper().apply {
        registerModule(KotlinModule.Builder().build())
        configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun parseIssuesFromJson(jsonResponse: String): List<Issue> {
        try {
            val cleanedJson = extractJsonFromResponse(jsonResponse)
            val issues: List<Issue> = objectMapper.readValue(
                cleanedJson,
                object : TypeReference<List<Issue>>() {}
            )
            return validateSecretValues(issues)
        } catch (e: Exception) {
            ScanReporter.reportError("Error parsing JSON response", e)
            throw JsonParserException("Error parsing JSON response", e)
        }
    }

    private fun extractJsonFromResponse(response: String): String {
        val jsonStart = response.indexOf('[')
        val jsonEnd = response.lastIndexOf(']')

        return if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            val extracted = response.substring(jsonStart, jsonEnd + 1)
            fixMalformedJson(extracted)
        } else {
            response
        }
    }

    private fun fixMalformedJson(json: String): String {
        val pattern = Regex(""""([^"]*?)"\s*\+\s*\n\s*"([^"]*?)"""")
        var result = json
        while (pattern.containsMatchIn(result)) {
            result = result.replace(pattern) { "\"${it.groupValues[1]}${it.groupValues[2]}\"" }
        }
        if (result != json) {
            ScanReporter.reportWarning("Fixed malformed JSON")
        }
        return result
    }

    private fun validateSecretValues(issues: List<Issue>): List<Issue> {
        return issues.filter { issue ->
            val secretValue = issue.secretValue
            if (secretValue.isNullOrBlank() || secretValue.length < 3) {
                ScanReporter.reportWarning("Discarded issue with empty or too short secret value at line ${issue.lineNumber}")
                false
            } else {
                true
            }
        }
    }
}