package net.cyclingbits.llmsecretscanner.core.exception

open class ScannerException(message: String, cause: Throwable? = null) : Exception(message, cause)

class TimeoutException(message: String, cause: Throwable? = null) : ScannerException(message, cause)
class AnalysisException(message: String, cause: Throwable? = null) : ScannerException(message, cause)
class DockerContainerException(message: String, cause: Throwable? = null) : ScannerException(message, cause)
class JsonParserException(message: String, cause: Throwable? = null) : ScannerException(message, cause)
class HttpException(message: String, cause: Throwable? = null) : ScannerException(message, cause)
class NoFilesFoundException(message: String, cause: Throwable? = null) : ScannerException(message, cause)