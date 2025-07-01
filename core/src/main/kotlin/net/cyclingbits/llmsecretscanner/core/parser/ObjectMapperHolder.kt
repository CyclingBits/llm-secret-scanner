package net.cyclingbits.llmsecretscanner.core.parser

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object ObjectMapperHolder {
    val objectMapper: ObjectMapper = ObjectMapper().apply {
        registerModule(KotlinModule.Builder().build())
        registerModule(JavaTimeModule())
        registerModule(
            SimpleModule()
                .addSerializer(Instant::class.java, InstantSerializer())
                .addSerializer(Throwable::class.java, ThrowableSerializer())
        )
        configure(SerializationFeature.INDENT_OUTPUT, true)
        factory.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false)
        factory.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true)
        setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
            indentArraysWith(DefaultIndenter())
        })
    }
}

class InstantSerializer : JsonSerializer<Instant>() {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        .withZone(ZoneId.systemDefault())

    override fun serialize(value: Instant, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(formatter.format(value))
    }
}

class ThrowableSerializer : JsonSerializer<Throwable>() {
    override fun serialize(value: Throwable, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeStartObject()
        gen.writeStringField("type", value.javaClass.simpleName)
        gen.writeStringField("message", value.message)

        gen.writeArrayFieldStart("stackTrace")
        value.stackTrace.take(10).forEach { element ->
            gen.writeString("${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})")
        }
        gen.writeEndArray()

        value.cause?.let { cause ->
            gen.writeStringField("cause", "${cause.javaClass.simpleName}: ${cause.message}")
        }

        gen.writeEndObject()
    }
}