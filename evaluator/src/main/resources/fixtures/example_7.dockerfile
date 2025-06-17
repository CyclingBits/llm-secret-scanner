# Dockerfile for demonstrating secret storage patterns.
# This is a critical file to scan for secrets in containerized applications.
# Secret in header comment: dockerfile-header-token-1a2b3c

# Use a common base image
FROM ubuntu:22.04

# 1. Using ENV to set a plaintext password. This is a very common and dangerous practice.
# The secret will be baked into a layer of the container image.
ENV DATABASE_PASSWORD="PasswordInDockerfileEnv!@#"

# 2. Using ARG to pass a build-time secret.
# While the ARG itself doesn't persist, if it's used to set an ENV, the secret remains.
ARG BUILD_TIME_TOKEN="build-time-secret-token-xyz"
ENV API_TOKEN=${BUILD_TIME_TOKEN}

# 3. Running a command that includes an inline secret.
# This gets stored in the image layer's history.
RUN apt-get update && apt-get install -y curl wget \
    && curl -u "admin:AdminPassInRunCommand" https://my-repo.example.com/install.sh | bash

# 4. Adding a local file that contains secrets into the image.
# If `secrets.conf` contains sensitive data, it's now part of the image.
COPY secrets.conf /etc/app/secrets.conf

# 5. A commented-out old secret (a very common finding).
# ENV OLD_API_KEY="old-key-abcdef-commented"

# 6. Downloading a file from a URL that contains a token.
RUN wget "https://some-service.com/download?token=token-in-wget-url-ghijkl" -O /app/data.zip

# 7. A secret exposed in a LABEL. Less common, but possible.
LABEL com.example.service.auth-key="auth-key-in-label-pqrst"

# 8. False positive: A package version or hash that might look like a secret.
ENV APP_VERSION="v2.1.0-a1b2c3d4e5f6"

# A default command for the container to run.
# This will expose the password in the container's logs if not handled carefully.
CMD ["sh", "-c", "echo 'Container started. DB_PASSWORD is: $DATABASE_PASSWORD'"]

