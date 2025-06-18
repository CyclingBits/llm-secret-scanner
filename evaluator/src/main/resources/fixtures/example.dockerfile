# dockerfile-header-token-1a2b3c

FROM ubuntu:22.04

ENV DATABASE_PASSWORD="PasswordInDockerfileEnv!@#"

ARG BUILD_TIME_TOKEN="build-time-secret-token-xyz"
ENV API_TOKEN=${BUILD_TIME_TOKEN}

RUN apt-get update && apt-get install -y curl wget \
    && curl -u "admin:AdminPassInRunCommand" https://my-repo.example.com/install.sh | bash

# ENV OLD_API_KEY="old-key-abcdef-commented"

RUN wget "https://some-service.com/download?token=token-in-wget-url-ghijkl" -O /app/data.zip

LABEL com.example.service.auth-key="auth-key-in-label-pqrst"
