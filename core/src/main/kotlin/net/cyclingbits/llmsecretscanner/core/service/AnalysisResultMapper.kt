package net.cyclingbits.llmsecretscanner.core.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import net.cyclingbits.llmsecretscanner.core.exception.JsonParserException
import net.cyclingbits.llmsecretscanner.core.model.Issue

object AnalysisResultMapper {

    private val objectMapper = ObjectMapper().apply {
        registerModule(KotlinModule.Builder().build())
        configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun parseAnalysisResult(jsonResponse: String): List<Issue> {
        try {
            val cleanedJson = extractJsonFromResponse(jsonResponse)
            val issues: List<Issue> = objectMapper.readValue(
                cleanedJson,
                object : TypeReference<List<Issue>>() {}
            )
            return issues
        } catch (e: Exception) {
            throw JsonParserException("Error parsing JSON response", e)
        }
    }

    private fun extractJsonFromResponse(response: String): String {
        val jsonStart = response.indexOf('[')
        val jsonEnd = response.lastIndexOf(']')

        return if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            val extracted = response.substring(jsonStart, jsonEnd + 1)
            extracted
        } else {
            response
        }
    }
}