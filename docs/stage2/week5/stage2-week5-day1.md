<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Stage 2 Week 5 Day 1 - 应用骨架 + Docker 镜像 + 推送 ECR（最小可运行）](#stage-2-week-5-day-1---%E5%BA%94%E7%94%A8%E9%AA%A8%E6%9E%B6--docker-%E9%95%9C%E5%83%8F--%E6%8E%A8%E9%80%81-ecr%E6%9C%80%E5%B0%8F%E5%8F%AF%E8%BF%90%E8%A1%8C)
  - [本机工具自检](#%E6%9C%AC%E6%9C%BA%E5%B7%A5%E5%85%B7%E8%87%AA%E6%A3%80)
  - [设置本周通用变量](#%E8%AE%BE%E7%BD%AE%E6%9C%AC%E5%91%A8%E9%80%9A%E7%94%A8%E5%8F%98%E9%87%8F)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Stage 2 Week 5 Day 1 - 应用骨架 + Docker 镜像 + 推送 ECR（最小可运行）

> **目标**
> 1. 以 Spring Initializr 生成 `apps/task-api`：Web、Actuator。
> 2. 提供 2 个端点：`GET /healthz`（返回 `"ok"`）与 `GET /api/tasks`（返回内存列表）。
> 3. `Dockerfile`（基于 `eclipse-temurin:21-jre`），本地构建，推到 ECR。

---

## 本机工具自检

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

## 设置本周通用变量

TBD
