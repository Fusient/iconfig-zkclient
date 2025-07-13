# Maven到Gradle迁移指南

## 概述
本项目已从Maven构建系统迁移到Gradle。以下是主要的变更和使用说明。

## 文件变更

### 新增文件
- `build.gradle` - 主要的Gradle构建文件
- `settings.gradle` - Gradle项目设置
- `gradle.properties` - Gradle属性配置
- `gradlew` / `gradlew.bat` - Gradle Wrapper脚本
- `gradle/wrapper/` - Gradle Wrapper相关文件

### 保留文件
- `pom.xml` - 保留作为参考，可以在确认Gradle构建正常后删除

## 主要功能对比

| 功能 | Maven命令 | Gradle命令 |
|------|-----------|------------|
| 清理 | `mvn clean` | `./gradlew clean` |
| 编译 | `mvn compile` | `./gradlew compileJava` |
| 测试 | `mvn test` | `./gradlew test` |
| 打包 | `mvn package` | `./gradlew build` |
| 安装到本地仓库 | `mvn install` | `./gradlew publishToMavenLocal` |
| 发布 | `mvn deploy` | `./gradlew publish` |

## 依赖管理

### 主要依赖
- Google Guava: 31.1-jre
- Apache Curator: 5.4.0
- SLF4J API: 1.7.36
- Logback Classic: 1.2.12

### 测试依赖
- JUnit: 4.13.2
- Mockito: 4.11.0

## 构建配置

### Java版本
- 源码兼容性: Java 8
- 目标兼容性: Java 8

### 编码
- 源码编码: UTF-8
- Javadoc编码: UTF-8

## 发布配置

### Maven Central发布
项目配置了发布到Maven Central的功能，需要在`~/.gradle/gradle.properties`中配置：

```properties
ossrhUsername=your-username
ossrhPassword=your-password
signing.keyId=your-key-id
signing.password=your-key-password
signing.secretKeyRingFile=/path/to/secring.gpg
```

### 发布命令
```bash
# 发布快照版本
./gradlew publishToSonatype

# 发布正式版本（需要先修改version去掉SNAPSHOT）
./gradlew publishToSonatype
```

## 常用命令

```bash
# 查看所有可用任务
./gradlew tasks

# 构建项目
./gradlew build

# 清理并构建
./gradlew clean build

# 生成源码jar
./gradlew sourcesJar

# 生成javadoc jar
./gradlew javadocJar

# 运行测试（当前配置为跳过）
./gradlew test

# 生成测试覆盖率报告
./gradlew jacocoTestReport
```

## 注意事项

1. **测试跳过**: 当前配置中测试被跳过（`test.enabled = false`），这与原Maven配置保持一致
2. **JAR排除**: 构建的JAR文件会排除`log4j.xml`文件
3. **签名**: 只有在发布时才会进行GPG签名
4. **Gradle版本**: 使用Gradle 7.6版本

## 验证迁移

运行以下命令验证迁移是否成功：

```bash
# 检查项目结构
./gradlew projects

# 检查依赖
./gradlew dependencies

# 构建项目
./gradlew clean build
```

如果构建成功，说明迁移完成。
