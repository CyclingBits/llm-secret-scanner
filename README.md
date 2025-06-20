# 🔍 LLM Secret Scanner

> AI-powered security scanner that detects secrets, API keys, and sensitive data in source code using local Large Language Models.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Changelog](https://img.shields.io/badge/changelog-v1.1.0-blue.svg)](CHANGELOG.md)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/cyclingbits/llm-secret-scanner)
[![Test Coverage](https://img.shields.io/badge/coverage-80%25-green.svg)](https://github.com/cyclingbits/llm-secret-scanner)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.java.net/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.10-purple.svg)](https://kotlinlang.org/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)

## ✨ Features

- 🤖 **AI-Powered Analysis** - Uses local LLM models via Docker Model Runner
- 🔒 **Privacy-First** - All analysis happens locally, no data leaves your machine
- 🎯 **Smart Detection** - Identifies API keys, passwords, certificates, database credentials, and more
- 🧠 **Adaptability** - Can detect unusual secret patterns that would escape traditional regex-based scanners
- 🚀 **Maven Integration** - Seamlessly integrates with your build pipeline
- ⚙️ **Highly Configurable** - Flexible file patterns, model selection, and timeout settings
- 🐳 **Containerized** - Automatic Docker container lifecycle management
- 🎨 **Beautiful Output** - Colorful, structured logging with emojis and clear issue reporting

## 📋 Requirements

- ☕ **Java 17+**
- 📦 **Maven 3.6+**
- 🐳 **Docker Desktop** (4.40+ Mac, 4.41+ Windows) with Model Runner enabled
  - You need to enable Docker Model Runner in Docker Desktop settings:
  - ![Docker Desktop Model Runner Settings](docker_desktop.png)
- 🌐 **Internet Connection**: Required for automatic download of selected LLM models

## 🚀 Quick Start

### 1. Installation

You can install the plugin using one of these methods:

#### Option A: Use GitHub Packages Repository
Add the GitHub Packages plugin repository to your `pom.xml`:

```xml
<pluginRepositories>
    <pluginRepository>
        <id>github</id>
        <url>https://maven.pkg.github.com/cyclingbits/llm-secret-scanner</url>
    </pluginRepository>
</pluginRepositories>
```

You'll also need to authenticate with GitHub Packages. Add to your `~/.m2/settings.xml`:
```xml
<servers>
    <server>
        <id>github</id>
        <username>YOUR_GITHUB_USERNAME</username>
        <password>YOUR_GITHUB_TOKEN</password>
    </server>
</servers>
```

#### Option B: Download from GitHub Releases (Recommended)
```bash
# Download the latest release JARs
wget https://github.com/cyclingbits/llm-secret-scanner/releases/latest/download/llm-secret-scanner-maven-plugin-1.1.0.jar
# Install to local Maven repository
mvn install:install-file -Dfile=llm-secret-scanner-maven-plugin-1.1.0.jar -DgroupId=net.cyclingbits -DartifactId=llm-secret-scanner-maven-plugin -Dversion=1.1.0 -Dpackaging=jar
```

#### Option C: Build from Source
```bash
git clone https://github.com/cyclingbits/llm-secret-scanner.git
cd llm-secret-scanner
mvn clean install
```

### 2. Add to Your Project

Add the plugin to your `pom.xml` (minimal configuration):

```xml
<plugin>
    <groupId>net.cyclingbits</groupId>
    <artifactId>llm-secret-scanner-maven-plugin</artifactId>
    <version>1.1.0</version>
</plugin>
```

### 3. Run the Scanner

```bash
mvn llm-secret-scanner:scan
```

## 🧠 Supported AI Models

The scanner supports various LLM models via [Docker Model Runner](https://hub.docker.com/u/ai). Below are performance benchmarks from our evaluation tests:

### 🎯 **Recommended Models**

**`ai/phi4:latest` ⭐ (Default Choice)**  
With a 74.3% detection rate and 100% scan success, this **15B parameter** model offers the best balance of accuracy and performance. At **8.43 GB**, it provides excellent results in just 3m 50s analysis time, making it ideal for most use cases.

**`ai/llama3.2:latest` 🚀 (Fast & Lightweight)**  
Perfect for quick scans and resource-constrained environments. This **3B parameter** model delivers 70.4% detection rate with 100% reliability in only 1m 20s. At just **1.87 GB**, it's the smallest model that maintains high accuracy and speed.

### 📊 **All Available Models**

| Model | Detection Rate | Scan Success | Analysis Time | Parameters | Context Window | Size | Best For                                      |
|-------|----------------|--------------|---------------|------------|----------------|------|-----------------------------------------------|
| `ai/llama3.3:latest` | **82.7%** | 100% | 17m 26s | 70B | 131K tokens | 39.59 GB | Highest accuracy                              |
| `ai/phi4:latest` ⭐ | **74.3%** | 100% | 3m 50s | 15B | 16K tokens | 8.43 GB | **Default choice**                            |
| `ai/llama3.2:latest` | **70.4%** | 100% | 1m 20s | 3B | 131K tokens | 1.87 GB | Fast & lightweight                            |
| `ai/deepcoder-preview:latest` | **69.3%** | 100% | 11m 15s | 14B | 131K tokens | 8.37 GB | -                                             |
| `ai/mistral-nemo:latest` | **65.7%** | 100% | 3m 5s | 12B | 131K tokens | 6.96 GB | -                                             |
| `ai/llama3.1:latest` | **64.8%** | 100% | 2m 1s | 8B | 131K tokens | 4.58 GB | -                                             |
| `ai/qwq:latest` | **64.3%** | 52.9% | 88m 22s | 32B | 41K tokens | 18.48 GB | JSON generation errors and model API timeouts |
| `ai/qwen3:latest` | **64.2%** | 76.5% | 21m 51s | 8B | 41K tokens | 4.68 GB | JSON generation errors                        |
| `ai/qwen2.5:latest` | **60.5%** | 94.1% | 1m 49s | 7B | 33K tokens | 4.36 GB | JSON generation errors                        |
| `ai/gemma3:latest` | **56.4%** | 100% | 1m 43s | 4B | 131K tokens | 2.31 GB | -                                             |
| `ai/gemma3-qat:latest` | **55.3%** | 100% | 1m 41s | 3.88B | 131K tokens | 2.93 GB | -                                             |
| `ai/deepseek-r1-distill-llama:latest` | **54.1%** | 100% | 4m 51s | 8B | 131K tokens | 4.58 GB | -                                             |
| `ai/mistral:latest` | **52.5%** | 100% | 2m 10s | 7B | 33K tokens | 4.07 GB | -                                             |
| `ai/smollm2:latest` | **0.0%** | 0% | 9m 44s | 360M | 8K tokens | 256.35 MB | JSON generation errors                        |

> 📊 **Performance data** based on analysis of test fixtures with known vulnerabilities. All models available from [Docker Hub AI](https://hub.docker.com/u/ai).
> 
> **Detection Rate** indicates the percentage of known security issues correctly identified by the model (with line number accuracy verification ±1).  
> **Scan Success** indicates the percentage of files that were successfully analyzed without errors (e.g., timeouts, JSON parsing failures).

## 📖 Usage Examples

### Basic Scan
```bash
mvn llm-secret-scanner:scan
```

### Custom Model
```bash
mvn llm-secret-scanner:scan -Dscan.modelName=ai/llama3.2:latest
```

### Fail Build on Issues
```bash
mvn llm-secret-scanner:scan -Dscan.failOnError=true
```

### Custom Source Directory
```bash
mvn llm-secret-scanner:scan -Dscan.sourceDirectory=./custom-src
```

## ⚙️ Advanced Configuration

For more control, you can customize the plugin configuration:

```xml
<plugin>
    <groupId>net.cyclingbits</groupId>
    <artifactId>llm-secret-scanner-maven-plugin</artifactId>
    <version>1.1.0</version>
    <configuration>
        <sourceDirectory>${project.basedir}/src</sourceDirectory>
        <includes>**/*.java,**/*.kt,**/*.properties,**/*.yml,**/*.env</includes>
        <excludes>**/target/**,**/test/**</excludes>
        <modelName>ai/phi4:latest</modelName>
        <systemPrompt>Find API keys and secrets in the provided code</systemPrompt>
        <failOnError>false</failOnError>
        <fileAnalysisTimeout>60</fileAnalysisTimeout>
        <maxTokens>10000</maxTokens>
        <temperature>0.0</temperature>
        <maxFileSizeBytes>102400</maxFileSizeBytes>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>scan</goal>
            </goals>
            <phase>verify</phase>
        </execution>
    </executions>
</plugin>
```

### Configuration Parameters

| Parameter | Default | Description |
|-----------|---------|-------------|
| `sourceDirectory` | `${project.basedir}` | Directory to scan |
| `includes` | `**/*.java,**/*.kt,...` | File patterns to include |
| `excludes` | `**/target/**` | File patterns to exclude |
| `modelName` | `ai/phi4:latest` | LLM model to use |
| `systemPrompt` | _null_ | Custom system prompt (optional) |
| `failOnError` | `false` | Fail build when issues found |
| `fileAnalysisTimeout` | `60` | File analysis timeout (seconds) |
| `maxTokens` | `10000` | Maximum tokens per request |
| `temperature` | `0.0` | LLM temperature (0.0-2.0) |
| `maxFileSizeBytes` | `102400` | Maximum file size (100KB) |

## 📊 Sample Output

```
🔍 Starting LLM Secret Scanner with configuration:
       📄 Source directory: /fixtures
       🐳 Model: ai/llama3.2:latest
       ⏱️ File analysis timeout: 60s
       ✅ Include patterns: **/*.java
       ❌ Exclude patterns: **/target/**

📄 Found 1 files matching patterns
🐳 Starting Docker container...
✅ Docker container started successfully
🔍 Starting analysis of 1 files

[1/1] Analyzing file example.java...
       Found 13 issues (analyzed in 14,9s)
       🚨 #1 | Line 12 | Password | Hardcoded password
       🚨 #2 | Line 14 | API Key | Hardcoded API key
       🚨 #3 | Line 16 | Base64 Encoded Secret | Base64 encoded secret
       🚨 #4 | Line 20 | Private Key | Hardcoded private key
       🚨 #5 | Line 31 | Database Connection String | Hardcoded database connection string
       🚨 #6 | Line 33 | Password | Hardcoded password
       🚨 #7 | Line 52 | Token | Hardcoded token
       🚨 #8 | Line 54 | Token | Hardcoded token
       🚨 #9 | Line 62 | Password | Hardcoded password
       🚨 #10 | Line 64 | Password | Hardcoded password
       🚨 #11 | Line 67 | Base64 Encoded Secret | Base64 encoded secret
       🚨 #12 | Line 68 | Base64 Encoded Secret | Base64 encoded secret
       🚨 #13 | Line 70 | Static Secret | Hardcoded static secret

✅ Scan completed successfully. Analyzed 1 of 1 files, found 13 total issues in 14,9s
```

## 🏗️ Architecture

This project consists of three modules:

- **🔧 core** - Core scanning engine and Docker container management
- **📦 maven-plugin** - Maven plugin integration for build pipelines  
- **📈 evaluator** - Performance benchmarking tools for testing LLM models

## 🛠️ Development

### Build from Source
```bash
git clone https://github.com/cyclingbits/llm-secret-scanner.git
cd llm-secret-scanner
mvn clean compile
```

### Run Tests
```bash
mvn test
```

### Run Evaluator

Quick evaluation (Java files only, single model):
```bash
cd evaluator
mvn exec:java -Dexec.mainClass="net.cyclingbits.llmsecretscanner.evaluator.QuickEvaluator"
```

Full evaluation (all file types, all models):
```bash
cd evaluator
mvn exec:java -Dexec.mainClass="net.cyclingbits.llmsecretscanner.evaluator.FullEvaluator"
```

## 🚨 What It Detects

The LLM scanner is trained to identify various types of sensitive information:

- 🔑 **API Keys** - AWS, Google Cloud, Azure, GitHub, etc.
- 🔐 **Passwords** - Hardcoded passwords in source code
- 🗄️ **Database Credentials** - Connection strings with embedded credentials
- 🔏 **Private Keys** - RSA, ECDSA, SSH private keys, certificates
- 🎟️ **Tokens** - JWT tokens, session tokens, auth tokens
- 🔒 **Secrets** - Generic secrets and high-entropy strings
- 📧 **Email Credentials** - SMTP passwords and configurations

## 🛡️ Security & Privacy

- **Local Processing**: All analysis happens on your machine - no data is sent to external services
- **Docker Isolation**: LLM models run in isolated Docker containers
- **Zero Data Persistence**: Scanner doesn't store or cache your source code, ensuring maximum security and privacy
- **Memory Safe**: Containers are automatically cleaned up after scanning

## ⚠️ Limitations

- Requires Docker Desktop with Model Runner feature
- Analysis speed depends on model size and file count
- False positives possible (manually review results)
- Only supports files encodable in UTF-8



## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📝 Changelog

All notable changes to this project are documented in the [CHANGELOG.md](CHANGELOG.md) file.

## 📞 Contact

For questions, feedback, or support, please reach out to us at:
📧 **CyclingBitsAI@gmail.com**

## 🙏 Acknowledgments

- [Docker Model Runner](https://docs.docker.com/desktop/model-runner/) for local AI model execution
- [TestContainers](https://testcontainers.org/) for Docker integration testing
- [Kotlin](https://kotlinlang.org/) for the beautiful, concise codebase
- [Maven](https://maven.apache.org/) for build management

---

Made with ❤️ by [Cycling Bits](https://github.com/CyclingBits)
