package net.cyclingbits.llmsecretscanner.core.llm

import net.cyclingbits.llmsecretscanner.core.exception.JsonParserException
import net.cyclingbits.llmsecretscanner.core.llm.IssueParser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows

class IssueParserTest {

    @Test
    fun parseAnalysisResult_validJsonArray_returnsIssues() {
        val json = """[
            {
                "filePath": "/path/to/file.java",
                "issueNumber": 1,
                "lineNumber": 42,
                "issueType": "Password",
                "secretValue": "hardcoded123"
            }
        ]"""
        
        val result = IssueParser.parseIssuesFromJson(json)
        
        assertEquals(1, result.size)
        assertEquals("/path/to/file.java", result[0].filePath)
        assertEquals(42, result[0].lineNumber)
        assertEquals("Password", result[0].issueType)
        assertEquals("hardcoded123", result[0].secretValue)
    }

    @Test
    fun parseAnalysisResult_singleObject_returnsWrappedIssue() {
        val json = """[{
            "filePath": "/path/to/file.kt",
            "issueNumber": 1,
            "lineNumber": 15,
            "issueType": "API Key",
            "secretValue": "sk_test_123"
        }]"""
        
        val result = IssueParser.parseIssuesFromJson(json)
        
        assertEquals(1, result.size)
        assertEquals("/path/to/file.kt", result[0].filePath)
        assertEquals(15, result[0].lineNumber)
    }

    @Test
    fun parseAnalysisResult_markdownCodeBlock_extractsJson() {
        val response = """```json
        [
            {
                "filePath": "/test.java",
                "issueNumber": 1,
                "lineNumber": 1,
                "issueType": "Token",
                "secretValue": "abc123token"
            }
        ]
        ```"""
        
        val result = IssueParser.parseIssuesFromJson(response)
        
        assertEquals(1, result.size)
        assertEquals("/test.java", result[0].filePath)
    }

    @Test
    fun parseAnalysisResult_standardFields_mapsCorrectly() {
        val json = """[{
            "filePath": "/alt/path.java",
            "issueNumber": 1,
            "lineNumber": 99,
            "issueType": "Token", 
            "secretValue": "secret123value"
        }]"""
        
        val result = IssueParser.parseIssuesFromJson(json)
        
        assertEquals(1, result.size)
        assertEquals("/alt/path.java", result[0].filePath)
        assertEquals(99, result[0].lineNumber)
        assertEquals("Token", result[0].issueType)
        assertEquals("secret123value", result[0].secretValue)
    }

    @Test
    fun parseAnalysisResult_invalidJson_throwsException() {
        val invalidJson = "invalid json"
        
        assertThrows<JsonParserException> {
            IssueParser.parseIssuesFromJson(invalidJson)
        }
    }

    @Test
    fun parseAnalysisResult_emptyArray_returnsEmptyList() {
        val json = "[]"
        
        val result = IssueParser.parseIssuesFromJson(json)
        
        assertEquals(0, result.size)
    }

    @Test
    fun parseAnalysisResult_multipleIssues_returnsAll() {
        val json = """[
            {
                "filePath": "/file1.java",
                "issueNumber": 1,
                "lineNumber": 10,
                "issueType": "Password",
                "secretValue": "pass123"
            },
            {
                "filePath": "/file2.java", 
                "issueNumber": 2,
                "lineNumber": 20,
                "issueType": "API Key",
                "secretValue": "api_key_456"
            }
        ]"""
        
        val result = IssueParser.parseIssuesFromJson(json)
        
        assertEquals(2, result.size)
        assertEquals("/file1.java", result[0].filePath)
        assertEquals("/file2.java", result[1].filePath)
    }
}