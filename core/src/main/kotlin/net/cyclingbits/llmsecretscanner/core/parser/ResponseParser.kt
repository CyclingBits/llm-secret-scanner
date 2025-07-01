package net.cyclingbits.llmsecretscanner.core.parser

import com.fasterxml.jackson.core.type.TypeReference
import net.cyclingbits.llmsecretscanner.core.logger.ScanLogger
import net.cyclingbits.llmsecretscanner.core.model.Issue

class ResponseParser(
    private val logger: ScanLogger
) {

    fun parseResponse(rawResponse: String): List<Issue> {
        return parseRawResponse(rawResponse)
            ?.let { parseJsonResponse(it) }
            ?: emptyList()
    }

    private fun parseRawResponse(rawResponse: String): String? {
        return try {
            val responseMap = ObjectMapperHolder.objectMapper.readValue(rawResponse, Map::class.java)
            val content = extractContent(responseMap)
            content?.let { logger.reportJsonParse(it) }
            content
        } catch (e: Exception) {
            logger.reportJsonParseError(rawResponse, e).let { null }
        }
    }

    private fun extractContent(responseMap: Map<*, *>): String? {
        val choices = responseMap["choices"] as? List<*>
        val firstChoice = choices?.firstOrNull() as? Map<*, *>
        val message = firstChoice?.get("message") as? Map<*, *>
        return message?.get("content") as? String
    }

    private fun parseJsonResponse(jsonResponse: String): List<Issue>? {
        return try {
            val cleanedJson = extractJsonFromResponse(jsonResponse)
            val issues: List<Issue> = ObjectMapperHolder.objectMapper.readValue(
                cleanedJson,
                object : TypeReference<List<Issue>>() {}
            )
            validateSecretValues(issues)
        } catch (e: Exception) {
            logger.reportJsonParseError(jsonResponse, e).let { null }
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
            logger.reportWarning("Fixed malformed JSON", json)
        }
        return result
    }

    private fun validateSecretValues(issues: List<Issue>): List<Issue> {
        return issues.filter { issue ->
            val secretValue = issue.secretValue
            if (secretValue.isNullOrBlank() || secretValue.length < 3) {
                logger.reportWarning("Discarded issue with empty or too short secret value at line ${issue.lineNumber}", issues)
                false
            } else {
                true
            }
        }
    }
}