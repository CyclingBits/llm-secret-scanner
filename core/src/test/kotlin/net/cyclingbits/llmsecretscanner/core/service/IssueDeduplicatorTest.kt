package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.model.Issue
import net.cyclingbits.llmsecretscanner.core.service.IssueDeduplicator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class IssueDeduplicatorTest {

    @Test
    fun deduplicate_noDuplicates_returnsAllIssues() {
        val issues = listOf(
            Issue("/path/file1.java", 1, 10, "Password", "secret1"),
            Issue("/path/file2.java", 2, 20, "API Key", "secret2"),
            Issue("/path/file3.java", 3, 30, "Token", "secret3")
        )

        val result = IssueDeduplicator.deduplicate(issues)

        assertEquals(3, result.size)
        assertEquals(issues, result)
    }

    @Test
    fun deduplicate_exactDuplicates_removesOne() {
        val issues = listOf(
            Issue("/path/file.java", 1, 10, "Password", "secret123"),
            Issue("/path/file.java", 2, 10, "Password", "secret123")
        )

        val result = IssueDeduplicator.deduplicate(issues)

        assertEquals(1, result.size)
        assertEquals("/path/file.java", result[0].filePath)
        assertEquals(10, result[0].lineNumber)
        assertEquals("secret123", result[0].secretValue)
    }

    @Test
    fun deduplicate_nearbyLines_removesCloseDuplicates() {
        val issues = listOf(
            Issue("/path/file.java", 1, 10, "Password", "secret123"),
            Issue("/path/file.java", 2, 11, "Password", "secret123"), // line diff = 1
            Issue("/path/file.java", 3, 12, "Password", "secret123")  // line diff = 2
        )

        val result = IssueDeduplicator.deduplicate(issues)

        assertEquals(1, result.size)
        assertEquals(10, result[0].lineNumber)
    }

    @Test
    fun deduplicate_distantLines_keepsAll() {
        val issues = listOf(
            Issue("/path/file.java", 1, 10, "Password", "secret123"),
            Issue("/path/file.java", 2, 13, "Password", "secret123"), // line diff = 3
            Issue("/path/file.java", 3, 16, "Password", "secret123")  // line diff = 6
        )

        val result = IssueDeduplicator.deduplicate(issues)

        assertEquals(3, result.size)
    }

    @Test
    fun deduplicate_differentFiles_keepsAll() {
        val issues = listOf(
            Issue("/path/file1.java", 1, 10, "Password", "secret123"),
            Issue("/path/file2.java", 2, 10, "Password", "secret123")
        )

        val result = IssueDeduplicator.deduplicate(issues)

        assertEquals(2, result.size)
    }

    @Test
    fun deduplicate_differentSecrets_keepsAll() {
        val issues = listOf(
            Issue("/path/file.java", 1, 10, "Password", "secret123"),
            Issue("/path/file.java", 2, 10, "Password", "different456")
        )

        val result = IssueDeduplicator.deduplicate(issues)

        assertEquals(2, result.size)
    }

    @Test
    fun deduplicate_secretPrefix_usesTenCharacters() {
        val issues = listOf(
            Issue("/path/file.java", 1, 10, "Password", "1234567890abcdef"),
            Issue("/path/file.java", 2, 10, "Password", "1234567890xyz")  // same first 10 chars
        )

        val result = IssueDeduplicator.deduplicate(issues)

        assertEquals(1, result.size)
    }

    @Test
    fun deduplicate_nullSecrets_handlesCorrectly() {
        val issues = listOf(
            Issue("/path/file.java", 1, 10, "Password", null),
            Issue("/path/file.java", 2, 10, "Password", null)
        )

        val result = IssueDeduplicator.deduplicate(issues)

        assertEquals(1, result.size)
    }

    @Test
    fun deduplicate_mixedNullAndValue_keepsDistinct() {
        val issues = listOf(
            Issue("/path/file.java", 1, 10, "Password", null),
            Issue("/path/file.java", 2, 10, "Password", "secret123")
        )

        val result = IssueDeduplicator.deduplicate(issues)

        assertEquals(2, result.size)
    }

    @Test
    fun deduplicate_returnsSortedByLineNumber() {
        val issues = listOf(
            Issue("/path/file.java", 3, 30, "Password", "secret3"),
            Issue("/path/file.java", 1, 10, "Password", "secret1"),
            Issue("/path/file.java", 2, 20, "Password", "secret2")
        )

        val result = IssueDeduplicator.deduplicate(issues)

        assertEquals(3, result.size)
        assertEquals(10, result[0].lineNumber)
        assertEquals(20, result[1].lineNumber)
        assertEquals(30, result[2].lineNumber)
    }

    @Test
    fun deduplicate_emptyList_returnsEmpty() {
        val issues = emptyList<Issue>()

        val result = IssueDeduplicator.deduplicate(issues)

        assertTrue(result.isEmpty())
    }

    @Test
    fun deduplicate_singleIssue_returnsSame() {
        val issues = listOf(
            Issue("/path/file.java", 1, 10, "Password", "secret123")
        )

        val result = IssueDeduplicator.deduplicate(issues)

        assertEquals(1, result.size)
        assertEquals(issues[0], result[0])
    }
}