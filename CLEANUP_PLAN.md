# Maven到Gradle迁移清理计划

## 当前状态
✅ Gradle构建文件已创建并配置完成
✅ 项目结构验证通过
✅ 依赖配置正确
✅ 发布配置已设置

## 需要清理的Maven文件

### 主要文件
- `pom.xml` - Maven主构建文件
- `target/` - Maven构建输出目录

### 可选清理文件
- `.gitlab-ci.yml` - 包含Maven命令的CI配置（需要更新为Gradle命令）

## 清理步骤

### 步骤1: 验证Gradle构建（推荐）
在删除Maven文件之前，建议先验证Gradle构建：

```bash
# 1. 设置执行权限
chmod +x gradlew

# 2. 清理并构建
./gradlew clean build

# 3. 检查生成的JAR文件
ls -la build/libs/

# 4. 运行测试（如果启用）
./gradlew test

# 5. 验证发布配置
./gradlew tasks --group publishing
```

### 步骤2: 备份Maven配置（可选）
如果需要保留Maven配置作为参考：

```bash
# 创建备份目录
mkdir maven-backup

# 备份Maven文件
cp pom.xml maven-backup/
cp -r target maven-backup/ 2>/dev/null || true
```

### 步骤3: 删除Maven文件
确认Gradle构建正常后，可以删除以下文件：

```bash
# 删除Maven构建文件
rm pom.xml

# 删除Maven构建输出目录
rm -rf target/
```

### 步骤4: 更新CI/CD配置
如果项目使用CI/CD，需要更新构建脚本：

#### GitLab CI (.gitlab-ci.yml)
将Maven命令替换为Gradle命令：

```yaml
# 原来的Maven命令
# mvn test sonar:sonar -Dmaven.test.failure.ignore=true

# 新的Gradle命令
build:
  script:
    - pwd
    - ./gradlew clean build test jacocoTestReport sonarqube -Dsonar.core.codeCoveragePlugin=jacoco
```

#### GitHub Actions
```yaml
- name: Build with Gradle
  run: ./gradlew clean build

- name: Test with Gradle
  run: ./gradlew test

- name: Publish
  run: ./gradlew publish
```

### 步骤5: 更新文档
更新项目文档中的构建说明：

1. **README.md** - 更新构建命令
2. **开发文档** - 更新开发环境设置
3. **部署文档** - 更新部署流程

## 迁移验证清单

### 构建验证
- [ ] `./gradlew clean` 执行成功
- [ ] `./gradlew compileJava` 编译成功
- [ ] `./gradlew build` 构建成功
- [ ] 生成的JAR文件正确
- [ ] 源码JAR生成正确
- [ ] Javadoc JAR生成正确

### 功能验证
- [ ] 所有依赖正确解析
- [ ] 项目类可以正常加载
- [ ] 测试可以运行（如果启用）
- [ ] 发布任务可以执行

### 配置验证
- [ ] Java版本配置正确
- [ ] 编码设置正确
- [ ] 排除配置生效
- [ ] 发布配置完整

## 回滚计划

如果Gradle构建出现问题，可以快速回滚到Maven：

### 恢复Maven文件
```bash
# 如果有备份
cp maven-backup/pom.xml .

# 或者从Git恢复
git checkout HEAD~1 -- pom.xml
```

### 临时使用Maven
```bash
# 使用Maven构建
mvn clean compile
mvn clean package
```

## 完成后的项目结构

清理完成后，项目应该只包含Gradle相关文件：

```
iconfig-zkclient/
├── build.gradle              # Gradle主构建文件
├── settings.gradle           # Gradle项目设置
├── gradle.properties         # Gradle属性配置
├── gradlew                   # Gradle Wrapper (Unix)
├── gradlew.bat              # Gradle Wrapper (Windows)
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── src/
│   ├── main/java/           # 源代码
│   └── test/java/           # 测试代码
├── build/                   # Gradle构建输出
├── README.md
├── GRADLE_MIGRATION.md      # 迁移指南
├── BUILD_VERIFICATION.md    # 构建验证报告
├── PUBLISH_SETUP.md         # 发布配置指南
└── CLEANUP_PLAN.md          # 本文件
```

## 注意事项

1. **确保构建成功** - 在删除Maven文件之前，务必确认Gradle构建完全正常
2. **保留备份** - 建议保留Maven配置的备份，直到Gradle构建在生产环境中稳定运行
3. **更新团队** - 通知团队成员构建系统的变更
4. **更新文档** - 及时更新所有相关文档和脚本

完成这些步骤后，项目就完全迁移到Gradle构建系统了！
