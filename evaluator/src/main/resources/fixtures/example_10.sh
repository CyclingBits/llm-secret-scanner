#!/bin/bash

# A shell script demonstrating common ways secrets are leaked.
# Secret in a header comment: shell-script-token-xyz-123

# 1. Exporting a secret as an environment variable.
export DATABASE_PASSWORD="PasswordFromExport_!@#"
echo "DB Password variable has been set."

# 2. A hardcoded API key directly in a variable.
API_KEY="api-key-directly-in-script-abcdef123"

# 3. A secret passed as a command-line argument (this could be logged by the shell).
# Example usage: ./deploy.sh --user admin --password "super_secret_arg_pass"
echo "Running a command with a secret argument..."
curl -u "admin:${API_KEY}" https://api.example.com/data

# 4. Old, commented-out credentials.
# ssh_password="old_ssh_password_123"

# 5. Using a secret within a command pipeline.
# The password could be visible in the process list (`ps aux`).
echo "MySuperSecretPasswordForPiping" | some_command --read-from-stdin

# 6. A base64 encoded token.
ENCODED_TOKEN="dG9rZW4tZnJvbS1hLXNoZWxsLXNjcmlwdA=="
DECODED_TOKEN=$(echo $ENCODED_TOKEN | base64 --decode)
echo "Decoded token: $DECODED_TOKEN"

# 7. A secret within a here-doc.
ftp -n ftp.example.com <<EOF
quote USER ftp_user
quote PASS FtpPasswordInHereDoc123
binary
get backup.zip
quit
EOF

# 8. Sourcing another file that contains secrets.
# The sourced file itself is a vulnerability.
# source ./secrets.conf

# 9. A private key embedded as a multi-line string.
PRIVATE_SSH_KEY="-----BEGIN OPENSSH PRIVATE KEY-----
b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAACFwAAAAdzc2gtcn...
... (rest of the key)
-----END OPENSSH PRIVATE KEY-----"

# 10. False positive: A command with a hash-like string that is not a secret.
git checkout a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2

# 11. Secret in an inline comment
docker login -u dockeruser -p DockerHubPassword123 # TODO: Move this to a secure secret store

echo "Script execution finished."
