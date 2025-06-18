#!/bin/bash

# shell-script-token-xyz-123

export DATABASE_PASSWORD="PasswordFromExport_!@#"
echo "DB Password variable has been set."

API_KEY="api-key-directly-in-script-abcdef123"

# Example usage: ./deploy.sh --user admin --password "super_secret_arg_pass"
echo "Running a command with a secret argument..."
curl -u "admin:${API_KEY}" https://api.example.com/data

# ssh_password="old_ssh_password_123"

echo "MySuperSecretPasswordForPiping" | some_command --read-from-stdin

ENCODED_TOKEN="dG9rZW4tZnJvbS1hLXNoZWxsLXNjcmlwdA=="
DECODED_TOKEN=$(echo $ENCODED_TOKEN | base64 --decode)
echo "Decoded token: $DECODED_TOKEN"

ftp -n ftp.example.com <<EOF
quote USER ftp_user
quote PASS FtpPasswordInHereDoc123
binary
get backup.zip
quit
EOF

PRIVATE_SSH_KEY="-----BEGIN OPENSSH PRIVATE KEY-----
b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAACFwAAAAdzc2gtcn...
... (rest of the key)
-----END OPENSSH PRIVATE KEY-----"

docker login -u dockeruser -p DockerHubPassword123 # TODO: Move this to a secure secret store

echo "Script execution finished."
