package net.cyclingbits.llmsecretscanner.evaluator.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ExpectedIssueTest {

    @Test
    fun `should create expected issue with all fields`() {
        val issue = ExpectedIssue(
            filePath = "src/main/java/Test.java",
            issueNumber = 1,
            lineNumber = 42,
            issueType = "API_KEY",
            secretValue = "sk-1234567890"
        )
        
        assertEquals("src/main/java/Test.java", issue.filePath)
        assertEquals(42, issue.lineNumber)
        assertEquals("API_KEY", issue.issueType)
        assertEquals("sk-1234567890", issue.secretValue)
    }

    @Test
    fun `should handle different issue types`() {
        val passwordIssue = ExpectedIssue(
            filePath = "config.properties",
            issueNumber = 1,
            lineNumber = 10,
            issueType = "PASSWORD",
            secretValue = "admin123"
        )
        
        val tokenIssue = ExpectedIssue(
            filePath = "app.yml",
            issueNumber = 2,
            lineNumber = 25,
            issueType = "TOKEN",
            secretValue = "ghp_xxxxxxxxxxxxxxxxxxxx"
        )
        
        assertEquals("PASSWORD", passwordIssue.issueType)
        assertEquals("TOKEN", tokenIssue.issueType)
    }

    @Test
    fun `should compare expected issues correctly`() {
        val issue1 = ExpectedIssue("file.kt", 1, 10, "API_KEY", "secret")
        val issue2 = ExpectedIssue("file.kt", 1, 10, "API_KEY", "secret")
        val issue3 = ExpectedIssue("file.kt", 2, 11, "API_KEY", "secret")
        
        assertEquals(issue1, issue2)
        assertNotEquals(issue1, issue3)
    }
}