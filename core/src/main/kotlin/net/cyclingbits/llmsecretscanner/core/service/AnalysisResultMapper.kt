package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.exception.JsonParserException
import net.cyclingbits.llmsecretscanner.core.model.Issue
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.slf4j.LoggerFactory

object AnalysisResultMapper {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val objectMapper = ObjectMapper().apply {
        registerModule(KotlinModule.Builder().build())
        configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun parseAnalysisResult(jsonResponse: String): List<Issue> {
        try {
            val cleanedJson = extractJsonFromResponse(jsonResponse)
            logger.debug("Attempting to parse JSON: {}", cleanedJson)
            
            val issues: List<Issue> = objectMapper.readValue(
                cleanedJson,
                object : TypeReference<List<Issue>>() {}
            )
            return issues
        } catch (e: Exception) {
            logger.error("Failed to parse analysis result JSON")
            logger.error("Original LLM response: {}", jsonResponse)
            logger.error("Extracted JSON: {}", extractJsonFromResponse(jsonResponse))
            logger.error("Error details: {}", e.message, e)
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