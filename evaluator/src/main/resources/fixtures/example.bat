@echo off
REM bat-script-token-abc-456

set "DATABASE_PASSWORD=PasswordFromSetCommand_!@#"
echo Database password has been set.

set "API_KEY=api-key-in-bat-script-ghijkl"

echo Running a command with a secret argument...
REM Example: some-tool.exe --user admin --password "super_secret_arg_pass"
curl -u "admin:%API_KEY%" https://api.example.com/data

REM old_ftp_password="old_ftp_password_789"
:: another_commented_secret="comment_secret_lmn"

REM This password could be cached or logged.
net use Z: \\some-server\share MySecretPassword123 /user:someuser

set "ENCODED_TOKEN=dG9rZW4tZnJvbS1hLWJhdC1zY3JpcHQ=" & REM "token-from-a-bat-script"
echo Encoded token is: %ENCODED_TOKEN%

set "PART_A=split"
set "PART_B=Secret"
set "PART_C=InBat"
set "FULL_SECRET=%PART_A%_%PART_B%_%PART_C%"
echo Full secret: %FULL_SECRET%

set "PRIVATE_KEY=-----BEGIN RSA PRIVATE KEY-----"
set "PRIVATE_KEY=%PRIVATE_KEY% MIIEogIBAAKCAQEAr..."
set "PRIVATE_KEY=%PRIVATE_KEY% ... (rest of the key)"
set "PRIVATE_KEY=%PRIVATE_KEY% -----END RSA PRIVATE KEY-----"

REM The password for the service account is TempPass2024!

echo Script execution finished.
pause
