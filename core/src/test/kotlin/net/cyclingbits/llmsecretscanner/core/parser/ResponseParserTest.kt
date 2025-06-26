package net.cyclingbits.llmsecretscanner.core.parser

import net.cyclingbits.llmsecretscanner.core.logger.ScanLogger
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ResponseParserTest {

    private val logger = mockk<ScanLogger>(relaxed = true)
    private val parser = ResponseParser(logger)

    @Test
    fun `should parse valid LLM response`() {
        val llmResponse = """
        {
            "choices": [
                {
                    "message": {
                        "content": "[{\"lineNumber\": 10, \"issueType\": \"API_KEY\", \"secretValue\": \"sk-123\"}]"
                    }
                }
            ]
        }
        """.trimIndent()
        
        val issues = parser.parseResponse(llmResponse)
        
        assertEquals(1, issues.size)
        assertEquals(10, issues[0].lineNumber)
        assertEquals("API_KEY", issues[0].issueType)
        assertEquals("sk-123", issues[0].secretValue)
    }

    @Test
    fun `should parse response with no issues`() {
        val llmResponse = """
        {
            "choices": [
                {
                    "message": {
                        "content": "[]"
                    }
                }
            ]
        }
        """.trimIndent()
        
        val issues = parser.parseResponse(llmResponse)
        
        assertTrue(issues.isEmpty())
    }

    @Test
    fun `should handle malformed JSON`() {
        val malformedJson = "not a valid json"
        
        val issues = parser.parseResponse(malformedJson)
        
        assertTrue(issues.isEmpty())
    }

    @Test
    fun `should filter out issues with short secret values`() {
        val llmResponse = """
        {
            "choices": [
                {
                    "message": {
                        "content": "[{\"lineNumber\": 10, \"issueType\": \"API_KEY\", \"secretValue\": \"ab\"}]"
                    }
                }
            ]
        }
        """.trimIndent()
        
        val issues = parser.parseResponse(llmResponse)
        
        assertTrue(issues.isEmpty())
    }

    @Test
    fun `should extract JSON from array in content`() {
        val llmResponse = """
        {
            "choices": [
                {
                    "message": {
                        "content": "Here's the analysis: [{\"lineNumber\": 5, \"issueType\": \"PASSWORD\", \"secretValue\": \"admin123\"}] Done."
                    }
                }
            ]
        }
        """.trimIndent()
        
        val issues = parser.parseResponse(llmResponse)
        
        assertEquals(1, issues.size)
        assertEquals(5, issues[0].lineNumber)
    }
}