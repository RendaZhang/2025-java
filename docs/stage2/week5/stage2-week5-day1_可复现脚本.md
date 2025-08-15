<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 1 全流程可复现脚本](#day-1-%E5%85%A8%E6%B5%81%E7%A8%8B%E5%8F%AF%E5%A4%8D%E7%8E%B0%E8%84%9A%E6%9C%AC)
  - [`scripts/00.env.sh`](#scripts00envsh)
  - [`scripts/build_push.sh`](#scriptsbuild_pushsh)
  - [`scripts/deploy.sh`](#scriptsdeploysh)
  - [`scripts/smoke.sh`](#scriptssmokesh)
  - [`scripts/cleanup_day1.sh`](#scriptscleanup_day1sh)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 1 全流程可复现脚本

> 用法（一次性）：
>
> ```bash
> mkdir -p scripts && cd scripts
> # 把下方 5 个脚本分别保存为对应文件，并 chmod +x
> chmod +x 00.env.sh build_push.sh deploy.sh smoke.sh cleanup_day1.sh
> ```
>
> 之后每次复现：
>
> ```bash
> ./00.env.sh          # 载入环境变量（会设置 AWS_PROFILE）
> ./build_push.sh      # 构建 + 推送镜像（会输出/刷新最新 DIGEST）
> ./deploy.sh          # 生成 YAML（锁定 digest）并部署
> ./smoke.sh           # 端到端验证（自动 port-forward + curl）
> # 收尾（仅移除 K8s 当天产物，不删 ECR 镜像）
> ./cleanup_day1.sh
> ```

## `scripts/00.env.sh`

```bash
#!/usr/bin/env bash
set -euo pipefail

# —— 基础参数（按需修改） ——
export PROFILE="phase2-sso"
export AWS_REGION="us-east-1"
export CLUSTER="dev"
export NS="svc-task"
export APP="task-api"
export ECR_REPO="task-api"
export VERSION="0.1.0"
export BUILD_PLATFORM="linux/amd64"   # 如节点为 Graviton，改为 linux/arm64

# —— 统一导出 AWS_PROFILE，省去每条命令 --profile ——
export AWS_PROFILE="$PROFILE"

# —— 账户/Registry 信息 ——
ACCOUNT_ID="$(aws sts get-caller-identity --query Account --output text)"
export ACCOUNT_ID
export REGISTRY="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
export REMOTE="${REGISTRY}/${ECR_REPO}"
export IMAGE_TAG="${VERSION}"
export IMAGE_URI="${REMOTE}:${IMAGE_TAG}"

echo "[env] ACCOUNT_ID=${ACCOUNT_ID}"
echo "[env] IMAGE_URI=${IMAGE_URI}"
```

## `scripts/build_push.sh`

```bash
#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=./00.env.sh
source "${SCRIPT_DIR}/00.env.sh"

cd "${SCRIPT_DIR}/.."

# 1) 登录 ECR
aws ecr describe-repositories --repository-names "${ECR_REPO}" >/dev/null 2>&1 \
  || aws ecr create-repository --repository-name "${ECR_REPO}" --image-tag-mutability IMMUTABLE --image-scanning-configuration scanOnPush=true >/dev/null
aws ecr get-login-password | docker login --username AWS --password-stdin "${REGISTRY}"

# 2) 构建可执行 JAR（按你当前项目结构，根目录就是 Maven 工程）
mvn -q -B -DskipTests package

# 3) 构建镜像（复用你的 Dockerfile；指定平台以匹配节点架构）
docker build --platform="${BUILD_PLATFORM}" -t "${IMAGE_URI}" .

# 4) 推送 :VERSION 与 :latest
docker tag "${IMAGE_URI}" "${REMOTE}:latest"
docker push "${IMAGE_URI}"
docker push "${REMOTE}:latest"

# 5) 取回 digest（优先按 VERSION，失败退回 latest）
DIGEST="$(aws ecr describe-images \
  --repository-name "${ECR_REPO}" \
  --image-ids imageTag="${IMAGE_TAG}" \
  --query 'imageDetails[0].imageDigest' --output text 2>/dev/null || echo "")"
if [[ -z "${DIGEST}" || "${DIGEST}" == "None" ]]; then
  DIGEST="$(aws ecr describe-images \
    --repository-name "${ECR_REPO}" \
    --image-ids imageTag="latest" \
    --query 'imageDetails[0].imageDigest' --output text)"
fi

echo "DIGEST=${DIGEST}" | tee "${SCRIPT_DIR}/.last_image"
echo "[build_push] pushed: ${IMAGE_URI}"
echo "[build_push] digest: ${DIGEST}"
```

## `scripts/deploy.sh`

```bash
#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=./00.env.sh
source "${SCRIPT_DIR}/00.env.sh"

# 0) Kubeconfig & NS
aws eks update-kubeconfig --name "${CLUSTER}" --region "${AWS_REGION}"
kubectl get ns "${NS}" >/dev/null 2>&1 || kubectl create ns "${NS}"

# 1) 读取最新 DIGEST（可用 env DIGEST 覆盖）
if [[ -z "${DIGEST:-}" ]]; then
  if [[ -f "${SCRIPT_DIR}/.last_image" ]]; then
    # shellcheck disable=SC1090
    source "${SCRIPT_DIR}/.last_image"
  fi
fi
if [[ -z "${DIGEST:-}" ]]; then
  echo "[deploy] DIGEST 未找到，请先执行 ./build_push.sh 或手动导出 DIGEST" >&2
  exit 1
fi

# 2) 生成 YAML（以 digest 锁定镜像）
MANIFEST="$(mktemp)"
cat > "${MANIFEST}" <<YAML
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${APP}
  namespace: ${NS}
spec:
  replicas: 2
  revisionHistoryLimit: 3
  selector:
    matchLabels: { app: ${APP} }
  template:
    metadata:
      labels: { app: ${APP} }
    spec:
      containers:
      - name: ${APP}
        image: ${REMOTE}@${DIGEST}
        imagePullPolicy: IfNotPresent
        ports: [{ name: http, containerPort: 8080 }]
        resources:
          requests: { cpu: "100m", memory: "128Mi" }
          limits:   { cpu: "500m", memory: "512Mi" }
        readinessProbe:
          httpGet: { path: /actuator/health/readiness, port: 8080 }
          initialDelaySeconds: 5
          periodSeconds: 10
          timeoutSeconds: 2
          failureThreshold: 3
        livenessProbe:
          httpGet: { path: /actuator/health/liveness, port: 8080 }
          initialDelaySeconds: 10
          periodSeconds: 10
          timeoutSeconds: 2
          failureThreshold: 3
---
apiVersion: v1
kind: Service
metadata:
  name: ${APP}
  namespace: ${NS}
spec:
  type: ClusterIP
  selector: { app: ${APP} }
  ports:
    - name: http
      port: 8080
      targetPort: 8080
YAML

# 3) 应用并等待
kubectl apply -f "${MANIFEST}"
kubectl -n "${NS}" rollout status deploy/"${APP}" --timeout=180s
kubectl -n "${NS}" get deploy,svc -o wide
echo "[deploy] OK -> ${APP} on ns/${NS}, image digest=${DIGEST}"
```

## `scripts/smoke.sh`

```bash
#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=./00.env.sh
source "${SCRIPT_DIR}/00.env.sh"

aws eks update-kubeconfig --name "${CLUSTER}" --region "${AWS_REGION}"

# 后台启动 port-forward
kubectl -n "${NS}" port-forward svc/"${APP}" 8080:8080 >/dev/null 2>&1 &
PF_PID=$!
cleanup() { kill "${PF_PID}" >/dev/null 2>&1 || true; }
trap cleanup EXIT
sleep 2

set +e
echo "# GET /api/hello"
curl -s "http://127.0.0.1:8080/api/hello?name=Renda" ; echo
echo "# Health"
curl -s "http://127.0.0.1:8080/actuator/health" ; echo
echo "# Readiness"
curl -s "http://127.0.0.1:8080/actuator/health/readiness" ; echo
echo "# Liveness"
curl -s "http://127.0.0.1:8080/actuator/health/liveness" ; echo
set -e

echo "[smoke] OK"
```

## `scripts/cleanup_day1.sh`

```bash
#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=./00.env.sh
source "${SCRIPT_DIR}/00.env.sh"

aws eks update-kubeconfig --name "${CLUSTER}" --region "${AWS_REGION}"

echo "[cleanup] deleting K8s resources (Deployment/Service) in ns/${NS} ..."
kubectl -n "${NS}" delete deploy "${APP}" --ignore-not-found
kubectl -n "${NS}" delete svc   "${APP}" --ignore-not-found

# 如需彻底清空当天命名空间（谨慎使用）：
# kubectl delete ns "${NS}"
echo "[cleanup] done. (ECR 镜像保留以便回滚)"
```
