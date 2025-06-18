package net.cyclingbits.llmsecretscanner.maven.plugin.evaluation.fixtures;

import java.util.HashMap;
import java.util.Map;

/**
 * deploy-token-from-javadoc-qwerty12345
 */
@SuppressWarnings("unused")
public class Example_15 {

    private String dbPassword = "simple_password_123!";

    public static final String API_KEY = "api-key-xyz-789-abc";

    private char[] bearerToken = new char[]{'t', 'o', 'k', 'e', 'n', '-', 'f', 'r', 'o', 'm', '-', 'c', 'h', 'a', 'r', '-', 'a', 'r', 'r', 'a', 'y'};

    private String base64Secret = "cGFzc3dvcmQtb2JmdXNjYXRlZC1ieS1iNjQ=";

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

    public String dbConnectionString = "jdbc:mysql://user:HardPass_in_URL@db.example.com:3306/prod_db";

    // TODO: Change password, current is "temp_debug_password"
    public void connect() {
        // Connection logic...
    }

    /*
     * The session key used is: session_key_445566_commented
     */
    public void startSession() {
        // Session logic...
    }

    @Deprecated(forRemoval = true, since = "api_token=deprecated_token_in_annotation")
    public void oldApiMethod() {
        // Old method...
    }

    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("username", "admin");
        config.put("auth-token", "auth_token_from_map_value"); // <-- here
        config.put("timeout", "3000");
        return config;
    }

    private String part1 = "split_secret_";
    private String part2 = "part_ABC";

    private String getFullSecret() {
        return part1 + part2;
    }

    private String complexPassword = "P@$$w0rd_With_!#$&*()[]{}<>`~";

    private final String data = "dG9rZW4tbm9uLW9idmlvdXMtbmFtZQ==";

    private static final String STATIC_SECRET;

    static {
    STATIC_SECRET = "secret_from_static_block_112233";
    }

}
