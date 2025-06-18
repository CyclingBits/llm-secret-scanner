package net.cyclingbits.llmsecretscanner.maven.plugin.evaluation.fixtures

const val GLOBAL_API_KEY = "global-kotlin-api-key-12345"

/*
 * temporary-access-token-from-file-comment
 */
class ExampleKotlin {

    companion object {
        private const val JWT_SECRET = "jwt_secret_from_companion_object_!@#$%"
        // val oldDbPass = "OldPass123_commented"
    }

    private val databasePassword = "db_password_in_kotlin"

    val encodedSecret = "S290bGluU2VjcmV0RW5jb2RlZA=="

    fun getConnectionString(user: String): String {
        val pass = "UserPassForConnectionString"
        return "mongodb://$user:$pass@cluster0.mongodb.net/test"
    }

    val adminUser = User(1, "admin", "admin-session-token-abcdef")

    val credentials = mapOf(
        "accessKey" to "AKIAIOSFODNN7EXAMPLE",
        "secretKey" to "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
    )

    private val secretPartA = "partA-"
    private val secretPartB = "partB-"
    private val secretPartC = "partC"
    val assembledSecret: String
        get() = "$secretPartA$secretPartB$secretPartC"

    fun String.toAuthHeader(): String {
        val rawToken = "raw_token_for_${this}"
        return "Bearer $rawToken"
    }
}

object BuildSecrets {
    val signingKeyPassword = "keystore_password_for_signing_!@#"
}

