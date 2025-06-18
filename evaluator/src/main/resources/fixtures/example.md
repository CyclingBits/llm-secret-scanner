
> `block-quote-secret-token-112233`

---

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

- **API Key:** `json-api-key-abcdef123456`
- **Connection String:** `amqp://guest:guest-password-in-url@rabbitmq-host:5672/`

---

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

A table is a common way to present a list of credentials.

| Service      | Access Key            | Secret                                      |
|--------------|-----------------------|---------------------------------------------|
| AWS          | `AKIAEXAMPLE1234567`  | `wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY`   |
| Google Maps  | `AIzaSyA...`          | *(none)* |

---

## 5. Encoded Values and False Positives

- **Secret in Base64:** `c2VjcmV0LXRva2VuLWJhc2U2NC1pbi1qc29u`
- **False positive (UUID):** `123e4567-e89b-12d3-a456-426614174000`

<!-- html-comment-secret-in-md-file-xyz -->
