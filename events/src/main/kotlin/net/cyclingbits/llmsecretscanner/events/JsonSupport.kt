package net.cyclingbits.llmsecretscanner.events

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.time.Instant

object JsonSupport {
    val objectMapper: ObjectMapper = ObjectMapper().apply {
        registerModule(KotlinModule.Builder().build())
        registerModule(JavaTimeModule())
        registerModule(SimpleModule().addSerializer(Instant::class.java, InstantSerializer()))
        configure(SerializationFeature.INDENT_OUTPUT, true)
    }
}

private class InstantSerializer : JsonSerializer<Instant>() {
    override fun serialize(value: Instant, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(Formatter.formatForJson(value))
    }
}

fun Any.toFormattedJsonString(truncateLength: Int = 100): String {
    return try {
        val processedObject = this.processForJson(truncateLength)
        JsonSupport.objectMapper.writeValueAsString(processedObject)
    } catch (e: Exception) {
        this.toString()
    }
}

private fun Any?.processForJson(truncateLength: Int): Any? {
    return when (this) {
        null -> null
        is String -> this.truncate(truncateLength)
        is Map<*, *> -> this.mapValues { (_, value) -> value.processForJson(truncateLength) }
        is List<*> -> this.map { item -> item.processForJson(truncateLength) }
        is Array<*> -> this.map { item -> item.processForJson(truncateLength) }
        else -> this
    }
}

private fun String.truncate(maxLength: Int): String {
    return if (this.length <= maxLength) this else "${this.take(maxLength)}..."
}