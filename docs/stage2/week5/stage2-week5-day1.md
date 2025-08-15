<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Stage 2 Week 5 Day 1 - 应用骨架 + Docker 镜像 + 推送 ECR（最小可运行）](#stage-2-week-5-day-1---%E5%BA%94%E7%94%A8%E9%AA%A8%E6%9E%B6--docker-%E9%95%9C%E5%83%8F--%E6%8E%A8%E9%80%81-ecr%E6%9C%80%E5%B0%8F%E5%8F%AF%E8%BF%90%E8%A1%8C)
  - [前置检查 & 环境就绪](#%E5%89%8D%E7%BD%AE%E6%A3%80%E6%9F%A5--%E7%8E%AF%E5%A2%83%E5%B0%B1%E7%BB%AA)
    - [本机工具自检](#%E6%9C%AC%E6%9C%BA%E5%B7%A5%E5%85%B7%E8%87%AA%E6%A3%80)
    - [设置本周通用变量](#%E8%AE%BE%E7%BD%AE%E6%9C%AC%E5%91%A8%E9%80%9A%E7%94%A8%E5%8F%98%E9%87%8F)
    - [校验 AWS 身份 & EKS 连通](#%E6%A0%A1%E9%AA%8C-aws-%E8%BA%AB%E4%BB%BD--eks-%E8%BF%9E%E9%80%9A)
    - [预检 ECR 登录（为稍后推送做准备）](#%E9%A2%84%E6%A3%80-ecr-%E7%99%BB%E5%BD%95%E4%B8%BA%E7%A8%8D%E5%90%8E%E6%8E%A8%E9%80%81%E5%81%9A%E5%87%86%E5%A4%87)
  - [创建最小 Spring Boot 服务并本地运行](#%E5%88%9B%E5%BB%BA%E6%9C%80%E5%B0%8F-spring-boot-%E6%9C%8D%E5%8A%A1%E5%B9%B6%E6%9C%AC%E5%9C%B0%E8%BF%90%E8%A1%8C)
    - [创建项目骨架](#%E5%88%9B%E5%BB%BA%E9%A1%B9%E7%9B%AE%E9%AA%A8%E6%9E%B6)
    - [编写应用主类](#%E7%BC%96%E5%86%99%E5%BA%94%E7%94%A8%E4%B8%BB%E7%B1%BB)
    - [编写最小控制器](#%E7%BC%96%E5%86%99%E6%9C%80%E5%B0%8F%E6%8E%A7%E5%88%B6%E5%99%A8)
    - [打开 Actuator 健康检查](#%E6%89%93%E5%BC%80-actuator-%E5%81%A5%E5%BA%B7%E6%A3%80%E6%9F%A5)
    - [本地运行](#%E6%9C%AC%E5%9C%B0%E8%BF%90%E8%A1%8C)
      - [方式一：开发期推荐](#%E6%96%B9%E5%BC%8F%E4%B8%80%E5%BC%80%E5%8F%91%E6%9C%9F%E6%8E%A8%E8%8D%90)
      - [方式二：先打包 JAR 再运行](#%E6%96%B9%E5%BC%8F%E4%BA%8C%E5%85%88%E6%89%93%E5%8C%85-jar-%E5%86%8D%E8%BF%90%E8%A1%8C)
    - [验证](#%E9%AA%8C%E8%AF%81)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Stage 2 Week 5 Day 1 - 应用骨架 + Docker 镜像 + 推送 ECR（最小可运行）

> **目标**
> 1. 以 Spring Initializr 生成 `apps/task-api`：Web、Actuator。
> 2. 提供 2 个端点：`GET /healthz`（返回 `"ok"`）与 `GET /api/tasks`（返回内存列表）。
> 3. `Dockerfile`（基于 `eclipse-temurin:21-jre`），本地构建，推到 ECR。

---

## 前置检查 & 环境就绪

### 本机工具自检

在终端依次执行，有输出即可：

```bash
(venv) renda@RendaZhangComputer:/mnt/d/0Repositories/2025Java$ java -version
openjdk version "21.0.8" 2025-07-15
OpenJDK Runtime Environment (build 21.0.8+9-Ubuntu-0ubuntu124.04.1)
OpenJDK 64-Bit Server VM (build 21.0.8+9-Ubuntu-0ubuntu124.04.1, mixed mode, sharing)
(venv) renda@RendaZhangComputer:/mnt/d/0Repositories/2025Java$ mvn -v
Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Maven home: /mnt/d/Java/apache-maven-3.9.9
Java version: 21.0.8, vendor: Ubuntu, runtime: /usr/lib/jvm/java-21-openjdk-amd64
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "5.15.167.4-microsoft-standard-wsl2", arch: "amd64", family: "unix"
(venv) renda@RendaZhangComputer:/mnt/d/0Repositories/2025Java$ docker version --format '{{.Server.Version}}'
28.3.3
(venv) renda@RendaZhangComputer:/mnt/d/0Repositories/2025Java$ aws --version
aws-cli/2.27.41 Python/3.13.4 Linux/5.15.167.4-microsoft-standard-WSL2 exe/x86_64.ubuntu.24
(venv) renda@RendaZhangComputer:/mnt/d/0Repositories/2025Java$ kubectl version --client --output=yaml | sed -n '1,10p'
clientVersion:
  buildDate: "2025-05-15T08:27:33Z"
  compiler: gc
  gitCommit: 8adc0f041b8e7ad1d30e29cc59c6ae7a15e19828
  gitTreeState: clean
  gitVersion: v1.33.1
  goVersion: go1.24.2
  major: "1"
  minor: "33"
  platform: linux/amd64
```

### 设置本周通用变量

```bash
export AWS_REGION=us-east-1
export CLUSTER=dev
export PROFILE=phase2-sso
export ECR_REPO=task-manager
export NS=svc-task
export APP=task-api
```

### 校验 AWS 身份 & EKS 连通

```bash
# AWS SSO 登录
aws sso login --profile "$PROFILE"

# 身份应返回 Account / Arn
aws sts get-caller-identity --profile "$PROFILE"
{
    "UserId": "AROAYGHSMSUJ476XGJ3BO:RendaZhang",
    "Account": "563149051155",
    "Arn": "arn:aws:sts::563149051155:assumed-role/AWSReservedSSO_AdministratorAccess_bb673747cef3fdeb/RendaZhang"
}

# 查看是否已有集群（没有也没关系，继续下一步）
aws eks list-clusters --region "$AWS_REGION" --profile "$PROFILE" | grep -E "\"$CLUSTER\"" || echo "EKS not found (ok for now)"
```

### 预检 ECR 登录（为稍后推送做准备）

```bash
ACCOUNT_ID=$(aws sts get-caller-identity --profile "$PROFILE" --query Account --output text)
aws ecr get-login-password --region "$AWS_REGION" --profile "$PROFILE" \
| docker login --username AWS --password-stdin "$ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"
# 显示 Login Succeeded
```

---

## 创建最小 Spring Boot 服务并本地运行

### 创建项目骨架

```bash
WORK_DIR=.
mkdir -p ${WORK_DIR}/task-api/src/main/java/com/renda/task \
         ${WORK_DIR}/task-api/src/main/resources
cd ${WORK_DIR}/task-api
```

新建 pom.xml（JDK 21，Web + Actuator）：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.renda</groupId>
  <artifactId>task-api</artifactId>
  <version>0.1.0</version>
  <name>task-api</name>
  <description>Minimal Spring Boot service for ECR/EKS</description>

  <properties>
    <java.version>21</java.version>
    <spring.boot.version>3.3.2</spring.boot.version>
    <maven.compiler.version>3.11.0</maven.compiler.version>
    <maven.compiler.parameters>true</maven.compiler.parameters>
  </properties>

  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>${spring.boot.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <!-- 单元测试（可留待后续） -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>${spring.boot.version}</version> <!-- 给出版本 -->
        <configuration>
          <layers enabled="true"/> <!-- 便于后续做多层镜像 -->
        </configuration>
        <executions>
          <execution>
            <goals>
              <goal>repackage</goal>  <!-- 绑定到 package 生命周期 -->
            </goals>
          </execution>
        </executions>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>${maven.compiler.version}</version>
        <configuration>
          <release>${java.version}</release>
          <parameters>true</parameters> <!-- 让反射能拿到参数名 -->
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

### 编写应用主类

`src/main/java/com/renda/task/TaskApiApplication.java`:

```java
package com.renda.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskApiApplication.class, args);
    }
}
```

### 编写最小控制器

`src/main/java/com/renda/task/HelloController.java`:

```java
package com.renda.task;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public String hello(@RequestParam(name = "name", required = false, defaultValue = "World") String name) {
        return "hello " + name;
    }

    // 预留一个“业务探针”，后续可被 readiness probe 依赖
    @GetMapping("/api/ping")
    public String ping() {
        return "pong";
    }
}
```

### 打开 Actuator 健康检查

`src/main/resources/application.yml`:

```yaml
server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      probes:
        enabled: true   # 启用 /actuator/health/{liveness,readiness}
```

### 本地运行

#### 方式一：开发期推荐

```bash
mvn -q spring-boot:run
```

#### 方式二：先打包 JAR 再运行

确保 Spring Boot 插件段有 `repackage` 的 goal，否则会报错：`no main manifest attribute, in target/task-api-0.1.0.jar`。

重新构建：

```bash
mvn -DskipTests clean package
java -jar target/task-api-0.1.0.jar
```

### 验证

确认这是一个“Boot 可执行 JAR”：

```bash
jar -tf target/task-api-0.1.0.jar | head -n 5
# 预期能看到 BOOT-INF/ 与 META-INF/ 目录

# 看 MANIFEST 关键字段（Start-Class 会是你的主类）
jar -xvf target/task-api-0.1.0.jar META-INF/MANIFEST.MF >/dev/null
sed -n '1,40p' META-INF/MANIFEST.MF | egrep 'Main-Class|Start-Class'
# 预期：
# Main-Class: org.springframework.boot.loader.launch.JarLauncher
# Start-Class: com.renda.task.TaskApiApplication
```

测试接口：

```bash
# 业务接口
curl -s "http://localhost:8080/api/hello?name=Renda"

# 健康检查（总体）
curl -s http://localhost:8080/actuator/health

# K8s 友好的探针
curl -s http://localhost:8080/actuator/health/liveness
curl -s http://localhost:8080/actuator/health/readiness
```

预期：

- `/api/hello` 返回 `hello Renda`
- `/actuator/health` 返回 `{"status":"UP"}`（或含组件详情）
- `liveness/readiness` 返回 `{"status":"UP"}`
