# iconfig-zkclient

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/Fusient/iconfig-zkclient)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Java Version](https://img.shields.io/badge/java-8+-orange.svg)](https://www.oracle.com/java/)
[![Gradle](https://img.shields.io/badge/gradle-7.6-green.svg)](https://gradle.org/)
[![Maven Central](https://img.shields.io/badge/maven--central-1.0.0--SNAPSHOT-blue.svg)](https://search.maven.org/artifact/com.ijson.ai/iconfig-zkclient)

English | [中文](README.md)

## 📖 Project Overview

**iconfig-zkclient** is a ZooKeeper-based automatic configuration client that supports dynamic configuration updates and local configuration file reading. This project serves as the client implementation for the [in configuration automation server](https://github.com/Fusient/iconfig-zkclient) and can also be used as a standalone local configuration file reading tool.

### 🌟 Project Highlights

- 🚀 **Zero-dependency Startup**: Ready to use out of the box without complex configuration
- 🔄 **Hot Reload Support**: Configuration changes take effect in real-time without application restart
- 🛡️ **Production-grade Stability**: Validated in large-scale production environments
- 📦 **Lightweight Design**: Minimal resource footprint with high performance
- 🔧 **Flexible Configuration**: Support for multiple configuration sources and formats

### ✨ Key Features

- 🔄 **Dynamic Configuration Updates**: Real-time configuration pulling from ZooKeeper servers
- 📁 **Local Configuration Reading**: Can be used as a local configuration file reading tool
- 🔐 **Security Authentication**: Support for ZooKeeper digest authentication
- 📊 **Configuration Monitoring**: Support for configuration change callback notifications
- ⚡ **High Performance**: Built on Apache Curator for excellent performance
- 🛠️ **Easy Integration**: Simple API design for easy integration into existing projects
- 🔒 **Thread Safety**: Fully thread-safe implementation
- 📈 **Monitoring Friendly**: Built-in performance monitoring and health checks
- 🌐 **Multi-environment Support**: Support for development, testing, production environments

### 🏗️ Architecture Features

- **Layered Design**: Clear layered architecture for easy extension and maintenance
- **Plugin-based**: Support for custom configuration sources and parsers
- **Fault Tolerance**: Built-in retry and fallback strategies
- **Resource Management**: Automatic resource cleanup to prevent memory leaks

## 🚀 Quick Start

### Dependency Integration

#### Gradle (Recommended)

```gradle
dependencies {
    implementation 'com.ijson.ai:iconfig-zkclient:1.0.0-SNAPSHOT'
}
```

#### Maven

```xml
<dependency>
    <groupId>com.ijson.ai</groupId>
    <artifactId>iconfig-zkclient</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Basic Usage

```java
import com.ijson.config.ConfigFactory;
import com.ijson.config.api.IChangeableConfig;

// Get configuration instance
IChangeableConfig config = ConfigFactory.getConfig("your-app-name", (value) -> {
    System.out.println("Configuration updated: " + value.getString());
});

// Read configuration values
String zkServers = config.getString("zk.servers");
boolean zkEnabled = config.getBool("zk.enable", false);
int timeout = config.getInt("timeout", 5000);
```

## 📋 Usage Modes

### 1. Local Configuration File Reading Mode

Suitable for scenarios where configuration files are already local and don't need dynamic updates. This mode automatically disables ZooKeeper connections, recommended for projects with low configuration change frequency.

```java
// Create application.properties file
// zk.enable=false
// app.name=my-application
// app.timeout=5000

IChangeableConfig config = ConfigFactory.getConfig("my-application");
String appName = config.getString("app.name");
int timeout = config.getInt("app.timeout", 3000);
```

### 2. Remote Dynamic Configuration Mode

Supports configuration updates from remote servers. When server configuration changes, it automatically downloads to local, achieving configuration updates without service restart. This mode establishes heartbeat connections with ZooKeeper, recommended for internal network use.

#### Configuration Parameters

| Parameter              | Default Value                           | Description                                |
| ---------------------- | --------------------------------------- | ------------------------------------------ |
| `zk.enable`            | `false`                                 | Enable ZooKeeper remote configuration      |
| `zk.servers`           | -                                       | ZooKeeper server addresses                 |
| `zk.auth`              | `in:ijson`                              | Authentication info (username:password)    |
| `zk.authType`          | `digest`                                | Authentication type                        |
| `zk.basePath`          | `/in/config`                            | ZooKeeper storage path                     |
| `config.url`           | `http://config.ijson.com/in/config/api` | Configuration service API address          |
| `process.profile`      | -                                       | Environment identifier (develop/test/prod) |
| `process.name`         | -                                       | Application name                           |
| `custom.zk.server.url` | -                                       | Custom ZooKeeper server address            |

#### Startup Parameters Example

```bash
java -jar your-app.jar \
  -Dzk.enable=true \
  -Dprocess.profile=develop \
  -Dprocess.name=your-app \
  -Dzk.servers=localhost:2181
```

## 🔧 Building the Project

This project uses the Gradle build system.

### Environment Requirements

- Java 8+
- Gradle 7.6+ (or use the included Gradle Wrapper)

### Build Commands

```bash
# Compile project
./gradlew compileJava

# Run tests
./gradlew test

# Build JAR packages
./gradlew build

# Publish to local Maven repository
./gradlew publishToMavenLocal

# View all available tasks
./gradlew tasks
```

### Generated Files

After successful build, the following files are generated in `build/libs/`:

- `iconfig-zkclient-1.0.0-SNAPSHOT.jar` - Main JAR package
- `iconfig-zkclient-1.0.0-SNAPSHOT-sources.jar` - Source package
- `iconfig-zkclient-1.0.0-SNAPSHOT-javadoc.jar` - Documentation package

## 📚 API Documentation

### ConfigFactory

Main factory class for creating configuration instances.

```java
// Get configuration instance (without listener)
IChangeableConfig config = ConfigFactory.getConfig("app-name");

// Get configuration instance (with listener)
IChangeableConfig config = ConfigFactory.getConfig("app-name", (newConfig) -> {
    System.out.println("Configuration updated");
});
```

### IChangeableConfig

Configuration interface providing various type configuration reading methods.

```java
// String configuration
String value = config.getString("key");
String value = config.getString("key", "defaultValue");

// Numeric configuration
int intValue = config.getInt("key", 0);
long longValue = config.getLong("key", 0L);
double doubleValue = config.getDouble("key", 0.0);

// Boolean configuration
boolean boolValue = config.getBool("key", false);

// Check if configuration exists
boolean exists = config.has("key");
```

## 🌐 Configuration Service API

Configuration service API format:

```http
GET http://config.ijson.com/in/config/api?profile=develop&name=demo
```

Response format:

```properties
zookeeper.servers=115.29.102.69:2181
zookeeper.authenticationType=digest
zookeeper.authentication=in:ijson
zookeeper.basePath=/in/config
```

## 📦 Dependency Information

This project uses the following main dependencies:

| Dependency      | Version  | Purpose                |
| --------------- | -------- | ---------------------- |
| Google Guava    | 31.1-jre | Utility library        |
| Apache Curator  | 5.4.0    | ZooKeeper client       |
| SLF4J API       | 1.7.36   | Logging interface      |
| Logback Classic | 1.2.12   | Logging implementation |

## 🔍 Example Project

### Complete Example

```java
import com.ijson.config.ConfigFactory;
import com.ijson.config.api.IChangeableConfig;

public class ConfigExample {
    public static void main(String[] args) {
        // Create configuration instance with change listener
        IChangeableConfig config = ConfigFactory.getConfig("demo-app", (newConfig) -> {
            System.out.println("Configuration updated, reloading application config");
            // Handle configuration change logic here
        });

        // Read various types of configuration
        String appName = config.getString("app.name", "default-app");
        int port = config.getInt("server.port", 8080);
        boolean debugMode = config.getBool("debug.enabled", false);

        System.out.println("Application name: " + appName);
        System.out.println("Server port: " + port);
        System.out.println("Debug mode: " + debugMode);

        // Check if configuration exists
        if (config.has("database.url")) {
            String dbUrl = config.getString("database.url");
            System.out.println("Database URL: " + dbUrl);
        }
    }
}
```

### Configuration File Example

Create `application.properties` file:

```properties
# Application basic configuration
app.name=my-awesome-app
server.port=8080
debug.enabled=true

# ZooKeeper configuration
zk.enable=true
zk.servers=localhost:2181
zk.basePath=/config/my-app

# Environment configuration
process.profile=develop
process.name=my-awesome-app
```

## 🔧 Troubleshooting

### Common Issues

#### 1. ZooKeeper Connection Failure

```bash
# Check ZooKeeper service status
zkServer.sh status

# Check network connection
telnet localhost 2181
```

#### 2. Configuration Not Taking Effect

```java
// Check if configuration path is correct
if (config.has("your.config.key")) {
    String value = config.getString("your.config.key");
    System.out.println("Configuration value: " + value);
} else {
    System.out.println("Configuration does not exist");
}
```

#### 3. Memory Leak Issues

Ensure proper resource cleanup when application shuts down:

```java
// Call when application shuts down
ConfigHelper.shutdown();
ZookeeperHelper.closeCurator();
```

### Performance Tuning

#### 1. Thread Pool Configuration

```java
// Custom thread pool size
System.setProperty("config.thread.pool.size", "4");
```

#### 2. Connection Pool Configuration

```java
// ZooKeeper connection configuration
System.setProperty("zk.connection.timeout", "5000");
System.setProperty("zk.session.timeout", "30000");
```

## 📊 Performance Metrics

| Metric                | Value   | Description             |
| --------------------- | ------- | ----------------------- |
| Config Read Latency   | < 1ms   | Local cache read        |
| Config Update Latency | < 100ms | ZooKeeper push          |
| Memory Usage          | < 10MB  | Base runtime            |
| Concurrent Support    | 1000+   | Concurrent config reads |

## 🔄 Version History

### v1.0.0-SNAPSHOT (Current Version)

- ✨ Support for dynamic configuration updates
- ✨ Support for local configuration file reading
- ✨ Support for ZooKeeper digest authentication
- ✨ Thread pool optimization and resource management
- ✨ Comprehensive error handling and logging

### Future Version Plans

- 🔮 Support for encrypted configuration storage
- 🔮 Support for configuration version management
- 🔮 Support for configuration audit logs
- 🔮 Support for Spring Boot auto-configuration

---

⭐ If this project helps you, please give us a Star!

## 🤝 Contributing

Welcome to contribute code! Please follow these steps:

1. Fork this repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Create Pull Request

### Development Environment Setup

```bash
# Clone repository
git clone https://github.com/Fusient/iconfig-zkclient.git
cd iconfig-zkclient

# Build project
./gradlew build

# Run tests
./gradlew test
```

## 📄 License

This project is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

## 👥 Authors

- **cuiyongxu** - _Project Maintainer_ - [GitHub](https://github.com/icuiyongxu)

## 🔗 Related Links

- [Project Homepage](https://github.com/Fusient/iconfig-zkclient)
- [Issue Tracker](https://github.com/Fusient/iconfig-zkclient/issues)
- [Configuration Server Project](https://github.com/Fusient/iconfig-zkclient)
- [Apache Curator Documentation](https://curator.apache.org/)

## 📞 Support

If you encounter problems during use, you can get help through the following ways:

- Check the [Issues](https://github.com/Fusient/iconfig-zkclient/issues) page
- Send email to: [cuiyongxu@gmail.com](mailto:cuiyongxu@gmail.com)
- Visit project homepage: [https://www.ijson.net](https://www.ijson.net)

---

⭐ If this project helps you, please give us a Star!
