package net.cyclingbits.llmsecretscanner.core.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class IssueTest {

    @Test
    fun `should create issue with all fields`() {
        val issue = Issue(
            lineNumber = 42,
            issueType = "API_KEY",
            secretValue = "sk-1234567890"
        )
        
        assertEquals(42, issue.lineNumber)
        assertEquals("API_KEY", issue.issueType)
        assertEquals("sk-1234567890", issue.secretValue)
    }

    @Test
    fun `should create issue with null secret value`() {
        val issue = Issue(
            lineNumber = 10,
            issueType = "PASSWORD",
            secretValue = null
        )
        
        assertEquals(10, issue.lineNumber)
        assertEquals("PASSWORD", issue.issueType)
        assertNull(issue.secretValue)
    }

    @Test
    fun `should compare issues correctly`() {
        val issue1 = Issue(42, "API_KEY", "secret")
        val issue2 = Issue(42, "API_KEY", "secret")
        val issue3 = Issue(43, "API_KEY", "secret")
        
        assertEquals(issue1, issue2)
        assertNotEquals(issue1, issue3)
    }
}