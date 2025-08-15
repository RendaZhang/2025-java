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
  - [容器化 & 推送到 ECR](#%E5%AE%B9%E5%99%A8%E5%8C%96--%E6%8E%A8%E9%80%81%E5%88%B0-ecr)
    - [设置本步变量：](#%E8%AE%BE%E7%BD%AE%E6%9C%AC%E6%AD%A5%E5%8F%98%E9%87%8F)
    - [添加 `.dockerignore`](#%E6%B7%BB%E5%8A%A0-dockerignore)
    - [创建多阶段 Dockerfile](#%E5%88%9B%E5%BB%BA%E5%A4%9A%E9%98%B6%E6%AE%B5-dockerfile)
    - [本地构建镜像](#%E6%9C%AC%E5%9C%B0%E6%9E%84%E5%BB%BA%E9%95%9C%E5%83%8F)
    - [创建/校验 ECR 仓库并登录](#%E5%88%9B%E5%BB%BA%E6%A0%A1%E9%AA%8C-ecr-%E4%BB%93%E5%BA%93%E5%B9%B6%E7%99%BB%E5%BD%95)
    - [打标签并推送](#%E6%89%93%E6%A0%87%E7%AD%BE%E5%B9%B6%E6%8E%A8%E9%80%81)
    - [验证镜像已入库](#%E9%AA%8C%E8%AF%81%E9%95%9C%E5%83%8F%E5%B7%B2%E5%85%A5%E5%BA%93)
  - [部署到 EKS 并验证](#%E9%83%A8%E7%BD%B2%E5%88%B0-eks-%E5%B9%B6%E9%AA%8C%E8%AF%81)
    - [变量 & 上下文](#%E5%8F%98%E9%87%8F--%E4%B8%8A%E4%B8%8B%E6%96%87)
    - [部署清单（Deployment + Service）](#%E9%83%A8%E7%BD%B2%E6%B8%85%E5%8D%95deployment--service)
    - [应用并等待滚动完成](#%E5%BA%94%E7%94%A8%E5%B9%B6%E7%AD%89%E5%BE%85%E6%BB%9A%E5%8A%A8%E5%AE%8C%E6%88%90)
    - [端到端验证（port-forward）](#%E7%AB%AF%E5%88%B0%E7%AB%AF%E9%AA%8C%E8%AF%81port-forward)
    - [创建 Ingress（今天暂时跳过，ALB 控制器安装好后再做）](#%E5%88%9B%E5%BB%BA-ingress%E4%BB%8A%E5%A4%A9%E6%9A%82%E6%97%B6%E8%B7%B3%E8%BF%87alb-%E6%8E%A7%E5%88%B6%E5%99%A8%E5%AE%89%E8%A3%85%E5%A5%BD%E5%90%8E%E5%86%8D%E5%81%9A)

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
export ECR_REPO=task-api
export NS=svc-task
export APP=task-api
export VERSION=0.1.0
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
    <finalName>app</finalName>
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

确保 Spring Boot 插件段有 `repackage` 的 goal，否则会报错：`no main manifest attribute, in target/app.jar`。

重新构建：

```bash
mvn -DskipTests clean package
java -jar target/app.jar
```

### 验证

确认这是一个“Boot 可执行 JAR”：

```bash
jar -tf target/app.jar | head -n 5
# 预期能看到 BOOT-INF/ 与 META-INF/ 目录

# 看 MANIFEST 关键字段（Start-Class 会是你的主类）
jar -xvf target/app.jar META-INF/MANIFEST.MF >/dev/null
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

---

## 容器化 & 推送到 ECR

### 设置本步变量：

```bash
# 账号 ID（稍后打标签/推送要用）
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text --profile "$PROFILE")
```

### 添加 `.dockerignore`

避免把无关文件打进镜像。

新建 `./.dockerignore`：

```bash
.gitignore
.gitattributes
.git
.pre-commit-config.yaml
requirements.txt
LICENSE
venv/
.venv/
scripts/
target/
**/*.iml
.mvn/
.idea/
.vscode/
Dockerfile
README*
```

### 创建多阶段 Dockerfile

新建 `./Dockerfile`：

```Dockerfile
# ---------- Build stage ----------
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /build

# 优化缓存：分步复制 pom.xml 和源码
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline

# 构建完成后清理可能残留 Maven 缓存
COPY src ./src
RUN mvn -q -B -DskipTests package && \
    rm -rf ~/.m2/repository

# ---------- Runtime stage ----------
# 使用 Alpine 版本 的 JRE 镜像以减少运行时镜像大小
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 使用最小用户和权限
# 多个命令放在一个 RUN 以减少镜像层数
RUN apk add --no-cache shadow && \
    useradd -r -u 10001 app && \
    chown -R app /app && \
    apk del shadow
USER app

# 复制构建产物（明确 JAR 名称）
COPY --from=build /build/target/app.jar /app/app.jar

EXPOSE 8080
ENV JAVA_OPTS=""

# 安装 curl 并添加健康检查
# HEALTHCHECK 在 Kubernetes 中可能会被探针覆盖
RUN apk add --no-cache curl
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -s http://127.0.0.1:8080/actuator/health || exit 1

ENTRYPOINT ["sh","-lc","exec java $JAVA_OPTS -jar /app/app.jar"]
```

说明：

- 采用 **JDK 21** 构建、**JRE 21** 运行；
- 以非 root 用户运行；
- 先 `pom.xml` → `go-offline` 再拷源码，提升层缓存命中；
- 运行层可以使用通配复制 jar，避免版本号改动时频繁改 Dockerfile。

### 本地构建镜像

```bash
# 查看确认节点的信息
kubectl get nodes -L kubernetes.io/arch,kubernetes.io/instance-type -o wide

# 精确字段
kubectl get nodes \
  -o custom-columns=NAME:.metadata.name,ARCH:.status.nodeInfo.architecture,OS:.status.nodeInfo.operatingSystem,INSTANCE:.metadata.labels."node\.kubernetes\.io/instance-type"

# 查看单个节点
kubectl get node <NODE_NAME> -o jsonpath='{.status.nodeInfo.architecture}'; echo
```

> 如果 EKS 节点是 x86_64（最常见），固定平台为 `linux/amd64`；若是 Graviton/ARM 节点，改成 `linux/arm64`。

```bash
# 清理所有未使用的镜像、容器、网络和卷
docker system prune -a

# 正式构建镜像
docker build --platform=linux/amd64 -t "$APP:$VERSION" .

# 列出所有本地镜像及大小
docker images
# 输出：
REPOSITORY     TAG       IMAGE ID       CREATED              SIZE
task-api       0.1.0     a2fa74bac2dd   About a minute ago   229MB

# 启动容器（暴露 8080 端口）
docker run -d -p 8080:8080 --name my-task task-api:0.1.0
# 交互式运行容器
docker run -it task-api:0.1.0 /bin/bash
# 查看容器启动状态
docker ps
# 测试 API 是否正常
curl http://localhost:8080/actuator/health
# 查看容器日志
docker logs my-task
# 停止容器
docker stop my-task
# 查看所有包括已经停止的容器
docker ps -a
# 删除容器
docker rm my-task
# 删除镜像
# docker rmi task-api
```

### 创建/校验 ECR 仓库并登录

```bash
# 创建仓库（若已存在会跳过）
aws ecr describe-repositories --repository-names "$ECR_REPO" --region "$AWS_REGION" --profile "$PROFILE" >/dev/null 2>&1 \
  || aws ecr create-repository \
       --repository-name "$ECR_REPO" \
       --image-scanning-configuration scanOnPush=true \
       --image-tag-mutability IMMUTABLE \
       --region "$AWS_REGION" \
       --profile "$PROFILE"

# 返回输出：
# {
#     "repository": {
#         "repositoryArn": "arn:aws:ecr:us-east-1:563149051155:repository/task-api",
#         "registryId": "563149051155",
#         "repositoryName": "task-api",
#         "repositoryUri": "563149051155.dkr.ecr.us-east-1.amazonaws.com/task-api",
#         "createdAt": "2025-08-16T00:52:26.200000+08:00",
#         "imageTagMutability": "IMMUTABLE",
#         "imageScanningConfiguration": {
#             "scanOnPush": true
#         },
#         "encryptionConfiguration": {
#             "encryptionType": "AES256"
#         }
#     }
# }

# 生命周期：仅保留最近 1 个 tag 且没有 tag 的一天就过期
# 建议后续至少保留最近 5~10 个 tag、untagged 保留 7d。
aws ecr put-lifecycle-policy \
  --repository-name "$ECR_REPO" \
  --lifecycle-policy-text '{
    "rules": [
      {
        "rulePriority": 1,
        "description": "expire untagged images older than 1 days",
        "selection": {
          "tagStatus": "untagged",
          "countType": "sinceImagePushed",
          "countUnit": "days",
          "countNumber": 1
        },
        "action": { "type": "expire" }
      },
      {
        "rulePriority": 2,
        "description": "keep last 1 images (any tag status)",
        "selection": {
          "tagStatus": "any",
          "countType": "imageCountMoreThan",
          "countNumber": 1
        },
        "action": { "type": "expire" }
      }
    ]
  }' \
  --region "$AWS_REGION" \
  --profile "$PROFILE"

# 返回输出：
# {
#     "registryId": "563149051155",
#     "repositoryName": "task-api",
#     "lifecyclePolicyText": "{\"rules\":[{\"rulePriority\":1,\"description\":\"expire untagged images older than 1 days\",\"selection\":{\"tagStatus\":\"untagged\",\"countType\":\"sinceImagePushed\",\"countUnit\":\"days\",\"countNumber\":1},\"action\":{\"type\":\"expire\"}},{\"rulePriority\":2,\"description\":\"keep last 1 images (any tag status)\",\"selection\":{\"tagStatus\":\"any\",\"countType\":\"imageCountMoreThan\",\"countNumber\":1},\"action\":{\"type\":\"expire\"}}]}"
# }

# 登录 ECR
aws ecr get-login-password --region "$AWS_REGION" --profile "$PROFILE" \
| docker login --username AWS --password-stdin "$ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"
# 返回：
# Login Succeeded
```

### 打标签并推送

```bash
REMOTE="$ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPO"

docker tag "$APP:$VERSION" "$REMOTE:$VERSION"
# 方便手动拉取；生产以版本/commit 为准
docker tag "$APP:$VERSION" "$REMOTE:latest"

docker push "$REMOTE:$VERSION"
# 返回输出：
# The push refers to repository [563149051155.dkr.ecr.us-east-1.amazonaws.com/task-api]
# 66d1157a40eb: Pushed
# a737ea50e690: Pushed
# 3f3a99dba9b6: Pushed
# 880a6d9a5a59: Pushed
# bb64f233ca86: Pushed
# 1eb3de508cc3: Pushed
# c2d2b55d55c7: Pushed
# 418dccb7d85a: Pushed
# 0.1.0: digest: sha256:927d20ca4cebedc14f81770e8e5e49259684723ba65b76e7c59f3003cc9a9741 size: 1994
docker push "$REMOTE:latest"
# 返回输出：
# The push refers to repository [563149051155.dkr.ecr.us-east-1.amazonaws.com/task-api]
# 66d1157a40eb: Layer already exists
# a737ea50e690: Layer already exists
# 3f3a99dba9b6: Layer already exists
# 880a6d9a5a59: Layer already exists
# bb64f233ca86: Layer already exists
# 1eb3de508cc3: Layer already exists
# c2d2b55d55c7: Layer already exists
# 418dccb7d85a: Layer already exists
# latest: digest: sha256:927d20ca4cebedc14f81770e8e5e49259684723ba65b76e7c59f3003cc9a9741 size: 1994
```

### 验证镜像已入库

看到 digest 即成功。

```bash
aws ecr describe-images \
  --repository-name "$ECR_REPO" \
  --image-ids imageTag="$VERSION" \
  --query 'imageDetails[0].imageDigest' \
  --output text \
  --region "$AWS_REGION" \
  --profile "$PROFILE"
# 返回输出：
# sha256:927d20ca4cebedc14f81770e8e5e49259684723ba65b76e7c59f3003cc9a9741
```

预期结果：
- `docker build` 成功产出镜像（大小 ~百 MB 级别）。
- `docker login` 显示 Login Succeeded。
- `describe-images` 返回一串 sha256:... 的 digest。

---

## 部署到 EKS 并验证

先用 Deployment + Service(ClusterIP) 完成集群内可用，再用 port-forward 做对外验证。

是否创建 Ingress 取决于是否已装好 AWS Load Balancer Controller（若未装，今天先不做 Ingress）。

### 变量 & 上下文

```bash
export PROFILE=phase2-sso
export AWS_REGION=us-east-1
export CLUSTER=dev
export NS=svc-task
export APP=task-api
export ECR_REPO=task-api
export ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text --profile "$PROFILE")
export REMOTE="$ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPO"
export DIGEST=sha256:927d20ca4cebedc14f81770e8e5e49259684723ba65b76e7c59f3003cc9a9741

# 配置 kubectl 上下文
aws eks update-kubeconfig \
  --name "$CLUSTER" --region "$AWS_REGION" --profile "$PROFILE"

# 命名空间
kubectl get ns "$NS" >/dev/null 2>&1 || kubectl create ns "$NS"
# 若已存在会报 AlreadyExists，无妨
# 正常返回输出：
# namespace/svc-task created
```

### 部署清单（Deployment + Service）

在 renda-cloud-lab 项目根目录创建 `k8s.yaml`（或任何位置）：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: task-api
  namespace: svc-task
spec:
  replicas: 2
  revisionHistoryLimit: 3
  selector:
    matchLabels: { app: task-api }
  template:
    metadata:
      labels: { app: task-api }
    spec:
      containers:
        - name: task-api
          # 通过 digest 锁定镜像，避免 tag 漂移
          # 把镜像行里的 <ACCOUNT_ID> 替换成真实账号 ID
          image: "<ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/task-api@sha256:927d20ca4cebedc14f81770e8e5e49259684723ba65b76e7c59f3003cc9a9741"
          imagePullPolicy: IfNotPresent
          ports:
            - name: http
              containerPort: 8080
          resources:
            requests:
              cpu: "100m"
              memory: "128Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 5
            periodSeconds: 10
            timeoutSeconds: 2
            failureThreshold: 3
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 10
            timeoutSeconds: 2
            failureThreshold: 3
---
apiVersion: v1
kind: Service
metadata:
  name: task-api
  namespace: svc-task
spec:
  selector: { app: task-api }
  ports:
    - name: http
      port: 8080
      targetPort: 8080
  type: ClusterIP
```

### 应用并等待滚动完成

```bash
kubectl -n "$NS" apply -f k8s.yaml
# 输出：
# deployment.apps/task-api created
# service/task-api created

kubectl -n "$NS" rollout status deploy/"$APP" --timeout=120s
# 输出：
# deployment "task-api" successfully rolled out

kubectl -n "$NS" get pods -o wide
# 输出：
# NAME                        READY   STATUS    RESTARTS   AGE     IP             NODE                           NOMINATED NODE   READINESS GATES
# task-api-59f8cc6cbf-4nh6w   1/1     Running   0          2m11s   10.0.128.148   ip-10-0-142-115.ec2.internal   <none>           <none>
# task-api-59f8cc6cbf-bxv5z   1/1     Running   0          2m11s   10.0.131.175   ip-10-0-142-115.ec2.internal   <none>           <none>

kubectl -n "$NS" get svc "$APP" -o wide
# 输出：
# NAME       TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE     SELECTOR
# task-api   ClusterIP   172.20.173.60   <none>        8080/TCP   3m42s   app=task-api
```

预期：

`rollout status` 显示 `successfully rolled ou`t，`READY`=`1/1`，`STATUS`=`Running`。

### 端到端验证（port-forward）

开一个终端做转发；另一个终端发请求验证：

```bash
# 终端 A（保持挂着）
kubectl -n "$NS" port-forward svc/"$APP" 8080:8080

# 终端 B
curl -s "http://127.0.0.1:8080/api/hello?name=Renda"
curl -s "http://127.0.0.1:8080/actuator/health"
curl -s "http://127.0.0.1:8080/actuator/health/readiness"
curl -s "http://127.0.0.1:8080/actuator/health/liveness"
```

预期：

返回 `hello Renda` 与各健康检查 `{"status":"UP"}`。

确认一下上下文确实是 EKS 集群（而非本地 kind/minikube）：

```bash
kubectl config current-context
# 输出：
# arn:aws:eks:us-east-1:563149051155:cluster/dev

kubectl cluster-info
# 输出：
# Kubernetes control plane is running at https://4AAE5B95DE964204D59DE72344B7D657.gr7.us-east-1.eks.amazonaws.com
# CoreDNS is running at https://4AAE5B95DE964204D59DE72344B7D657.gr7.us-east-1.eks.amazonaws.com/api/v1/namespaces/kube-system/services/kube-dns:dns/proxy

# 看看 ProviderID/内网网段是否是 AWS/EKS 风格
kubectl get nodes -o wide | awk '{print $1,$7,$8}'
# 输出：
# NAME EXTERNAL-IP OS-IMAGE
# ip-10-0-142-115.ec2.internal <none> Amazon
```

### 创建 Ingress（今天暂时跳过，ALB 控制器安装好后再做）

若此前已部署 AWS Load Balancer Controller 且给子网打好了标签，可以再加一个 Ingress：

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: task-api
  namespace: svc-task
  annotations:
    # 选择公有还是私有子网（看子网标签）
    alb.ingress.kubernetes.io/scheme: internet-facing
    alb.ingress.kubernetes.io/target-type: ip
    # （可选）健康检查路径
    alb.ingress.kubernetes.io/healthcheck-path: /actuator/health/readiness
spec:
  ingressClassName: alb
  rules:
    - http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: task-api
                port:
                  number: 8080
```

```bash
kubectl -n "$NS" apply -f ingress.yaml
kubectl -n "$NS" get ingress
```

预期：

几分钟后出现 `ADDRESS`（ALB DNS 名）。

若迟迟不出，重点排查：**子网标签**、**控制器权限**、**SG 端口**、**探针健康**。
