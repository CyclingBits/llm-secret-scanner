package com.example.falsepositive;

import java.util.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class FalsePositiveTestCase {

    private static final String API_BASE_URL = "https://api.example.com/v1";
    private static final String DEFAULT_TOKEN_PREFIX = "Bearer ";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
    private static final String SECRET_KEY_ALGORITHM = "AES";
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String AUTH_TOKEN_COOKIE = "auth_token";
    
    private static final Map<String, String> CONFIG_KEYS = Map.of(
        "database.password.encrypted", "true",
        "api.key.rotation.enabled", "false",
        "secret.management.provider", "HashiCorp Vault",
        "token.expiry.hours", "24",
        "private.key.format", "PKCS8"
    );

    private String passwordValidator;
    private String apiKeyManager;
    private String secretConfiguration;
    private String tokenGenerator;
    private String privateKeyLoader;

    public boolean validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        Pattern upperCase = Pattern.compile("[A-Z]");
        Pattern lowerCase = Pattern.compile("[a-z]");
        Pattern digit = Pattern.compile("\\d");
        Pattern specialChar = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");
        
        return upperCase.matcher(password).find() &&
               lowerCase.matcher(password).find() &&
               digit.matcher(password).find() &&
               specialChar.matcher(password).find();
    }

    public boolean isValidApiKeyFormat(String apiKey) {
        if (apiKey == null) return false;
        
        String prefixPattern = "sk-[a-zA-Z0-9]{48}";
        String uuidPattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
        String base64Pattern = "^[A-Za-z0-9+/]*={0,2}$";
        
        return apiKey.matches(prefixPattern) || 
               apiKey.matches(uuidPattern) ||
               (apiKey.length() >= 16 && apiKey.matches(base64Pattern));
    }

    public String generateSecureToken(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        
        for (int i = 0; i < length; i++) {
            token.append(characters.charAt(random.nextInt(characters.length())));
        }
        
        return token.toString();
    }

    public class SecretManager {
        private final Map<String, String> secretCache = new ConcurrentHashMap<>();
        
        public void loadSecretsFromVault() {
            System.out.println("Loading secrets from HashiCorp Vault...");
        }
        
        public String getSecret(String secretName) {
            return secretCache.get(secretName);
        }
        
        public void rotateApiKey(String serviceName) {
            System.out.println("Rotating API key for service: " + serviceName);
        }
        
        public boolean validateSecretFormat(String secret) {
            return secret != null && secret.length() >= 16;
        }
    }

    public class DatabaseConfig {
        private String host = "localhost";
        private int port = 5432;
        private String database = "myapp";
        private String username = "app_user";
        
        private static final String PASSWORD_PROPERTY = "database.password";
        private static final String SSL_CERT_PROPERTY = "database.ssl.certificate";
        private static final String PRIVATE_KEY_PROPERTY = "database.ssl.private_key";
        
        public Properties getDatabaseProperties() {
            Properties props = new Properties();
            props.setProperty("host", host);
            props.setProperty("port", String.valueOf(port));
            props.setProperty("database", database);
            props.setProperty("username", username);
            return props;
        }
        
        public String getConnectionString() {
            return String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
        }
    }

    public class CryptoUtils {
        
        public byte[] generateRandomKey(int keySize) {
            try {
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(keySize);
                return keyGen.generateKey().getEncoded();
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate key", e);
            }
        }
        
        public String hashPassword(String password, String salt) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(salt.getBytes());
                byte[] hashedPassword = md.digest(password.getBytes());
                return Base64.getEncoder().encodeToString(hashedPassword);
            } catch (Exception e) {
                throw new RuntimeException("Failed to hash password", e);
            }
        }
        
        public boolean verifySignature(byte[] data, byte[] signature, String publicKey) {
            System.out.println("Verifying signature with public key");
            return true;
        }
        
        public String encryptData(String data, String keyAlias) {
            System.out.println("Encrypting data with key alias: " + keyAlias);
            return "encrypted_" + data;
        }
    }

    public class TestDataProvider {
        
        private static final String EXAMPLE_API_KEY_FORMAT = "ak_live_1234567890abcdef";
        private static final String EXAMPLE_JWT_FORMAT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.example.signature";
        private static final String EXAMPLE_PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----";
        private static final String EXAMPLE_SSH_KEY_FORMAT = "ssh-rsa AAAAB3NzaC1yc2EAAAA...";
        
        public List<String> getTestApiKeyFormats() {
            return Arrays.asList(
                "sk-test_1234567890abcdef1234567890abcdef12345678",
                "pk-test_1234567890abcdef1234567890abcdef12345678",
                "rk-test_1234567890abcdef1234567890abcdef12345678"
            );
        }
        
        public Map<String, String> getConfigurationExamples() {
            Map<String, String> examples = new HashMap<>();
            examples.put("api_key_length", "32");
            examples.put("token_expiry_seconds", "3600");
            examples.put("password_min_length", "8");
            examples.put("private_key_algorithm", "RSA");
            examples.put("secret_rotation_days", "90");
            return examples;
        }
        
        public String generatePlaceholderToken() {
            return "placeholder_token_" + System.currentTimeMillis();
        }
        
        public String createTestCredential(String type) {
            return "test_" + type + "_" + UUID.randomUUID().toString().replace("-", "");
        }
    }

    public static final String[] CONFIGURATION_DOCS = {
        "Set your API key in the environment variable API_KEY",
        "The password should be stored in a secure vault",
        "Private keys must be in PEM format",
        "Tokens expire after 24 hours",
        "Secret rotation is performed weekly",
        "Database credentials are managed by the platform"
    };

    public static final String[] CODE_COMMENTS = {
        "// TODO: Replace hardcoded password with environment variable",
        "// FIXME: API key should be loaded from configuration",
        "// NOTE: This is a placeholder for the actual secret",
        "/* The private key will be loaded at runtime */",
        "/** @param apiKey The API key for authentication */",
        "// WARNING: Never commit secrets to version control"
    };

    private static final Pattern API_KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9]{32,}$");
    private static final Pattern TOKEN_PATTERN = Pattern.compile("^[A-Za-z0-9+/]*={0,2}$");
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    public String getApiKeyFromEnvironment() {
        return System.getenv("API_KEY");
    }
    
    public String getDatabasePasswordFromSystem() {
        return System.getProperty("db.password");
    }
    
    public String getPrivateKeyPath() {
        return System.getProperty("user.home") + "/.ssh/id_rsa";
    }

    public void processConfigurationFile(String configPath) {
        Properties config = new Properties();
        String[] configLines = {
            "database.host=localhost",
            "database.port=5432",
            "database.name=myapp",
            "api.endpoint=https://api.example.com",
            "auth.method=bearer",
            "ssl.enabled=true",
            "logging.level=INFO"
        };
        
        for (String line : configLines) {
            String[] parts = line.split("=");
            if (parts.length == 2) {
                config.setProperty(parts[0], parts[1]);
            }
        }
    }

    public class AuthenticationService {
        private final String tokenEndpoint = "/oauth/token";
        private final String authHeaderName = "Authorization";
        private final String apiKeyHeaderName = "X-API-Key";
        
        public boolean authenticateWithBearerToken(String token) {
            return token != null && token.startsWith("Bearer ");
        }
        
        public String extractApiKeyFromHeader(Map<String, String> headers) {
            return headers.get(apiKeyHeaderName);
        }
        
        public boolean isValidTokenFormat(String token) {
            return token != null && token.length() >= 20 && token.matches("[A-Za-z0-9+/=]+");
        }
        
        public void logAuthenticationAttempt(String username, boolean success) {
            System.out.println("Authentication attempt for user: " + username + 
                             ", success: " + success);
        }
    }

    public class PasswordUtils {
        private static final String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        
        public String generateSalt(int length) {
            SecureRandom random = new SecureRandom();
            StringBuilder salt = new StringBuilder();
            for (int i = 0; i < length; i++) {
                salt.append(SALT_CHARS.charAt(random.nextInt(SALT_CHARS.length())));
            }
            return salt.toString();
        }
        
        public boolean checkPasswordComplexity(String password) {
            if (password == null || password.length() < 8) return false;
            
            boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
            boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
            boolean hasDigit = password.chars().anyMatch(Character::isDigit);
            boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()".indexOf(ch) >= 0);
            
            return hasUpper && hasLower && hasDigit && hasSpecial;
        }
        
        public String maskPassword(String password) {
            if (password == null) return null;
            return "*".repeat(password.length());
        }
        
        public long calculatePasswordEntropy(String password) {
            if (password == null) return 0;
            
            Set<Character> uniqueChars = new HashSet<>();
            for (char c : password.toCharArray()) {
                uniqueChars.add(c);
            }
            
            return Math.round(password.length() * Math.log(uniqueChars.size()) / Math.log(2));
        }
    }

    public static void main(String[] args) {
        FalsePositiveTestCase testCase = new FalsePositiveTestCase();
        
        System.out.println("Testing password validation...");
        boolean isStrong = testCase.validatePasswordStrength("TestPassword123!");
        System.out.println("Password strength valid: " + isStrong);
        
        System.out.println("Validating API key format...");
        boolean validFormat = testCase.isValidApiKeyFormat("sk-test1234567890abcdef1234567890abcdef12345678");
        System.out.println("API key format valid: " + validFormat);
        
        System.out.println("Generating secure token...");
        String token = testCase.generateSecureToken(32);
        System.out.println("Generated token length: " + token.length());
        
        Properties dbProps = testCase.new DatabaseConfig().getDatabaseProperties();
        System.out.println("Database configuration loaded with " + dbProps.size() + " properties");
        
        CryptoUtils crypto = testCase.new CryptoUtils();
        byte[] key = crypto.generateRandomKey(256);
        System.out.println("Generated key size: " + key.length + " bytes");
        
        TestDataProvider testData = testCase.new TestDataProvider();
        List<String> keyFormats = testData.getTestApiKeyFormats();
        System.out.println("Loaded " + keyFormats.size() + " test key formats");
        
        for (String doc : CONFIGURATION_DOCS) {
            System.out.println("Doc: " + doc);
        }
        
        for (String comment : CODE_COMMENTS) {
            System.out.println("Comment: " + comment);
        }
        
        AuthenticationService authService = testCase.new AuthenticationService();
        boolean tokenValid = authService.authenticateWithBearerToken("Bearer placeholder_token");
        System.out.println("Token authentication valid: " + tokenValid);
        
        PasswordUtils passwordUtils = testCase.new PasswordUtils();
        String salt = passwordUtils.generateSalt(16);
        System.out.println("Generated salt length: " + salt.length());
        
        testCase.processConfigurationFile("config.properties");
        System.out.println("Configuration processing completed");
    }
}