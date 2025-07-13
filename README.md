# iconfig-zkclient

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/Fusient/iconfig-zkclient)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Java Version](https://img.shields.io/badge/java-8+-orange.svg)](https://www.oracle.com/java/)
[![Gradle](https://img.shields.io/badge/gradle-7.6-green.svg)](https://gradle.org/)

## 📖 项目简介

**iconfig-zkclient** 是一个基于 ZooKeeper 的自动配置客户端，支持动态配置更新和本地配置文件读取。本项目为 [in 配置自动化服务端](https://github.com/Fusient/iconfig-zkclient) 的客户端实现，也可作为独立的本地配置文件读取工具使用。

### ✨ 主要特性

- 🔄 **动态配置更新**: 支持从 ZooKeeper 服务端实时拉取配置更新
- 📁 **本地配置读取**: 可作为本地配置文件读取工具使用
- 🔐 **安全认证**: 支持 ZooKeeper digest 认证
- 📊 **配置监听**: 支持配置变更回调通知
- ⚡ **高性能**: 基于 Apache Curator 实现，性能优异
- 🛠️ **易于集成**: 简单的 API 设计，易于集成到现有项目

## 🚀 快速开始

### 依赖引入

#### Gradle (推荐)

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

### 基本使用

```java
import com.ijson.config.ConfigFactory;
import com.ijson.config.api.IChangeableConfig;

// 获取配置实例
IChangeableConfig config = ConfigFactory.getConfig("your-app-name", (value) -> {
    System.out.println("配置已更新: " + value.getString());
});

// 读取配置值
String zkServers = config.getString("zk.servers");
boolean zkEnabled = config.getBool("zk.enable", false);
int timeout = config.getInt("timeout", 5000);
```

## 📋 使用模式

### 1. 本地配置文件读取模式

适用于配置文件已在本地，不需要动态更新的场景。此模式会自动关闭与 ZooKeeper 的连接，推荐对配置修改频率不高的项目使用。

```java
// 创建application.properties文件
// zk.enable=false
// app.name=my-application
// app.timeout=5000

IChangeableConfig config = ConfigFactory.getConfig("my-application");
String appName = config.getString("app.name");
int timeout = config.getInt("app.timeout", 3000);
```

### 2. 远程动态配置模式

支持从远程服务端获取配置更新，服务端配置修改后自动下载到本地，实现不重启服务即可更新配置的方案。此模式会与 ZooKeeper 建立心跳连接，推荐内网环境使用。

#### 配置参数

| 参数名称               | 默认值                                  | 描述                         |
| ---------------------- | --------------------------------------- | ---------------------------- |
| `zk.enable`            | `false`                                 | 是否启用 ZooKeeper 远程配置  |
| `zk.servers`           | -                                       | ZooKeeper 服务器地址         |
| `zk.auth`              | `in:ijson`                              | 认证信息 (username:password) |
| `zk.authType`          | `digest`                                | 认证类型                     |
| `zk.basePath`          | `/in/config`                            | ZooKeeper 存储路径           |
| `config.url`           | `http://config.ijson.com/in/config/api` | 配置服务 API 地址            |
| `process.profile`      | -                                       | 环境标识 (develop/test/prod) |
| `process.name`         | -                                       | 应用名称                     |
| `custom.zk.server.url` | -                                       | 自定义 ZooKeeper 服务地址    |

#### 启动参数示例

```bash
java -jar your-app.jar \
  -Dzk.enable=true \
  -Dprocess.profile=develop \
  -Dprocess.name=your-app \
  -Dzk.servers=localhost:2181
```

## 🔧 构建项目

本项目使用 Gradle 构建系统。

### 环境要求

- Java 8+
- Gradle 7.6+ (或使用项目自带的 Gradle Wrapper)

### 构建命令

```bash
# 编译项目
./gradlew compileJava

# 运行测试
./gradlew test

# 构建JAR包
./gradlew build

# 发布到本地Maven仓库
./gradlew publishToMavenLocal

# 查看所有可用任务
./gradlew tasks
```

### 生成的文件

构建成功后，在 `build/libs/` 目录下会生成：

- `iconfig-zkclient-1.0.0-SNAPSHOT.jar` - 主 JAR 包
- `iconfig-zkclient-1.0.0-SNAPSHOT-sources.jar` - 源码包
- `iconfig-zkclient-1.0.0-SNAPSHOT-javadoc.jar` - 文档包

## 📚 API 文档

### ConfigFactory

主要的工厂类，用于创建配置实例。

```java
// 获取配置实例（无监听器）
IChangeableConfig config = ConfigFactory.getConfig("app-name");

// 获取配置实例（带监听器）
IChangeableConfig config = ConfigFactory.getConfig("app-name", (newConfig) -> {
    System.out.println("配置已更新");
});
```

### IChangeableConfig

配置接口，提供各种类型的配置读取方法。

```java
// 字符串配置
String value = config.getString("key");
String value = config.getString("key", "defaultValue");

// 数值配置
int intValue = config.getInt("key", 0);
long longValue = config.getLong("key", 0L);
double doubleValue = config.getDouble("key", 0.0);

// 布尔配置
boolean boolValue = config.getBool("key", false);

// 检查配置是否存在
boolean exists = config.has("key");
```

## 🌐 配置服务 API

配置服务 API 格式：

```http
GET http://config.ijson.com/in/config/api?profile=develop&name=demo
```

返回格式：

```properties
zookeeper.servers=115.29.102.69:2181
zookeeper.authenticationType=digest
zookeeper.authentication=in:ijson
zookeeper.basePath=/in/config
```

## 📦 依赖信息

本项目使用以下主要依赖：

| 依赖            | 版本     | 用途             |
| --------------- | -------- | ---------------- |
| Google Guava    | 31.1-jre | 工具类库         |
| Apache Curator  | 5.4.0    | ZooKeeper 客户端 |
| SLF4J API       | 1.7.36   | 日志接口         |
| Logback Classic | 1.2.12   | 日志实现         |

## 🔍 示例项目

### 完整示例

```java
import com.ijson.config.ConfigFactory;
import com.ijson.config.api.IChangeableConfig;

public class ConfigExample {
    public static void main(String[] args) {
        // 创建配置实例，带变更监听
        IChangeableConfig config = ConfigFactory.getConfig("demo-app", (newConfig) -> {
            System.out.println("配置已更新，重新加载应用配置");
            // 在这里处理配置变更逻辑
        });

        // 读取各种类型的配置
        String appName = config.getString("app.name", "default-app");
        int port = config.getInt("server.port", 8080);
        boolean debugMode = config.getBool("debug.enabled", false);

        System.out.println("应用名称: " + appName);
        System.out.println("服务端口: " + port);
        System.out.println("调试模式: " + debugMode);

        // 检查配置是否存在
        if (config.has("database.url")) {
            String dbUrl = config.getString("database.url");
            System.out.println("数据库URL: " + dbUrl);
        }
    }
}
```

### 配置文件示例

创建 `application.properties` 文件：

```properties
# 应用基本配置
app.name=my-awesome-app
server.port=8080
debug.enabled=true

# ZooKeeper配置
zk.enable=true
zk.servers=localhost:2181
zk.basePath=/config/my-app

# 环境配置
process.profile=develop
process.name=my-awesome-app
```

## 🤝 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

### 开发环境设置

```bash
# 克隆仓库
git clone https://github.com/Fusient/iconfig-zkclient.git
cd iconfig-zkclient

# 构建项目
./gradlew build

# 运行测试
./gradlew test
```

## 📄 许可证

本项目采用 [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) 许可证。

## 👥 作者

- **cuiyongxu** - _项目维护者_ - [GitHub](https://github.com/icuiyongxu)

## 🔗 相关链接

- [项目主页](https://github.com/Fusient/iconfig-zkclient)
- [问题反馈](https://github.com/Fusient/iconfig-zkclient/issues)
- [配置服务端项目](https://github.com/Fusient/iconfig-zkclient)
- [Apache Curator 文档](https://curator.apache.org/)

## 📞 支持

如果您在使用过程中遇到问题，可以通过以下方式获取帮助：

- 查看 [Issues](https://github.com/Fusient/iconfig-zkclient/issues) 页面
- 发送邮件至：[cuiyongxu@gmail.com](mailto:cuiyongxu@gmail.com)
- 访问项目主页：[https://www.ijson.net](https://www.ijson.net)

---

⭐ 如果这个项目对您有帮助，请给我们一个 Star！
