<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 1 - 应用指标暴露 + AMP 工作区](#day-1---%E5%BA%94%E7%94%A8%E6%8C%87%E6%A0%87%E6%9A%B4%E9%9C%B2--amp-%E5%B7%A5%E4%BD%9C%E5%8C%BA)
  - [第一步：在仓库根目录创建本地环境文件并验证加载](#%E7%AC%AC%E4%B8%80%E6%AD%A5%E5%9C%A8%E4%BB%93%E5%BA%93%E6%A0%B9%E7%9B%AE%E5%BD%95%E5%88%9B%E5%BB%BA%E6%9C%AC%E5%9C%B0%E7%8E%AF%E5%A2%83%E6%96%87%E4%BB%B6%E5%B9%B6%E9%AA%8C%E8%AF%81%E5%8A%A0%E8%BD%BD)
  - [第二步：为 `task-api` 打开 Prometheus 指标（加依赖 + 配置暴露）](#%E7%AC%AC%E4%BA%8C%E6%AD%A5%E4%B8%BA-task-api-%E6%89%93%E5%BC%80-prometheus-%E6%8C%87%E6%A0%87%E5%8A%A0%E4%BE%9D%E8%B5%96--%E9%85%8D%E7%BD%AE%E6%9A%B4%E9%9C%B2)
    - [修改 `pom.xml`（添加 Prometheus 注册表依赖）](#%E4%BF%AE%E6%94%B9-pomxml%E6%B7%BB%E5%8A%A0-prometheus-%E6%B3%A8%E5%86%8C%E8%A1%A8%E4%BE%9D%E8%B5%96)
    - [修改 `application.yml`（暴露端点 + 打开直方图）](#%E4%BF%AE%E6%94%B9-applicationyml%E6%9A%B4%E9%9C%B2%E7%AB%AF%E7%82%B9--%E6%89%93%E5%BC%80%E7%9B%B4%E6%96%B9%E5%9B%BE)
  - [第三步：在本地启动 `task-api` 并验证 `/actuator/prometheus`](#%E7%AC%AC%E4%B8%89%E6%AD%A5%E5%9C%A8%E6%9C%AC%E5%9C%B0%E5%90%AF%E5%8A%A8-task-api-%E5%B9%B6%E9%AA%8C%E8%AF%81-actuatorprometheus)
  - [第四步：创建 AMP Workspace，拿到 Workspace ID 与 remote write 端点](#%E7%AC%AC%E5%9B%9B%E6%AD%A5%E5%88%9B%E5%BB%BA-amp-workspace%E6%8B%BF%E5%88%B0-workspace-id-%E4%B8%8E-remote-write-%E7%AB%AF%E7%82%B9)
  - [第五步：构建最新的 task-api → 推送到 ECR → 用 Digest 回滚发布](#%E7%AC%AC%E4%BA%94%E6%AD%A5%E6%9E%84%E5%BB%BA%E6%9C%80%E6%96%B0%E7%9A%84-task-api-%E2%86%92-%E6%8E%A8%E9%80%81%E5%88%B0-ecr-%E2%86%92-%E7%94%A8-digest-%E5%9B%9E%E6%BB%9A%E5%8F%91%E5%B8%83)
  - [第六步：最小连通性预检（AMP remote write ↔ 集群出网 ↔ 新镜像就绪）](#%E7%AC%AC%E5%85%AD%E6%AD%A5%E6%9C%80%E5%B0%8F%E8%BF%9E%E9%80%9A%E6%80%A7%E9%A2%84%E6%A3%80amp-remote-write--%E9%9B%86%E7%BE%A4%E5%87%BA%E7%BD%91--%E6%96%B0%E9%95%9C%E5%83%8F%E5%B0%B1%E7%BB%AA)
    - [变量与 remote write](#%E5%8F%98%E9%87%8F%E4%B8%8E-remote-write)
    - [从集群内做 DNS/TLS 出网预检](#%E4%BB%8E%E9%9B%86%E7%BE%A4%E5%86%85%E5%81%9A-dnstls-%E5%87%BA%E7%BD%91%E9%A2%84%E6%A3%80)
    - [确认新镜像的指标端点在集群内可读](#%E7%A1%AE%E8%AE%A4%E6%96%B0%E9%95%9C%E5%83%8F%E7%9A%84%E6%8C%87%E6%A0%87%E7%AB%AF%E7%82%B9%E5%9C%A8%E9%9B%86%E7%BE%A4%E5%86%85%E5%8F%AF%E8%AF%BB)
    - [只读前置：取 EKS 集群名、OIDC Issuer、以及 AWS 托管策略 ARN](#%E5%8F%AA%E8%AF%BB%E5%89%8D%E7%BD%AE%E5%8F%96-eks-%E9%9B%86%E7%BE%A4%E5%90%8Doidc-issuer%E4%BB%A5%E5%8F%8A-aws-%E6%89%98%E7%AE%A1%E7%AD%96%E7%95%A5-arn)

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

## 第一步：在仓库根目录创建本地环境文件并验证加载

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

## 第四步：创建 AMP Workspace，拿到 Workspace ID 与 remote write 端点

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

> 也可以在 AWS 控制台 `Amazon Prometheus > Workspaces > amp-renda-cloud-lab-wk6-use1` 里面查看详情。

---

## 第五步：构建最新的 task-api → 推送到 ECR → 用 Digest 回滚发布

**目标**：

基于今天更新的 `task-api` 代码，构建新镜像并推到 ECR，拿到 **digest**，用 `kubectl set image` 以 **digest 锁定** 更新现有 Deployment（和 Week5 的策略一致，避免 tag 漂移）。

> 下面命令默认 Region `us-east-1`，ECR 仓库名 `task-api`，命名空间 `svc-task`，应用名 `task-api`，与 Week5 保持一致。

1. **确认节点架构**，以选择正确的 `--platform`：

    ```bash
    kubectl get nodes -o custom-columns=NAME:.metadata.name,ARCH:.status.nodeInfo.architecture
    ```

2. **进入子项目**：

    ```bash
    cd task-api
    ```

3. **变量与登录**：

    ```bash
    # 基本变量
    export PROFILE="phase2-sso"
    # —— 统一导出 AWS_PROFILE，省去每条命令 --profile ——
    export AWS_PROFILE="$PROFILE"
    export AWS_REGION=us-east-1
    export ECR_REPO=task-api
    export APP=task-api
    export NS=svc-task
    # 可追踪 tag
    export VERSION="0.1.0-$(date +%y%m%d%H%M)"
    echo "[week6] ECR Image Tag: $VERSION"

    # 账户与仓库 URI
    ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
    REMOTE="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}"
    echo "[week6] ECR Remote: $REMOTE"

    # 确认/创建仓库（若已存在会跳过）
    aws ecr describe-repositories --repository-names "$ECR_REPO" --region "$AWS_REGION" >/dev/null 2>&1 \
      || aws ecr create-repository --repository-name "$ECR_REPO" --image-tag-mutability IMMUTABLE --region "$AWS_REGION"
    # 登录
    aws ecr get-login-password --region "$AWS_REGION" | docker login --username AWS --password-stdin "${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

    # 输出：
    [week6] ECR Image Tag: 0.1.0-2508272044
    [week6] ECR Remote: 563149051155.dkr.ecr.us-east-1.amazonaws.com/task-api
    Login Succeeded
    ```

4. **本地构建镜像（下例以 `linux/amd64` 为例，可按需替换）**：

    ```bash
    docker system prune -a
    docker build --platform=linux/amd64 -t "${APP}:${VERSION}" .
    docker images
    ```

5. 可使用 `docker run` 在本地冒烟：

    ```bash
    # 启动测试容器
    docker run -d -p 8080:8080 --name my-task "${APP}:${VERSION}"
    docker ps
    # 测试
    curl http://localhost:8080/actuator/health
    curl http://localhost:8080/actuator/prometheus
    # 完成后清理
    docker stop my-task && docker rm my-task
    docker ps -a
    ```

6. **推送到 ECR 并记录 digest**：

    ```bash
    docker tag "${APP}:${VERSION}" "${REMOTE}:${VERSION}"
    docker tag "${APP}:${VERSION}" "$REMOTE:latest"
    docker push "${REMOTE}:${VERSION}"
    # 推送成功会回显各层与 digest

    # 读取本次镜像的 digest
    DIGEST=$(aws ecr describe-images \
      --repository-name "$ECR_REPO" \
      --image-ids imageTag="$VERSION" \
      --query 'imageDetails[0].imageDigest' \
      --output text \
      --region "$AWS_REGION")
    echo "DIGEST=$DIGEST"
    # 更新子项目 task-api 的 scripts 记录
    printf 'export DIGEST=%s\n' "$DIGEST" > scripts/.last_image

    # 输出：
    0.1.0-2508272044: digest: sha256:d409d3f8925d75544f34edf9f0dbf8d772866b27609ef01826e1467fee52170a size: 1994
    DIGEST=sha256:d409d3f8925d75544f34edf9f0dbf8d772866b27609ef01826e1467fee52170a
    ```

    > 已检查最新推送的镜像大小为 95.93 MB

7. **给 ECR 镜像加新的 latest 标签**：

    ```bash
    # 拉取 ECR 镜像到本地
    docker system prune -a
    docker pull "${REMOTE}:${VERSION}"
    docker images
    # 打新标签
    docker tag "${REMOTE}:${VERSION}" "$REMOTE:latest"
    # 修改为 MUTABLE（需管理员权限）
    aws ecr put-image-tag-mutability \
    --repository-name $APP \
    --image-tag-mutability MUTABLE \
    --region $AWS_REGION
    # 推送 latest 标签
    docker push "$REMOTE:latest"
    # 改回 IMMUTABLE
    aws ecr put-image-tag-mutability \
    --repository-name $APP \
    --image-tag-mutability IMMUTABLE \
    --region $AWS_REGION
    # 本地清理
    docker system prune -a
    docker images -a
    docker ps -a
    ```

8. **更新部署引用**：

   - 将新的 digest 写入 `task-api/k8s/base/deploy-svc.yaml`。
   - 把最新的 tag `${VERSION}` 的值同步更新到 `scripts/post-recreate.sh` 的 `IMAGE_TAG` 的默认值中。
   - 可以在执行 `post-recreate.sh` 时通过 `IMAGE_TAG`/`IMAGE_DIGEST` 传入。

9. **用 digest 更新集群中的 Deployment 并等待滚动完成**：

    ```bash
    kubectl -n "$NS" set image deploy/"$APP" "$APP"="${REMOTE}@${DIGEST}" --record
    kubectl -n "$NS" rollout status deploy/"$APP" --timeout=180s
    kubectl -n "$NS" get deploy,po -o wide
    ```

10. **集群内冒烟（`/actuator/health` 与 `/actuator/prometheus`）**：

    ```bash
    # 临时起个调试 Pod （会自动删除）并使用内网域名进行测试
    kubectl -n "$NS" run curl \
      --image=curlimages/curl:8.10.1 -it --rm -- sh -lc 'curl -sf http://task-api.svc-task.svc.cluster.local:8080/actuator/health && echo && curl -sf http://task-api.svc-task.svc.cluster.local:8080/actuator/prometheus | head'
    # 检查和清理
    kubectl -n "$NS" get pods
    kubectl -n "$NS" get services
    kubectl -n "$NS" delete service curl
    kubectl -n "$NS" delete pod curl
    ```

---

## 第六步：最小连通性预检（AMP remote write ↔ 集群出网 ↔ 新镜像就绪）

**目标**：

1. 验证集群能连到 AMP 的 `remote_write`（DNS/TLS/出网 OK，预期返回 403/400，因未做 SigV4）；
2. 再次确认 `task-api` 的 `/actuator/prometheus` 在集群内可读；
3. 预取 Day 2 需要的 **OIDC/Policy** 信息（只读检查，不做变更）。

### 变量与 remote write

```bash
AMP_ENDPOINT=$(
  aws amp describe-workspace \
    --region "$AWS_REGION" \
    --workspace-id "$AMP_WORKSPACE_ID" \
    --query 'workspace.prometheusEndpoint' --output text
)
REMOTE_WRITE="${AMP_ENDPOINT}api/v1/remote_write"
echo "[week6] remote_write = $REMOTE_WRITE"
```

输出：

```bash
[week6] remote_write = https://aps-workspaces.us-east-1.amazonaws.com/workspaces/ws-4c9b04d5-5e49-415e-90ef-747450304dca/api/v1/remote_write
```

### 从集群内做 DNS/TLS 出网预检

预期 HTTP 403/400，代表网络就绪但未签名。

```bash
kubectl -n "$NS" run amp-netcheck --rm -it --restart=Never --image=curlimages/curl:8.10.1 -- \
  sh -lc "nslookup aps-workspaces.${AWS_REGION}.amazonaws.com || getent hosts aps-workspaces.${AWS_REGION}.amazonaws.com; \
          echo '== POST remote_write (expect 403/400) =='; \
          curl -s -o /dev/null -w 'HTTP %{http_code}\n' -X POST '$REMOTE_WRITE'"
```

输出：

```bash
Server:         172.20.0.10
Address:        172.20.0.10:53

Non-authoritative answer:

Non-authoritative answer:
Name:   aps-workspaces.us-east-1.amazonaws.com
Address: 54.197.120.175
Name:   aps-workspaces.us-east-1.amazonaws.com
Address: 3.216.243.210
Name:   aps-workspaces.us-east-1.amazonaws.com
Address: 44.216.31.204
Name:   aps-workspaces.us-east-1.amazonaws.com
Address: 3.221.177.188
Name:   aps-workspaces.us-east-1.amazonaws.com
Address: 98.83.214.158
Name:   aps-workspaces.us-east-1.amazonaws.com
Address: 50.19.192.67
Name:   aps-workspaces.us-east-1.amazonaws.com
Address: 98.83.89.149
Name:   aps-workspaces.us-east-1.amazonaws.com
Address: 54.208.94.103

== POST remote_write (expect 403/400) ==
HTTP 403
pod "amp-netcheck" deleted
```

### 确认新镜像的指标端点在集群内可读

```bash
kubectl -n "$NS" run curl --image=curlimages/curl:8.10.1 -it --rm -- \
  sh -lc 'curl -sf http://task-api:8080/actuator/prometheus | head'
```

输出：

```bash
# HELP application_ready_time_seconds Time taken for the application to be ready to service requests
# TYPE application_ready_time_seconds gauge
application_ready_time_seconds{application="task-api",main_application_class="com.renda.task.TaskApiApplication"} 17.851
# HELP application_started_time_seconds Time taken to start the application
# TYPE application_started_time_seconds gauge
application_started_time_seconds{application="task-api",main_application_class="com.renda.task.TaskApiApplication"} 17.391
# HELP disk_free_bytes Usable space for path
# TYPE disk_free_bytes gauge
disk_free_bytes{application="task-api",path="/app/."} 1.711149056E10
# HELP disk_total_bytes Total space for path
pod "curl" deleted
```

### 只读前置：取 EKS 集群名、OIDC Issuer、以及 AWS 托管策略 ARN

```bash
CTX_CLUSTER=$(kubectl config view --minify -o jsonpath='{.contexts[0].context.cluster}')
CLUSTER=${CTX_CLUSTER##*/}
ISSUER=$(aws eks describe-cluster --name "$CLUSTER" --region "$AWS_REGION" --query 'cluster.identity.oidc.issuer' --output text)
PROVIDER_HOSTPATH=${ISSUER#https://}

# 是否已存在与该 Issuer 匹配的 IAM OIDC Provider（仅检查，不改动）
FOUND=$(aws iam list-open-id-connect-providers --query 'OpenIDConnectProviderList[*].Arn' --output text \
  | tr '\t' '\n' \
  | while read arn; do aws iam get-open-id-connect-provider --open-id-connect-provider-arn "$arn" --query 'Url' --output text; done \
  | grep -c "$PROVIDER_HOSTPATH" || true)

REMOTE_WRITE_POLICY_ARN=$(aws iam list-policies --scope AWS --query "Policies[?PolicyName=='AmazonPrometheusRemoteWriteAccess'].Arn" --output text)

echo "[week6] EKS cluster: $CLUSTER"
echo "[week6] OIDC issuer : $ISSUER"
if [ "$FOUND" -ge 1 ]; then echo "[week6] IAM OIDC provider: PRESENT"; else echo "[week6] IAM OIDC provider: MISSING (we'll create on Day 2)"; fi
echo "[week6] AmazonPrometheusRemoteWriteAccess ARN: $REMOTE_WRITE_POLICY_ARN"
```

输出：

```bash
[week6] EKS cluster: dev
[week6] OIDC issuer : https://oidc.eks.us-east-1.amazonaws.com/id/4A580B5B467656AA8A2E18C0238FBC3A
[week6] IAM OIDC provider: PRESENT
[week6] AmazonPrometheusRemoteWriteAccess ARN: arn:aws:iam::aws:policy/AmazonPrometheusRemoteWriteAccess
```

---
