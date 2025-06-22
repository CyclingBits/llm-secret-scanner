package net.cyclingbits.llmsecretscanner.core.service

import net.cyclingbits.llmsecretscanner.core.model.Issue
import kotlin.math.abs

object IssueDeduplicator {

    fun deduplicate(issues: List<Issue>): List<Issue> {
        val uniqueIssues = mutableListOf<Issue>()
        
        for (issue in issues) {
            val isDuplicateFound = uniqueIssues.any { existing ->
                isDuplicate(existing, issue)
            }
            
            if (!isDuplicateFound) {
                uniqueIssues.add(issue)
            }
        }
        
        return uniqueIssues.sortedBy { it.lineNumber }
    }

    private fun isDuplicate(issue1: Issue, issue2: Issue): Boolean {
        return abs(issue1.lineNumber - issue2.lineNumber) <= 2 &&
                issue1.filePath == issue2.filePath &&
                (issue1.secretValue?.take(10) ?: "") == (issue2.secretValue?.take(10) ?: "")
    }
}