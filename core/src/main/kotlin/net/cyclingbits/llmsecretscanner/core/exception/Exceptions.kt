package net.cyclingbits.llmsecretscanner.core.exception

class TimeoutException(message: String, cause: Throwable? = null) : Exception(message, cause)
class AnalysisException(message: String, cause: Throwable? = null) : Exception(message, cause)
class DockerContainerException(message: String, cause: Throwable? = null) : Exception(message, cause)
class JsonParserException(message: String, cause: Throwable? = null) : Exception(message, cause)
class NoFilesFoundException(message: String, cause: Throwable? = null) : Exception(message, cause)