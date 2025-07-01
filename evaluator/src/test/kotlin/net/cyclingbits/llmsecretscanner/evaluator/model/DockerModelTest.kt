package net.cyclingbits.llmsecretscanner.evaluator.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class DockerModelTest {

    @Test
    fun `should create docker model with all fields`() {
        val model = DockerModel(
            modelName = "ai/phi4:latest",
            parameters = "14B",
            quantization = "Q4_K_M",
            contextWindow = "128k",
            vram = "8GB",
            size = "8.4GB"
        )
        
        assertEquals("ai/phi4:latest", model.modelName)
        assertEquals("14B", model.parameters)
        assertEquals("128k", model.contextWindow)
        assertEquals("8.4GB", model.size)
    }

    @Test
    fun `should handle different model sizes`() {
        val smallModel = DockerModel(
            modelName = "ai/phi3:latest",
            parameters = "3.8B",
            quantization = "Q4_K_M",
            contextWindow = "128k",
            vram = "4GB",
            size = "2.3GB"
        )
        
        val largeModel = DockerModel(
            modelName = "ai/llama3.1:latest",
            parameters = "70B",
            quantization = "Q4_K_M",
            contextWindow = "128k",
            vram = "64GB",
            size = "40GB"
        )
        
        assertEquals("3.8B", smallModel.parameters)
        assertEquals("70B", largeModel.parameters)
        assertEquals("2.3GB", smallModel.size)
        assertEquals("40GB", largeModel.size)
    }

    @Test
    fun `should compare docker models correctly`() {
        val model1 = DockerModel("ai/test:latest", "1B", "Q4_K_M", "32k", "2GB", "1GB")
        val model2 = DockerModel("ai/test:latest", "1B", "Q4_K_M", "32k", "2GB", "1GB")
        val model3 = DockerModel("ai/other:latest", "1B", "Q4_K_M", "32k", "2GB", "1GB")
        
        assertEquals(model1, model2)
        assertNotEquals(model1, model3)
    }
}