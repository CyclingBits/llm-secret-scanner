@echo off
REM A Windows Batch script demonstrating common ways secrets are leaked.
REM Secret in a header comment: bat-script-token-abc-456

REM 1. Setting a secret as a local variable.
set "DATABASE_PASSWORD=PasswordFromSetCommand_!@#"
echo Database password has been set.

REM 2. A hardcoded API key directly in a variable.
set "API_KEY=api-key-in-bat-script-ghijkl"

REM 3. A secret passed as a command-line argument to a tool.
echo Running a command with a secret argument...
REM Example: some-tool.exe --user admin --password "super_secret_arg_pass"
curl -u "admin:%API_KEY%" https://api.example.com/data

REM 4. Old, commented-out credentials (using REM and ::).
REM old_ftp_password="old_ftp_password_789"
:: another_commented_secret="comment_secret_lmn"

REM 5. Using a secret within a command (e.g., net use).
REM This password could be cached or logged.
net use Z: \\some-server\share MySecretPassword123 /user:someuser

REM 6. A Base64 encoded token (decoding is less direct than in Bash).
set "ENCODED_TOKEN=dG9rZW4tZnJvbS1hLWJhdC1zY3JpcHQ=" & REM "token-from-a-bat-script"
echo Encoded token is: %ENCODED_TOKEN%
REM A user might have a custom script or tool to decode this.

REM 7. Concatenating a secret from parts.
set "PART_A=split"
set "PART_B=Secret"
set "PART_C=InBat"
set "FULL_SECRET=%PART_A%_%PART_B%_%PART_C%"
echo Full secret: %FULL_SECRET%

REM 8. A private key embedded as a multi-line string (uncommon but possible).
set "PRIVATE_KEY=-----BEGIN RSA PRIVATE KEY-----"
set "PRIVATE_KEY=%PRIVATE_KEY% MIIEogIBAAKCAQEAr..."
set "PRIVATE_KEY=%PRIVATE_KEY% ... (rest of the key)"
set "PRIVATE_KEY=%PRIVATE_KEY% -----END RSA PRIVATE KEY-----"

REM 9. False positive: A string that looks like a GUID, not a secret.
echo App ID: {123e4567-e89b-12d3-a456-426655440000}

REM 10. Secret in an inline comment after a command
REM The password for the service account is TempPass2024!

echo Script execution finished.
pause
