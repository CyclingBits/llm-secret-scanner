package net.cyclingbits.llmsecretscanner.evaluator.service

import net.cyclingbits.llmsecretscanner.core.parser.ObjectMapperHolder
import net.cyclingbits.llmsecretscanner.evaluator.config.EvaluatorConfiguration.DOCKER_MODELS_FILE
import net.cyclingbits.llmsecretscanner.evaluator.model.DockerModel

object DockerModelProvider {

    fun findModelData(modelName: String): DockerModel? {
        val dockerModels = ObjectMapperHolder.objectMapper.readValue(DOCKER_MODELS_FILE, Array<DockerModel>::class.java).toList()

        return dockerModels.find { model ->
            model.modelName == modelName
        }
    }
}