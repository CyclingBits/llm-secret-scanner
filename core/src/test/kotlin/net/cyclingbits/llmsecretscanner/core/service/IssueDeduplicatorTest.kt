package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.model.Issue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class IssueDeduplicatorTest {

    @Test
    fun `should remove duplicate issues on same line`() {
        val issues = listOf(
            Issue(10, "API_KEY", "sk-1234567890"),
            Issue(10, "API_KEY", "sk-1234567890")
        )
        
        val result = IssueDeduplicator.deduplicate(issues)
        
        assertEquals(1, result.size)
        assertEquals(10, result[0].lineNumber)
    }

    @Test
    fun `should remove issues within 2 lines with same secret prefix`() {
        val issues = listOf(
            Issue(10, "API_KEY", "sk-1234567890abcdef"),
            Issue(11, "API_KEY", "sk-1234567890xyz"),
            Issue(12, "API_KEY", "sk-1234567890123")
        )
        
        val result = IssueDeduplicator.deduplicate(issues)
        
        assertEquals(1, result.size)
        assertEquals(10, result[0].lineNumber)
    }

    @Test
    fun `should keep issues on different lines with different secrets`() {
        val issues = listOf(
            Issue(10, "API_KEY", "sk-1234567890"),
            Issue(15, "API_KEY", "pk-0987654321"),
            Issue(20, "PASSWORD", "password123")
        )
        
        val result = IssueDeduplicator.deduplicate(issues)
        
        assertEquals(3, result.size)
    }

    @Test
    fun `should handle empty list`() {
        val result = IssueDeduplicator.deduplicate(emptyList())
        
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should sort results by line number`() {
        val issues = listOf(
            Issue(20, "PASSWORD", "pass2"),
            Issue(10, "API_KEY", "key1"),
            Issue(15, "TOKEN", "token1")
        )
        
        val result = IssueDeduplicator.deduplicate(issues)
        
        assertEquals(3, result.size)
        assertEquals(10, result[0].lineNumber)
        assertEquals(15, result[1].lineNumber)
        assertEquals(20, result[2].lineNumber)
    }
}