package net.cyclingbits.llmsecretscanner.maven.plugin.evaluation.fixtures

// 1. Top-level constant property with an API key.
const val GLOBAL_API_KEY = "global-kotlin-api-key-12345"

/*
 * File-level comment containing a temporary access token:
 * temp_token = temporary-access-token-from-file-comment
 */
class ExampleKotlin {

    // 2. Secret inside a companion object.
    companion object {
        private const val JWT_SECRET = "jwt_secret_from_companion_object_!@#$%"
        // An old, commented-out password
        // val oldDbPass = "OldPass123_commented"
    }

    // 3. Password in a private property.
    private val databasePassword = "db_password_in_kotlin"

    // 4. Base64 encoded secret.
    val encodedSecret = "S290bGluU2VjcmV0RW5jb2RlZA=="

    // 5. Secret inside a string template.
    fun getConnectionString(user: String): String {
        val pass = "UserPassForConnectionString"
        return "mongodb://$user:$pass@cluster0.mongodb.net/test"
    }

    // 6. A data class that might hold sensitive information.
    data class User(val id: Int, val username: String, val sessionToken: String)

    val adminUser = User(1, "admin", "admin-session-token-abcdef")

    // 7. A map containing credentials.
    val credentials = mapOf(
        "accessKey" to "AKIAIOSFODNN7EXAMPLE",
        "secretKey" to "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
    )

    // 8. False positive: A regex that looks like a secret.
    val hexPattern = "[0-9a-fA-F]{32}"

    // 9. Secret constructed from parts.
    private val secretPartA = "partA-"
    private val secretPartB = "partB-"
    private val secretPartC = "partC"
    val assembledSecret: String
        get() = "$secretPartA$secretPartB$secretPartC"

    // 10. Extension function that could expose a secret.
    fun String.toAuthHeader(): String {
        val rawToken = "raw_token_for_${this}"
        return "Bearer $rawToken"
    }
}

// 11. Example of a secret in build.gradle.kts style (usually in a separate file).
// This is just a simulation within a .kt file.
object BuildSecrets {
    val signingKeyPassword = "keystore_password_for_signing_!@#"
}

