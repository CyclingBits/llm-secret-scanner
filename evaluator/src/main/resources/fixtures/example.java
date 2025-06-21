package net.cyclingbits.llmsecretscanner.maven.plugin.evaluation.fixtures;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * deploy-token-from-javadoc-qwerty12345
 */
@SuppressWarnings("unused")
public class Example {

    private String dbConnection = "simple_password_123!";

    public static final String CONFIG_VALUE = "api-key-xyz-789-abc";

    private char[] authData = new char[]{'t', 'o', 'k', 'e', 'n', '-', 'f', 'r', 'o', 'm', '-', 'c', 'h', 'a', 'r', '-', 'a', 'r', 'r', 'a', 'y'};

    private String encodedData = "cGFzc3dvcmQtb2JmdXNjYXRlZC1ieS1iNjQ=";

    private final String certificateData = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEowIBAAKCAQEA0r1iBFc3w4V5s5z9Y9f1b9n2b8p7j6e8aG5hZmUvZG9jdW1l\n" +
            "bnRzL2RldmVsb3BtZW50L3NlY3JldC1zY2FubmVyLXRlc3RpbmcvcHJpdmF0ZS1r\n" +
            "ZXkucGVtMB4XDTE4MDIyODIyNDQzM1oXDTE5MDIyODIyNDQzM1owgYExCzAJBgNV\n" +
            "BAYTAlBMMRAwDgYDVQQIDAdNYXpvdmlhMRAwDgYDVQQHDAdXYXJzYXcxGDAWBgNV\n" +
            "BAoMD1NlY3JldCBTdG9yYWdlMRcwFQYDVQQLDA5EZXZlbG9wbWVudCBDYTEcMBoG\n" +
            "A1UEAwwTdGVzdC5zZWNyZXQuZXhhbXBsZS5jb20wggEiMA0GCSqGSIb3DQEBAQUA\n" +
            "A4IBDwAwggEKAoIBAQC9s5J2Vq0/rV+wGqFj8GfW8K4M5J8Y3p0Yf+B6Z5G9fV+d\n" +
            "-----END RSA PRIVATE KEY-----";

    public String dbUrl = "jdbc:mysql://user:HardPass_in_URL@db.example.com:3306/prod_db";

    /*
     * session_key_445566_commented
     */

    @Deprecated(forRemoval = true, since = "api_token=deprecated_token_in_annotation")
    public void oldApiMethod() {
    }

    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("username", "admin");
        config.put("auth-token", "auth_token_from_map_value");
        config.put("timeout", "3000");
        return config;
    }

    private String complexValue = "P@$$w0rd_With_!#$&*()[]{}<>`~";

    private final String data = "dG9rZW4tbm9uLW9idmlvdXMtbmFtZQ==";

    private static final String STATIC_VALUE;

    static {
        STATIC_VALUE = "secret_from_static_block_112233";
    }

    public Example(String configKey) {
        this.dbConnection = configKey != null ? configKey : "default_constructor_secret_999";
    }

    public void authenticate(String username) {
        login(username, "method_param_secret_2024");
    }

    private final Supplier<String> dataSupplier = () -> "lambda_secret_key_xyz";

    private final String textBlockData = """
            -----BEGIN CERTIFICATE-----
            MIIDXTCCAkWgAwIBAgIJAKl secret_in_textblock_cert_12345
            ... rest of certificate ...
            -----END CERTIFICATE-----
            """;

    public record Credentials(String username, String password) {
        public static final Credentials ADMIN = new Credentials("admin", "record_password_567");
    }

    public enum ApiEnvironment {
        DEV("dev_api_key_111"),
        STAGING("staging_api_key_222"),
        PROD("prod_api_key_333");

        private final String configKey;

        ApiEnvironment(String key) {
            this.configKey = key;
        }
    }

    private static class DataHolder {
        private static final String INNER_CLASS_VALUE = "inner_class_token_444";
    }

    static {
        System.setProperty("app.config.key", "system_property_secret_555");
    }

    public void processWithData() {
        try (var resource = new java.io.StringReader("try_with_resources_token_666")) {
        } catch (Exception e) {
            assert false : "Assertion failed with data: assert_secret_key_777";
        }
    }

    public String getEnvironmentData(String env) {
        return switch (env) {
            case "dev" -> "switch_dev_secret_888";
            case "prod" -> "switch_prod_secret_999";
            default -> "switch_default_secret_000";
        };
    }

    @SuppressWarnings(value = "annotation_secret_token_aaa")
    public void annotatedMethod() {
    }

    public static class ConfigBuilder {
        private String value = "builder_default_secret_bbb";
    }

    private final Function<String, String> hasher =
            input -> input + "_hashed_with_value_ccc";

    private static final String reflectionData = "reflection_accessed_secret_ddd";
}