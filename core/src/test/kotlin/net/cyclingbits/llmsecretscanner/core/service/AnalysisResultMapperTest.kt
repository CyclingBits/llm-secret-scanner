package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.exception.JsonParserException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows

class AnalysisResultMapperTest {

    @Test
    fun parseAnalysisResult_validJsonArray_returnsIssues() {
        val json = """[
            {
                "filePath": "/path/to/file.java",
                "lineNumber": 42,
                "issueType": "Password",
                "description": "Hardcoded password found"
            }
        ]"""
        
        val result = AnalysisResultMapper.parseAnalysisResult(json)
        
        assertEquals(1, result.size)
        assertEquals("/path/to/file.java", result[0].filePath)
        assertEquals(42, result[0].lineNumber)
        assertEquals("Password", result[0].issueType)
        assertEquals("Hardcoded password found", result[0].description)
    }

    @Test
    fun parseAnalysisResult_singleObject_returnsWrappedIssue() {
        val json = """[{
            "filePath": "/path/to/file.kt",
            "lineNumber": 15,
            "issueType": "API Key",
            "description": "API key detected"
        }]"""
        
        val result = AnalysisResultMapper.parseAnalysisResult(json)
        
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
                "lineNumber": 1,
                "issueType": "Token",
                "description": "Token found"
            }
        ]
        ```"""
        
        val result = AnalysisResultMapper.parseAnalysisResult(response)
        
        assertEquals(1, result.size)
        assertEquals("/test.java", result[0].filePath)
    }

    @Test
    fun parseAnalysisResult_standardFields_mapsCorrectly() {
        val json = """[{
            "filePath": "/alt/path.java",
            "lineNumber": 99,
            "issueType": "Secret", 
            "description": "Secret value detected"
        }]"""
        
        val result = AnalysisResultMapper.parseAnalysisResult(json)
        
        assertEquals(1, result.size)
        assertEquals("/alt/path.java", result[0].filePath)
        assertEquals(99, result[0].lineNumber)
        assertEquals("Secret", result[0].issueType)
        assertEquals("Secret value detected", result[0].description)
    }

    @Test
    fun parseAnalysisResult_invalidJson_throwsException() {
        val invalidJson = "invalid json"
        
        assertThrows<JsonParserException> {
            AnalysisResultMapper.parseAnalysisResult(invalidJson)
        }
    }

    @Test
    fun parseAnalysisResult_emptyArray_returnsEmptyList() {
        val json = "[]"
        
        val result = AnalysisResultMapper.parseAnalysisResult(json)
        
        assertEquals(0, result.size)
    }

    @Test
    fun parseAnalysisResult_multipleIssues_returnsAll() {
        val json = """[
            {
                "filePath": "/file1.java",
                "lineNumber": 10,
                "issueType": "Password",
                "description": "First issue"
            },
            {
                "filePath": "/file2.java", 
                "lineNumber": 20,
                "issueType": "API Key",
                "description": "Second issue"
            }
        ]"""
        
        val result = AnalysisResultMapper.parseAnalysisResult(json)
        
        assertEquals(2, result.size)
        assertEquals("/file1.java", result[0].filePath)
        assertEquals("/file2.java", result[1].filePath)
    }
}