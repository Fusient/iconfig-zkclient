# 发布配置设置指南

## 概述
本项目已配置为可以发布到Maven Central。要启用发布功能，需要在用户级别的gradle.properties文件中配置相关凭据。

## 配置文件位置
用户级别的gradle.properties文件应该位于：
- **macOS/Linux**: `~/.gradle/gradle.properties`
- **Windows**: `%USERPROFILE%\.gradle\gradle.properties`

## 必需的配置项

### 1. OSSRH (Sonatype) 凭据
```properties
# Maven Central发布凭据
ossrhUsername=your-sonatype-username
ossrhPassword=your-sonatype-password
```

### 2. GPG签名配置
```properties
# GPG签名配置
signing.keyId=your-gpg-key-id
signing.password=your-gpg-key-password
signing.secretKeyRingFile=/path/to/your/secring.gpg
```

## 详细配置步骤

### 步骤1: 创建Sonatype账户
1. 访问 https://issues.sonatype.org/
2. 创建账户并申请发布权限
3. 创建JIRA票据申请com.ijson.ai组ID权限

### 步骤2: 生成GPG密钥
```bash
# 生成GPG密钥对
gpg --gen-key

# 列出密钥
gpg --list-secret-keys --keyid-format LONG

# 导出公钥到密钥服务器
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID

# 导出私钥环（用于签名）
gpg --export-secret-keys YOUR_KEY_ID > ~/.gnupg/secring.gpg
```

### 步骤3: 配置gradle.properties
创建或编辑 `~/.gradle/gradle.properties` 文件：

```properties
# Maven Central发布凭据
ossrhUsername=your-sonatype-username
ossrhPassword=your-sonatype-password

# GPG签名配置
signing.keyId=YOUR_GPG_KEY_ID
signing.password=your-gpg-key-password
signing.secretKeyRingFile=/Users/yourusername/.gnupg/secring.gpg

# 可选：性能优化
org.gradle.jvmargs=-Xmx2048m
org.gradle.parallel=true
org.gradle.caching=true
```

## 发布命令

### 发布快照版本
```bash
# 确保版本号包含SNAPSHOT
./gradlew publishToSonatype
```

### 发布正式版本
```bash
# 1. 修改build.gradle中的version，去掉SNAPSHOT
# 2. 发布
./gradlew publishToSonatype

# 3. 登录Sonatype Nexus Repository Manager
# 4. 在Staging Repositories中找到你的发布
# 5. Close并Release
```

### 本地发布（测试用）
```bash
# 发布到本地Maven仓库
./gradlew publishToMavenLocal
```

## 验证发布配置

### 检查发布任务
```bash
./gradlew tasks --group publishing
```

### 验证POM生成
```bash
./gradlew generatePomFileForMavenPublication
cat build/publications/maven/pom-default.xml
```

### 测试签名
```bash
./gradlew signMavenPublication
```

## 安全注意事项

1. **不要将凭据提交到版本控制**
   - gradle.properties文件应该在用户目录下，不在项目目录
   - 确保.gitignore包含gradle.properties

2. **保护GPG私钥**
   - 定期备份GPG密钥
   - 使用强密码保护私钥

3. **使用环境变量（可选）**
   ```bash
   export OSSRH_USERNAME=your-username
   export OSSRH_PASSWORD=your-password
   export SIGNING_KEY_ID=your-key-id
   export SIGNING_PASSWORD=your-key-password
   ```

## 故障排除

### 常见问题

1. **GPG签名失败**
   - 检查GPG密钥是否正确
   - 确认密钥密码正确
   - 验证secring.gpg文件路径

2. **认证失败**
   - 验证OSSRH用户名和密码
   - 确认有发布权限

3. **POM验证失败**
   - 检查必需的POM元素是否完整
   - 验证许可证和开发者信息

### 调试命令
```bash
# 详细日志
./gradlew publishToSonatype --info --stacktrace

# 检查配置
./gradlew properties | grep -E "(signing|ossrh)"
```

## 示例配置文件

完整的 `~/.gradle/gradle.properties` 示例：

```properties
# 性能配置
org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m
org.gradle.parallel=true
org.gradle.caching=true

# Maven Central发布
ossrhUsername=cuiyongxu
ossrhPassword=your-secure-password

# GPG签名
signing.keyId=ABCD1234
signing.password=your-gpg-password
signing.secretKeyRingFile=/Users/cuiyongxu/.gnupg/secring.gpg

# 可选：代理配置（如果需要）
# systemProp.http.proxyHost=proxy.company.com
# systemProp.http.proxyPort=8080
# systemProp.https.proxyHost=proxy.company.com
# systemProp.https.proxyPort=8080
```

配置完成后，就可以使用Gradle发布到Maven Central了！
