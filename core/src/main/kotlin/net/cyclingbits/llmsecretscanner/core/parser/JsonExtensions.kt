package net.cyclingbits.llmsecretscanner.core.parser

fun Any.toFormattedJsonString(
    truncateLength: Int = 100
): String {
    return try {
        val processedObject = this.processForJson(truncateLength)
        ObjectMapperHolder.objectMapper.writeValueAsString(processedObject)
    } catch (e: Exception) {
        this.toString()
    }
}

private fun Any?.processForJson(
    truncateLength: Int
): Any? {
    return when (this) {
        null -> null
        is String -> this.truncate(truncateLength)

        is Map<*, *> -> this.mapValues { (_, value) ->
            value.processForJson(truncateLength)
        }

        is List<*> -> this.map { item ->
            item.processForJson(truncateLength)
        }

        is Array<*> -> this.map { item ->
            item.processForJson(truncateLength)
        }

        else -> this
    }
}

private fun String.truncate(maxLength: Int): String {
    return if (this.length <= maxLength) this else "${this.take(maxLength)}..."
}