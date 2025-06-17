package net.cyclingbits.llmsecretscanner.maven.plugin.evaluation.fixtures;

import java.util.HashMap;
import java.util.Map;

/**
 * Javadoc comment with a secret:
 * For deployment, use the token: deploy-token-from-javadoc-qwerty12345
 */
@SuppressWarnings("unused")
public class Example_15 {

    // 1. Simple hardcoded password in a private field
    private String dbPassword = "simple_password_123!";

    // 2. API key in a public constant
    public static final String API_KEY = "api-key-xyz-789-abc";

    // 3. Token in a char array (a slightly more secure way)
    private char[] bearerToken = new char[]{'t', 'o', 'k', 'e', 'n', '-', 'f', 'r', 'o', 'm', '-', 'c', 'h', 'a', 'r', '-', 'a', 'r', 'r', 'a', 'y'};

    // 4. Base64 encoded secret
    private String base64Secret = "cGFzc3dvcmQtb2JmdXNjYXRlZC1ieS1iNjQ="; // "password-obfuscated-by-b64"

    // 5. PEM certificate as a multiline String
    private final String privateKeyPem = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEowIBAAKCAQEA0r1iBFc3w4V5s5z9Y9f1b9n2b8p7j6e8aG5hZmUvZG9jdW1l\n" +
            "bnRzL2RldmVsb3BtZW50L3NlY3JldC1zY2FubmVyLXRlc3RpbmcvcHJpdmF0ZS1r\n" +
            "ZXkucGVtMB4XDTE4MDIyODIyNDQzM1oXDTE5MDIyODIyNDQzM1owgYExCzAJBgNV\n" +
            "BAYTAlBMMRAwDgYDVQQIDAdNYXpvdmlhMRAwDgYDVQQHDAdXYXJzYXcxGDAWBgNV\n" +
            "BAoMD1NlY3JldCBTdG9yYWdlMRcwFQYDVQQLDA5EZXZlbG9wbWVudCBDYTEcMBoG\n" +
            "A1UEAwwTdGVzdC5zZWNyZXQuZXhhbXBsZS5jb20wggEiMA0GCSqGSIb3DQEBAQUA\n" +
            "A4IBDwAwggEKAoIBAQC9s5J2Vq0/rV+wGqFj8GfW8K4M5J8Y3p0Yf+B6Z5G9fV+d\n" +
            "// ... (rest of the key) ...\n" +
            "-----END RSA PRIVATE KEY-----";

    // 6. Secret as part of a URL (e.g., connection string)
    public String dbConnectionString = "jdbc:mysql://user:HardPass_in_URL@db.example.com:3306/prod_db";

    // 7. Password in a single-line comment (often found during debugging)
    // TODO: Change password, current is "temp_debug_password"
    public void connect() {
        // Connection logic...
    }

    /*
     * 8. Key in a multi-line comment.
     * The session key used is: session_key_445566_commented
     */
    public void startSession() {
        // Session logic...
    }

    // 9. False positive - a variable name suggesting a secret, but the value is safe
    public String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

    // 10. Secret in an annotation (rare, but possible)
    @Deprecated(forRemoval = true, since = "api_token=deprecated_token_in_annotation")
    public void oldApiMethod() {
        // Old method...
    }

    // 11. Secret hidden in a map or other data structure
    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("username", "admin");
        config.put("auth-token", "auth_token_from_map_value"); // <-- here
        config.put("timeout", "3000");
        return config;
    }

    // 12. A secret split into parts
    private String part1 = "split_secret_";
    private String part2 = "part_ABC";

    private String getFullSecret() {
        return part1 + part2;
    }

    // 13. Secret with special characters
    private String complexPassword = "P@$$w0rd_With_!#$&*()[]{}<>`~";

    // 14. An "innocent" variable that is actually a key
    private final String data = "dG9rZW4tbm9uLW9idmlvdXMtbmFtZQ==";

    // 15. Secret in a static block
    private static final String STATIC_SECRET;

    static {
        // Sometimes secrets are loaded this way to hide their origin
        STATIC_SECRET = "secret_from_static_block_112233";
    }

}
