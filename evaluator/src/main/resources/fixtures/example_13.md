# Examples of Secrets in a Markdown File

This document shows how secrets can be embedded in `.md` files. Scanners should be able to detect these patterns.

> **Secret in a blockquote:** `block-quote-secret-token-112233`

---

## 1. Database Access Credentials

Login credentials are often found in configuration documentation.

- **User:** `json_user`
- **Password:** `PasswordFromJsonFile123#`
- **Comment in code:** Below is a configuration example in code:
  ```json
  {
    "user": "admin",
    "password": "another_password_in_code_block"
  }
  ```

---

## 2. API Keys and Connection Strings

API keys are often pasted directly into README files for a quick start.

- **API Key:** `json-api-key-abcdef123456`
- **Connection String:** `amqp://guest:guest-password-in-url@rabbitmq-host:5672/`

---

## 3. Tokens and Certificates

JWT tokens and private keys can be embedded in code blocks.

### JWT Token
Secret for signing JWT tokens: `super_secret_jwt_key_from_nested_json`

### Private Key PEM
Below is an example of a private key that might have been pasted into documentation.

```pem
-----BEGIN RSA PRIVATE KEY-----
MIIEogIBAAKCAQEAr...rest of key...
-----END RSA PRIVATE KEY-----
```

---

## 4. List of Credentials

A table is a common way to present a list of credentials.

| Service      | Access Key            | Secret                                      |
|--------------|-----------------------|---------------------------------------------|
| AWS          | `AKIAEXAMPLE1234567`  | `wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY`   |
| Google Maps  | `AIzaSyA...`          | *(none)* |

---

## 5. Encoded Values and False Positives

- **Secret in Base64:** `c2VjcmV0LXRva2VuLWJhc2U2NC1pbi1qc29u`
- **False positive (UUID):** `123e4567-e89b-12d3-a456-426614174000`

<!-- Secret in an HTML comment: html-comment-secret-in-md-file-xyz -->
