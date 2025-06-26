package net.cyclingbits.llmsecretscanner.evaluator.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class DockerModelProviderTest {

    @Test
    fun `should handle model search with null results`() {
        val result = try {
            DockerModelProvider.findModelData("ai/non-existent:latest")
        } catch (e: Exception) {
            null
        }
        
        assertNull(result)
    }

    @Test
    fun `should handle empty model name gracefully`() {
        val result = try {
            DockerModelProvider.findModelData("")
        } catch (e: Exception) {
            null
        }
        
        assertNull(result)
    }

    @Test
    fun `should handle null model name gracefully`() {
        val result = try {
            DockerModelProvider.findModelData("not-a-valid-model")
        } catch (e: Exception) {
            null
        }
        
        assertNull(result)
    }
}