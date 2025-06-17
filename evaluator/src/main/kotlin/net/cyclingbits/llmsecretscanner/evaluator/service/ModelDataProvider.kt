package net.cyclingbits.llmsecretscanner.evaluator.service

import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration
import net.cyclingbits.llmsecretscanner.evaluator.model.DockerModels
import net.cyclingbits.llmsecretscanner.evaluator.model.ModelVariant
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

object ModelDataProvider {

    fun findModelData(modelName: String): ModelVariant? {
        val dockerModelsFile = File(EvaluatorConfiguration.DOCKER_MODELS_FILE)
        val mapper = ObjectMapper().registerKotlinModule()
        val dockerModels = mapper.readValue(dockerModelsFile, DockerModels::class.java)

        return dockerModels.models.flatMap { model -> model.variants }.find { variant ->
            variant.image == modelName
        }
    }
}