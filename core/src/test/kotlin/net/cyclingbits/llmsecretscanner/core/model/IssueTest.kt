package net.cyclingbits.llmsecretscanner.core.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class IssueTest {

    @Test
    fun create_withAllFields_succeeds() {
        val issue = Issue(
            filePath = "/path/to/file.java",
            lineNumber = 42,
            issueType = "Password",
            description = "Hardcoded password found"
        )
        
        assertEquals("/path/to/file.java", issue.filePath)
        assertEquals(42, issue.lineNumber)
        assertEquals("Password", issue.issueType)
        assertEquals("Hardcoded password found", issue.description)
    }

    @Test
    fun create_withDefaults_usesDefaultValues() {
        val issue = Issue()
        
        assertEquals("", issue.filePath)
        assertEquals(0, issue.lineNumber)
        assertEquals("Unknown", issue.issueType)
        assertEquals("No description provided", issue.description)
    }

    @Test
    fun create_partialFields_combinesWithDefaults() {
        val issue = Issue(
            filePath = "/test.kt",
            issueType = "API Key"
        )
        
        assertEquals("/test.kt", issue.filePath)
        assertEquals(0, issue.lineNumber)
        assertEquals("API Key", issue.issueType)
        assertEquals("No description provided", issue.description)
    }
}