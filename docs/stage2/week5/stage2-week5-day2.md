<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Stage 2 Week 5 Day 2 - K8s 基础对象（NS/SA/Config/Secret/Deployment/Service）](#stage-2-week-5-day-2---k8s-%E5%9F%BA%E7%A1%80%E5%AF%B9%E8%B1%A1nssaconfigsecretdeploymentservice)
  - [目标](#%E7%9B%AE%E6%A0%87)
  - [Step 1/5 — 规范化 `k8s/base`（并以 digest 锁定镜像）](#step-15--%E8%A7%84%E8%8C%83%E5%8C%96-k8sbase%E5%B9%B6%E4%BB%A5-digest-%E9%94%81%E5%AE%9A%E9%95%9C%E5%83%8F)
    - [目录与变量](#%E7%9B%AE%E5%BD%95%E4%B8%8E%E5%8F%98%E9%87%8F)
    - [Namespace + ServiceAccount（预留 IRSA 注解位）](#namespace--serviceaccount%E9%A2%84%E7%95%99-irsa-%E6%B3%A8%E8%A7%A3%E4%BD%8D)
    - [ConfigMap（按需给应用注入非敏感配置）](#configmap%E6%8C%89%E9%9C%80%E7%BB%99%E5%BA%94%E7%94%A8%E6%B3%A8%E5%85%A5%E9%9D%9E%E6%95%8F%E6%84%9F%E9%85%8D%E7%BD%AE)
    - [Deployment + Service（以 **digest** 锁镜像）](#deployment--service%E4%BB%A5-digest-%E9%94%81%E9%95%9C%E5%83%8F)
    - [渲染模板并应用（会覆盖昨天的临时资源）](#%E6%B8%B2%E6%9F%93%E6%A8%A1%E6%9D%BF%E5%B9%B6%E5%BA%94%E7%94%A8%E4%BC%9A%E8%A6%86%E7%9B%96%E6%98%A8%E5%A4%A9%E7%9A%84%E4%B8%B4%E6%97%B6%E8%B5%84%E6%BA%90)
    - [快速冒烟（集群内）](#%E5%BF%AB%E9%80%9F%E5%86%92%E7%83%9F%E9%9B%86%E7%BE%A4%E5%86%85)
    - [常见小坑](#%E5%B8%B8%E8%A7%81%E5%B0%8F%E5%9D%91)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Stage 2 Week 5 Day 2 - K8s 基础对象（NS/SA/Config/Secret/Deployment/Service）

## 目标

1. **夯实 K8s 基础对象**
   - 把应用在集群内“站稳”的最小集合做规范化与落库：**Namespace / ServiceAccount（预留 IRSA 注解位）/ ConfigMap / Deployment / Service**，
   - 并把**readiness/liveness** 探针接到 Actuator 健康检查；清单落入仓库 `k8s/base/`，便于脚本自动应用。
2. **对外暴露与弹性（Day 3）**
   - 安装 **AWS Load Balancer Controller**，编写 **Ingress** 生成公网 **ALB**，健康检查走 `/actuator/health/readiness`；
   - 顺手加一个 **HPA（CPU=60%）** 做最小扩缩演示。
3. **纳入每日重建/销毁体系**
   - 所有新增内容都要能随着 `make start-all / stop-all` 循环重放：
     - 基础设施仍由 **Terraform** 管理；
     - 控制器与业务清单，通过 **post-recreate.sh** 自动部署与验活（或将 ALB Controller 以 Terraform/Helm 资源声明化；二选一我们稍后定）。

验收清单：

- `k8s/base/` 下**规范化**的：
  - `ns-sa.yaml`、`deploy-svc.yaml`（含探针/配置注入），
  - 以及 `k8s/ingress.yaml`、`k8s/hpa.yaml`。
- **ALB DNS** 可访问（主页/健康检查）与一次 `kubectl describe hpa` 输出。
- **post-recreate.sh**：
  - 追加/调整安装与发布逻辑，使它能 **自动更新 Deployment 的镜像为 ECR digest**、等待 `rollout`、在集群内做冒烟，
  - 并安装/升级 ALB Controller。
- 文档：在 `云原生计划` 的小节补齐“做什么/关键命令/产物”与退路记录。

补充：

- 今日所有改动应当**可被每日重建**自动复现（`make start-all`），并在每日销毁时完整清理（`make stop-all`）。
- 继续坚持 **用 ECR digest 部署**，避免 tag 漂移，使重建后行为稳定、便于回滚。

---

## Step 1/5 — 规范化 `k8s/base`（并以 digest 锁定镜像）

目标：把昨天的临时清单重构为可复用的目录结构，并用 **ECR digest** 固定镜像，便于在 `post-recreate.sh` 里一键重放。

### 目录与变量

```bash
mkdir -p ~/work/task-api/k8s/base
cd ~/work/task-api

# 你的环境（可直接复制）
export PROFILE=phase2-sso
export AWS_PROFILE=$PROFILE
export AWS_REGION=us-east-1
export CLUSTER=dev
export NS=svc-task
export APP=task-api
export ECR_REPO=task-api

# 账户与镜像 registry
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
export ACCOUNT_ID
export REMOTE="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}"

# 镜像 digest（优先用昨天脚本文件，否则就用你昨天给我的那串）
if [ -f scripts/.last_image ]; then source scripts/.last_image; fi
export DIGEST=${DIGEST:-sha256:927d20ca4cebedc14f81770e8e5e49259684723ba65b76e7c59f3003cc9a9741}
```

### Namespace + ServiceAccount（预留 IRSA 注解位）

`k8s/base/ns-sa.yaml`

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: svc-task
  labels:
    app.kubernetes.io/part-of: task-platform
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: task-api
  namespace: svc-task
  labels:
    app.kubernetes.io/name: task-api
# 预留 IRSA 注解位（Day 4 再填上 role-arn）
#  annotations:
#    eks.amazonaws.com/role-arn: arn:aws:iam::<ACCOUNT_ID>:role/<ROLE_NAME>
```

> 现在先不填 `role-arn`，避免无意义的 403；Day 4 做 IRSA 时再打开。

### ConfigMap（按需给应用注入非敏感配置）

`k8s/base/configmap.yaml`

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: task-api-config
  namespace: svc-task
data:
  APP_NAME: "task-api"
  WELCOME_MSG: "hello from ${AWS_REGION}"
```

### Deployment + Service（以 **digest** 锁镜像）

为便于脚本注入变量，这里先写成模板文件，再渲染。

`k8s/base/deploy-svc.tmpl.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${APP}
  namespace: ${NS}
  labels: { app: ${APP} }
spec:
  replicas: 2
  revisionHistoryLimit: 3
  selector:
    matchLabels: { app: ${APP} }
  template:
    metadata:
      labels: { app: ${APP} }
    spec:
      serviceAccountName: ${APP}
      terminationGracePeriodSeconds: 20
      containers:
        - name: ${APP}
          image: ${REMOTE}@${DIGEST}
          imagePullPolicy: IfNotPresent
          ports:
            - name: http
              containerPort: 8080
          envFrom:
            - configMapRef:
                name: task-api-config
          resources:
            requests:
              cpu: "100m"
              memory: "128Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
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
  labels: { app: ${APP} }
spec:
  type: ClusterIP
  selector: { app: ${APP} }
  ports:
    - name: http
      port: 8080
      targetPort: 8080
```

### 渲染模板并应用（会覆盖昨天的临时资源）

> macOS/Linux 如无 `envsubst` 也可用 `sed`，我两种都给出。任选其一执行即可。

**方式 A：envsubst**

```bash
cd ~/work/task-api
envsubst < k8s/base/deploy-svc.tmpl.yaml > k8s/base/deploy-svc.yaml
```

**方式 B：sed**

```bash
cd ~/work/task-api
sed -e "s|\${APP}|${APP}|g" \
    -e "s|\${NS}|${NS}|g" \
    -e "s|\${REMOTE}|${REMOTE}|g" \
    -e "s|\${DIGEST}|${DIGEST}|g" \
  k8s/base/deploy-svc.tmpl.yaml > k8s/base/deploy-svc.yaml
```

应用并等待滚动完成：

```bash
aws eks update-kubeconfig --name "$CLUSTER" --region "$AWS_REGION" --profile "$PROFILE"

kubectl apply -f k8s/base/ns-sa.yaml
kubectl apply -f k8s/base/configmap.yaml
kubectl apply -f k8s/base/deploy-svc.yaml

kubectl -n "$NS" rollout status deploy/"$APP" --timeout=180s
kubectl -n "$NS" get deploy,svc -o wide
```

**预期：** `rollout status` 成功；`task-api` 的 `IMAGE` 字段展示为 `…/${ECR_REPO}@sha256:…`（digest 形式），`READY`=2/2。

### 快速冒烟（集群内）

```bash
kubectl -n "$NS" port-forward svc/"$APP" 8080:8080 >/dev/null 2>&1 &
PF=$!; sleep 2
curl -s "http://127.0.0.1:8080/api/hello?name=Renda"; echo
curl -s http://127.0.0.1:8080/actuator/health; echo
kill $PF >/dev/null 2>&1 || true
```

### 常见小坑

* **`ImagePullBackOff`**：节点无 ECR 拉取权限/私有子网无 NAT；或你推的是 `arm64` 镜像但节点是 `amd64`。
* **`CrashLoopBackOff`**：应用启动慢/探针过于激进，调高 `initialDelaySeconds`。
* **`Forbidden`（SA/IRSA 注解报错）**：先别加 IRSA 注解，等 Day 4 对齐 IAM/角色信任策略再开。

---
