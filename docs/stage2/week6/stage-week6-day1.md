<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 1 - 应用指标暴露 + AMP 工作区](#day-1---%E5%BA%94%E7%94%A8%E6%8C%87%E6%A0%87%E6%9A%B4%E9%9C%B2--amp-%E5%B7%A5%E4%BD%9C%E5%8C%BA)
- [第一步：在仓库根目录创建本地环境文件并验证加载](#%E7%AC%AC%E4%B8%80%E6%AD%A5%E5%9C%A8%E4%BB%93%E5%BA%93%E6%A0%B9%E7%9B%AE%E5%BD%95%E5%88%9B%E5%BB%BA%E6%9C%AC%E5%9C%B0%E7%8E%AF%E5%A2%83%E6%96%87%E4%BB%B6%E5%B9%B6%E9%AA%8C%E8%AF%81%E5%8A%A0%E8%BD%BD)
  - [第二步：为 `task-api` 打开 Prometheus 指标（加依赖 + 配置暴露）](#%E7%AC%AC%E4%BA%8C%E6%AD%A5%E4%B8%BA-task-api-%E6%89%93%E5%BC%80-prometheus-%E6%8C%87%E6%A0%87%E5%8A%A0%E4%BE%9D%E8%B5%96--%E9%85%8D%E7%BD%AE%E6%9A%B4%E9%9C%B2)
    - [修改 `pom.xml`（添加 Prometheus 注册表依赖）](#%E4%BF%AE%E6%94%B9-pomxml%E6%B7%BB%E5%8A%A0-prometheus-%E6%B3%A8%E5%86%8C%E8%A1%A8%E4%BE%9D%E8%B5%96)
    - [修改 `application.yml`（暴露端点 + 打开直方图）](#%E4%BF%AE%E6%94%B9-applicationyml%E6%9A%B4%E9%9C%B2%E7%AB%AF%E7%82%B9--%E6%89%93%E5%BC%80%E7%9B%B4%E6%96%B9%E5%9B%BE)
  - [第三步：在本地启动 `task-api` 并验证 `/actuator/prometheus`](#%E7%AC%AC%E4%B8%89%E6%AD%A5%E5%9C%A8%E6%9C%AC%E5%9C%B0%E5%90%AF%E5%8A%A8-task-api-%E5%B9%B6%E9%AA%8C%E8%AF%81-actuatorprometheus)
  - [第四步：创建 AMP Workspace，拿到 Workspace ID 与 remote_write 端点](#%E7%AC%AC%E5%9B%9B%E6%AD%A5%E5%88%9B%E5%BB%BA-amp-workspace%E6%8B%BF%E5%88%B0-workspace-id-%E4%B8%8E-remote_write-%E7%AB%AF%E7%82%B9)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 1 - 应用指标暴露 + AMP 工作区

**目标**：

让 `task-api` 暴露 Prometheus 指标端点（`/actuator/prometheus`）；

创建 AMP Workspace（仅创建，不接入采集——采集放到 Day 2）。

**产出**：

1. `.env.week6.local`
2. `task-api` 已启用 Actuator + Prometheus（可本地或集群内 `curl` 验证）
3. 已创建 AMP Workspace，并把 `AMP_WORKSPACE_ID` 写回 `.env.week6.local`

---

# 第一步：在仓库根目录创建本地环境文件并验证加载

**目标**：

新建 `.env.week6.local`（被 git 忽略），写入 Week 6 所需的最小变量，并在当前 Shell 中正确加载。

**执行：**

```bash
# 进入仓库根目录
cd $WORK_DIR

# 1) 新建/覆盖本地 env 文件（注意：region 已按你的 us-east-1）
tee .env.week6.local >/dev/null <<'EOF'
# Region / Names
AWS_REGION=us-east-1
NS=svc-task
APP=task-api

# Namespaces
PROM_NAMESPACE=observability
CHAOS_NS=chaos-testing

# AMP (ID 会在创建后回填)
AMP_ALIAS=amp-renda-cloud-lab-wk6-use1
AMP_WORKSPACE_ID=
EOF

# 2) 确保被 git 忽略（如已有则不会重复添加）
grep -qxF '.env.week6.local' .gitignore || echo '.env.week6.local' >> .gitignore

# 3) 加载并回显关键变量，确认写入成功
set -a; source ./.env.week6.local; set +a
printf "AWS_REGION=%s\nNS=%s\nAPP=%s\nPROM_NAMESPACE=%s\nCHAOS_NS=%s\nAMP_ALIAS=%s\nAMP_WORKSPACE_ID=%s\n" \
  "$AWS_REGION" "$NS" "$APP" "$PROM_NAMESPACE" "$CHAOS_NS" "$AMP_ALIAS" "${AMP_WORKSPACE_ID:-<empty>}"

# 4) 登录 aws cli
export AWS_PROFILE=phase2-sso
aws sso login
```

**输出：**

```bash
AWS_REGION=us-east-1
NS=svc-task
APP=task-api
PROM_NAMESPACE=observability
CHAOS_NS=chaos-testing
AMP_ALIAS=amp-renda-cloud-lab-wk6-use1
AMP_WORKSPACE_ID=<empty>
...
Successfully logged into Start URL: https://d-9066388969.awsapps.com/start
```

---

## 第二步：为 `task-api` 打开 Prometheus 指标（加依赖 + 配置暴露）

- 当前 `pom.xml` 里已有 Actuator，但**没有** `micrometer-registry-prometheus`，需要补上它才能提供 `/actuator/prometheus` 端点。
- `application.yml` 目前只暴露了 `health, info`，需要把 `metrics, prometheus` 加进暴露列表，并开启 HTTP 直方图以便做 P95。
- Spring 官方说明：默认只暴露 `/health`；要显式配置 `management.endpoints.web.exposure.include`，且接入 Prometheus 需添加 `micrometer-registry-prometheus`。

### 修改 `pom.xml`（添加 Prometheus 注册表依赖）

> 放到 `<dependencies>` 里；使用 Spring Boot 的依赖管理，无需写版本号。

```xml
<!-- micrometer → prometheus registry -->
<dependency>
  <groupId>io.micrometer</groupId>
  <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

其余已有依赖保持不变：`spring-boot-starter-web`, `spring-boot-starter-actuator` 等。

### 修改 `application.yml`（暴露端点 + 打开直方图）

> 在原有内容基础上更新为如下（保留 `server.port: 8080` 与探针设置，并新增暴露项和直方图设置）：

```yaml
server:
  port: 8080

management:
  endpoint:
    health:
      probes:
        enabled: true  # /actuator/health/{liveness,readiness}
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    # 给所有指标加一个统一标签，便于 Grafana/GQL 过滤
    tags:
      application: ${APP:task-api}
    # 为 http.server.requests 开启直方图，用于 P95/P99 统计
    distribution:
      percentiles-histogram:
        http.server.requests: true
```

> 说明：
>
> - 只有把 `prometheus` 加到 exposure，`/actuator/prometheus` 才会出现在 HTTP 端点上。
> - Micrometer + Prometheus 的组合是 Spring Boot 3 推荐做法；开启直方图有助于在 PromQL/Grafana 中做 P95。

---

## 第三步：在本地启动 `task-api` 并验证 `/actuator/prometheus`

**目标**：

确认刚才的依赖与配置已生效，能输出 Prometheus 指标。

执行：

```bash
# 直接本地跑（推荐）
mvn -f task-api/pom.xml spring-boot:run

# 若 8080 被占用，可改临时端口：
# SERVER_PORT=8081 mvn -f task-api/pom.xml spring-boot:run
```

等日志出现 “Started … in … seconds” 后，开一个新终端验证：

```bash
# 如果是默认 8080
curl -sf http://localhost:8080/actuator/prometheus | head -n 30

# 如果用了 8081
# curl -sf http://localhost:8081/actuator/prometheus | head -n 30
```

**已经看到**：

- 以 `# HELP ...`、`# TYPE ...` 开头的行；
- 常见指标名如：`jvm_memory_used_bytes`、`http_server_requests_seconds_count`、`process_cpu_usage` 等。

Prometheus 端点已成功暴露。

`Ctrl + C` 停止 Spring Boot 进程。

常见问题速排：

- **404**：检查 `application.yml` 是否包含 `management.endpoints.web.exposure.include: health,info,metrics,prometheus`。
- **401/403**：如果项目启用了 Spring Security，需要另外放行 `/actuator/**`。
- **端口占用**：用 `SERVER_PORT=8081`（见上）或关闭占用进程。

---

## 第四步：创建 AMP Workspace，拿到 Workspace ID 与 remote_write 端点

**目标**：

在 `us-east-1` 创建一个新的 Amazon Managed Service for Prometheus（AMP）Workspace，记录 `workspaceId`，并查询 `remote_write` 端点，写回到 `.env.week6.local` 里以便后续 Day 2 采集用。

> 说明要点：
>
> - `--alias` 只是便于识别的友好名，**不要求唯一**（同名也允许），前后空格会被修剪。
> - `describe-workspace` 会返回 `prometheusEndpoint`，形如 `.../workspaces/<id>/api/v1/`，用于拼接 `remote_write`：`.../api/v1/remote_write`。
> - 如需删除，可用 `aws amp delete-workspace --workspace-id <id>`（数据会在一段时间后永久删除）。

执行命令（在仓库根目录）：

```bash
# 1) 加载本地变量
set -a; source ./.env.week6.local; set +a

# 2) 创建 AMP Workspace（返回 workspaceId）
AMP_WORKSPACE_ID=$(
  aws amp create-workspace \
    --region "$AWS_REGION" \
    --alias "$AMP_ALIAS" \
    --query 'workspaceId' --output text
)
echo "[week6] AMP_WORKSPACE_ID=$AMP_WORKSPACE_ID"

# 3) 写回到 .env.week6.local（便于后续 Day 2 使用）
awk -v v="$AMP_WORKSPACE_ID" '/^AMP_WORKSPACE_ID=/{$0="AMP_WORKSPACE_ID="v}1' .env.week6.local > .env.week6.local.new && mv .env.week6.local.new .env.week6.local

# 4) 查询 prometheusEndpoint，并打印出 remote_write 完整地址
AMP_ENDPOINT=$(
  aws amp describe-workspace \
    --region "$AWS_REGION" \
    --workspace-id "$AMP_WORKSPACE_ID" \
    --query 'workspace.prometheusEndpoint' --output text
)
echo "[week6] prometheusEndpoint: $AMP_ENDPOINT"
echo "[week6] remote_write: ${AMP_ENDPOINT}api/v1/remote_write"
```

**输出**：

```bash
[week6] AMP_WORKSPACE_ID=ws-4c9b04d5-5e49-415e-90ef-747450304dca
[week6] prometheusEndpoint: https://aps-workspaces.us-east-1.amazonaws.com/workspaces/ws-4c9b04d5-5e49-415e-90ef-747450304dca/
[week6] remote_write: https://aps-workspaces.us-east-1.amazonaws.com/workspaces/ws-4c9b04d5-5e49-415e-90ef-747450304dca/api/v1/remote_write
```

> 如意外重复创建了 Workspace，在“收尾清理”时再统一处理回收；今天的目标是**先有一个可用的 workspaceId 和 remote_write 地址**。
> （若更谨慎：也可以先用 `aws amp list-workspaces --region us-east-1 --alias "$AMP_ALIAS"` 查看同前缀 alias 的已有工作区，再决定是否复用或新建。

检查新建的 workspace：

```bash
$ aws amp list-workspaces --region us-east-1 --alias "$AMP_ALIAS"

{
    "workspaces": [
        {
            "alias": "amp-renda-cloud-lab-wk6-use1",
            "arn": "arn:aws:aps:us-east-1:563149051155:workspace/ws-4c9b04d5-5e49-415e-90ef-747450304dca",
            "createdAt": "2025-08-27T18:41:50.195000+08:00",
            "status": {
                "statusCode": "ACTIVE"
            },
            "tags": {},
            "workspaceId": "ws-4c9b04d5-5e49-415e-90ef-747450304dca"
        }
    ]
}
```

---
