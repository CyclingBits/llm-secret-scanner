# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1] - 2025-06-18

### ✨ Improved

- **Enhanced accuracy validation** - Evaluator now validates detected issues against reference results with precise line number matching

## [1.0.0] - 2025-06-17

### 🎉 Initial Release

This is the first stable release of LLM Secret Scanner - an AI-powered security scanner that detects secrets, API keys, and sensitive data in source code using local Large Language Models.

### ✨ Features

- **🤖 AI-Powered Analysis** - Uses local LLM models via Docker Model Runner for intelligent secret detection
- **🔒 Privacy-First** - All analysis happens locally, no data leaves your machine
- **🎯 Smart Detection** - Identifies various types of sensitive information:
  - API Keys (AWS, Google Cloud, Azure, GitHub, etc.)
  - Passwords and hardcoded credentials
  - Database connection strings
  - Private keys (RSA, ECDSA, SSH) and certificates
  - JWT tokens, session tokens, auth tokens
  - Generic secrets and high-entropy strings
  - Email credentials and SMTP configurations

- **🚀 Maven Integration** - Seamless integration with Maven build pipelines
  - Maven plugin with `llm-secret-scanner:scan` goal
  - Configurable execution phases (default: verify)
  - Command-line parameter overrides

- **⚙️ Highly Configurable** - Flexible configuration options:
  - File pattern matching (include/exclude filters)
  - Multiple LLM model support
  - Timeout and performance settings
  - Custom system prompts
  - Fail-on-error build integration

- **🐳 Containerized** - Automatic Docker container lifecycle management
  - Docker Model Runner integration
  - Automatic container startup and cleanup
  - Support for multiple AI models

- **🎨 Beautiful Output** - User-friendly reporting:
  - Colorful, structured logging with emojis
  - Clear issue reporting with file paths and line numbers
  - Progress indicators and timing information

### 🏗️ Architecture

- **Multi-module Maven project** with three modules:
  - **core** - Core scanning engine and Docker container management
  - **maven-plugin** - Maven plugin integration for build pipelines
  - **evaluator** - Performance benchmarking tools for testing LLM models

### 🧠 Supported AI Models

- **ai/phi4:latest** ⭐ (default) - 76.7% detection rate, 15B parameters
- **ai/deepcoder-preview:latest** - 82.4% detection rate, 14B parameters
- **ai/mistral-nemo:latest** - 80.0% detection rate, 12B parameters
- **ai/deepseek-r1-distill-llama:latest** - 78.6% detection rate, 8B parameters
- **ai/llama3.1:latest** - 62.9% detection rate, 8B parameters
- **ai/gemma3:latest** - 61.9% detection rate, 4B parameters
- **ai/qwen2.5:latest** - 61.4% detection rate, 7B parameters

### 🛠️ Technical Details

- **Language**: Kotlin 2.1.10 targeting Java 17
- **Build Tool**: Maven 3.6+ with multi-module structure
- **HTTP Client**: Fuel 2.3.1 for OpenAI-compatible API communication
- **JSON Processing**: Jackson 2.19.0 with Kotlin module support
- **Testing**: JUnit 5 with MockK for Kotlin mocking
- **Docker Integration**: TestContainers for container lifecycle management
- **Logging**: SLF4J with SimpleLogger, custom configuration for clean output

### 📋 Requirements

- Java 17+
- Maven 3.6+
- Docker Desktop (4.40+ Mac, 4.41+ Windows) with Model Runner enabled
- Internet connection for initial model downloads

### 🧪 Testing

- **Comprehensive test suite** with 60+ tests
- **Unit tests** using MockK for isolated component testing
- **Integration tests** using real Docker containers for end-to-end validation
- **Evaluation framework** for model performance benchmarking

### 📖 Documentation

- Complete README with usage examples and configuration guide
- Inline code documentation
- Performance benchmarks and model comparisons
- Architecture overview and development setup

---

For detailed usage instructions, see the [README.md](README.md) file.