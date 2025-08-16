<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Stage 2 Week 5 Day 2 - K8s 基础对象（NS/SA/Config/Secret/Deployment/Service）](#stage-2-week-5-day-2---k8s-%E5%9F%BA%E7%A1%80%E5%AF%B9%E8%B1%A1nssaconfigsecretdeploymentservice)
  - [目标](#%E7%9B%AE%E6%A0%87)
  - [Step 1/5 - 规范化 `k8s/base`（并以 digest 锁定镜像）](#step-15---%E8%A7%84%E8%8C%83%E5%8C%96-k8sbase%E5%B9%B6%E4%BB%A5-digest-%E9%94%81%E5%AE%9A%E9%95%9C%E5%83%8F)
    - [目录与变量](#%E7%9B%AE%E5%BD%95%E4%B8%8E%E5%8F%98%E9%87%8F)
    - [Namespace + ServiceAccount（预留 IRSA 注解位）](#namespace--serviceaccount%E9%A2%84%E7%95%99-irsa-%E6%B3%A8%E8%A7%A3%E4%BD%8D)
    - [ConfigMap（按需给应用注入非敏感配置）](#configmap%E6%8C%89%E9%9C%80%E7%BB%99%E5%BA%94%E7%94%A8%E6%B3%A8%E5%85%A5%E9%9D%9E%E6%95%8F%E6%84%9F%E9%85%8D%E7%BD%AE)
    - [Deployment + Service（以 **digest** 锁镜像）](#deployment--service%E4%BB%A5-digest-%E9%94%81%E9%95%9C%E5%83%8F)
    - [渲染模板和应用](#%E6%B8%B2%E6%9F%93%E6%A8%A1%E6%9D%BF%E5%92%8C%E5%BA%94%E7%94%A8)
      - [渲染模板](#%E6%B8%B2%E6%9F%93%E6%A8%A1%E6%9D%BF)
      - [应用并覆盖昨天的临时资源](#%E5%BA%94%E7%94%A8%E5%B9%B6%E8%A6%86%E7%9B%96%E6%98%A8%E5%A4%A9%E7%9A%84%E4%B8%B4%E6%97%B6%E8%B5%84%E6%BA%90)
    - [快速冒烟（集群内）](#%E5%BF%AB%E9%80%9F%E5%86%92%E7%83%9F%E9%9B%86%E7%BE%A4%E5%86%85)
  - [Step 2/5 - IRSA 用 Terraform，ALB Controller 用 Helm（进 `post-recreate.sh`）](#step-25---irsa-%E7%94%A8-terraformalb-controller-%E7%94%A8-helm%E8%BF%9B-post-recreatesh)
    - [Terraform：创建 IRSA（Role + Policy + SA 注解）](#terraform%E5%88%9B%E5%BB%BA-irsarole--policy--sa-%E6%B3%A8%E8%A7%A3)
      - [准备官方策略（放文件，便于以后升级替换）](#%E5%87%86%E5%A4%87%E5%AE%98%E6%96%B9%E7%AD%96%E7%95%A5%E6%94%BE%E6%96%87%E4%BB%B6%E4%BE%BF%E4%BA%8E%E4%BB%A5%E5%90%8E%E5%8D%87%E7%BA%A7%E6%9B%BF%E6%8D%A2)
      - [HCL 代码（IRSA + SA 注解）](#hcl-%E4%BB%A3%E7%A0%81irsa--sa-%E6%B3%A8%E8%A7%A3)
      - [使用 Terraform 执行变更：](#%E4%BD%BF%E7%94%A8-terraform-%E6%89%A7%E8%A1%8C%E5%8F%98%E6%9B%B4)
    - [Helm 安装/升级 + CRDs：更新 `post-recreate.sh`](#helm-%E5%AE%89%E8%A3%85%E5%8D%87%E7%BA%A7--crds%E6%9B%B4%E6%96%B0-post-recreatesh)
    - [验证](#%E9%AA%8C%E8%AF%81)
  - [Step 3/5 — 创建 Ingress（生成 ALB）+ 公网验证 + 写入 `post-recreate.sh`](#step-35--%E5%88%9B%E5%BB%BA-ingress%E7%94%9F%E6%88%90-alb-%E5%85%AC%E7%BD%91%E9%AA%8C%E8%AF%81--%E5%86%99%E5%85%A5-post-recreatesh)
    - [预检查（子网标签是否 OK）](#%E9%A2%84%E6%A3%80%E6%9F%A5%E5%AD%90%E7%BD%91%E6%A0%87%E7%AD%BE%E6%98%AF%E5%90%A6-ok)
    - [写 Ingress 清单](#%E5%86%99-ingress-%E6%B8%85%E5%8D%95)
    - [等待 ALB 分配地址并冒烟验证](#%E7%AD%89%E5%BE%85-alb-%E5%88%86%E9%85%8D%E5%9C%B0%E5%9D%80%E5%B9%B6%E5%86%92%E7%83%9F%E9%AA%8C%E8%AF%81)
    - [把 Ingress 发布与等待写进 `post-recreate.sh`](#%E6%8A%8A-ingress-%E5%8F%91%E5%B8%83%E4%B8%8E%E7%AD%89%E5%BE%85%E5%86%99%E8%BF%9B-post-recreatesh)

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
     - Terraform 仅负责 IAM 角色与 ServiceAccount；
     - 控制器本身通过 **post-recreate.sh** 的 Helm 安装。

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

- 今日所有改动应当 **可被每日重建** 自动复现（`make start-all`），并在每日销毁时完整清理（`make stop-all`）。
- 继续坚持 **用 ECR digest 部署**，避免 tag 漂移，使重建后行为稳定、便于回滚。

---

## Step 1/5 - 规范化 `k8s/base`（并以 digest 锁定镜像）

目标：把昨天的临时清单重构为可复用的目录结构，并用 **ECR digest** 固定镜像，便于在 `post-recreate.sh` 里一键重放。

### 目录与变量

```bash
WORK_DIR=/mnt/d/0Repositories/CloudNative
mkdir -p ${WORK_DIR}/task-api/k8s/base
cd ${WORK_DIR}/task-api

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

> 现在先不填 `role-arn`，避免无意义的 403；等明天做 IRSA 时再打开。

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

### 渲染模板和应用

#### 渲染模板

> macOS/Linux 如无 `envsubst` 也可用 `sed`，两种都给出。
> 任选其一执行即可。

- **方式 A：envsubst**
    ```bash
    cd ${WORK_DIR}/task-api
    envsubst < k8s/base/deploy-svc.tmpl.yaml > k8s/base/deploy-svc.yaml
    ```
- **方式 B：sed**
    ```bash
    cd ~/work/task-api
    sed -e "s|\${APP}|${APP}|g" \
        -e "s|\${NS}|${NS}|g" \
        -e "s|\${REMOTE}|${REMOTE}|g" \
        -e "s|\${DIGEST}|${DIGEST}|g" \
    k8s/base/deploy-svc.tmpl.yaml > k8s/base/deploy-svc.yaml
    ```

#### 应用并覆盖昨天的临时资源

预览差异（对比昨天使用 `k8s.yaml` 部署的差异）：

```bash
kubectl -n "$NS" diff -f k8s/base/ns-sa.yaml
# 输出：
# diff -u -N /tmp/LIVE-2317581702/v1.Namespace..svc-task /tmp/MERGED-3486373042/v1.Namespace..svc-task
# --- /tmp/LIVE-2317581702/v1.Namespace..svc-task 2025-08-16 22:50:00.271411589 +0800
# +++ /tmp/MERGED-3486373042/v1.Namespace..svc-task       2025-08-16 22:50:00.271411589 +0800
# @@ -3,6 +3,7 @@
#  metadata:
#    creationTimestamp: "2025-08-16T14:39:42Z"
#    labels:
# +    app.kubernetes.io/part-of: task-platform
#      kubernetes.io/metadata.name: svc-task
#    name: svc-task
#    resourceVersion: "3314"
# diff -u -N /tmp/LIVE-2317581702/v1.ServiceAccount.svc-task.task-api /tmp/MERGED-3486373042/v1.ServiceAccount.svc-task.task-api
# --- /tmp/LIVE-2317581702/v1.ServiceAccount.svc-task.task-api    2025-08-16 22:50:01.131332891 +0800
# +++ /tmp/MERGED-3486373042/v1.ServiceAccount.svc-task.task-api  2025-08-16 22:50:01.131332891 +0800
# @@ -0,0 +1,9 @@
# +apiVersion: v1
# +kind: ServiceAccount
# +metadata:
# +  creationTimestamp: "2025-08-16T14:50:01Z"
# +  labels:
# +    app.kubernetes.io/name: task-api
# +  name: task-api
# +  namespace: svc-task
# +  uid: 19e1a327-2146-4208-a6b6-b31f2c542b55

kubectl -n "$NS" diff -f k8s/base/configmap.yaml
# 输出：
# diff -u -N /tmp/LIVE-2707704724/v1.ConfigMap.svc-task.task-api-config /tmp/MERGED-3458038461/v1.ConfigMap.svc-task.task-api-config
# --- /tmp/LIVE-2707704724/v1.ConfigMap.svc-task.task-api-config  2025-08-16 22:51:38.948653919 +0800
# +++ /tmp/MERGED-3458038461/v1.ConfigMap.svc-task.task-api-config        2025-08-16 22:51:38.948653919 +0800
# @@ -0,0 +1,10 @@
# +apiVersion: v1
# +data:
# +  APP_NAME: task-api
# +  WELCOME_MSG: hello from ${AWS_REGION}
# +kind: ConfigMap
# +metadata:
# +  creationTimestamp: "2025-08-16T14:51:38Z"
# +  name: task-api-config
# +  namespace: svc-task
# +  uid: a8a278e6-894d-49f8-9ffb-f0cd8ed613a7

kubectl -n "$NS" diff -f k8s/base/deploy-svc.yaml
# 输出
# diff -u -N /tmp/LIVE-173526345/apps.v1.Deployment.svc-task.task-api /tmp/MERGED-2415446233/apps.v1.Deployment.svc-task.task-api
# --- /tmp/LIVE-173526345/apps.v1.Deployment.svc-task.task-api    2025-08-16 22:51:57.986005507 +0800
# +++ /tmp/MERGED-2415446233/apps.v1.Deployment.svc-task.task-api 2025-08-16 22:51:57.986005507 +0800
# @@ -8,7 +8,9 @@
#      kubernetes.io/change-cause: kubectl set image deploy/task-api task-api=563149051155.dkr.ecr.us-east-1.amazonaws.com/task-api@sha256:927d20ca4cebedc14f81770e8e5e49259684723ba65b76e7c59f3003cc9a9741
#        --namespace=svc-task --record=true
#    creationTimestamp: "2025-08-16T14:40:33Z"
# -  generation: 2
# +  generation: 3
# +  labels:
# +    app: task-api
#    name: task-api
#    namespace: svc-task
#    resourceVersion: "3622"
# @@ -32,7 +34,10 @@
#          app: task-api
#      spec:
#        containers:
# -      - image: 563149051155.dkr.ecr.us-east-1.amazonaws.com/task-api@sha256:927d20ca4cebedc14f81770e8e5e49259684723ba65b76e7c59f3003cc9a9741
# +      - envFrom:
# +        - configMapRef:
# +            name: task-api-config
# +        image: 563149051155.dkr.ecr.us-east-1.amazonaws.com/task-api@sha256:927d20ca4cebedc14f81770e8e5e49259684723ba65b76e7c59f3003cc9a9741
#          imagePullPolicy: IfNotPresent
#          livenessProbe:
#            failureThreshold: 3
# @@ -72,7 +77,9 @@
#        restartPolicy: Always
#        schedulerName: default-scheduler
#        securityContext: {}
# -      terminationGracePeriodSeconds: 30
# +      serviceAccount: task-api
# +      serviceAccountName: task-api
# +      terminationGracePeriodSeconds: 20
#  status:
#    availableReplicas: 2
#    conditions:
# diff -u -N /tmp/LIVE-173526345/v1.Service.svc-task.task-api /tmp/MERGED-2415446233/v1.Service.svc-task.task-api
# --- /tmp/LIVE-173526345/v1.Service.svc-task.task-api    2025-08-16 22:51:58.725159956 +0800
# +++ /tmp/MERGED-2415446233/v1.Service.svc-task.task-api 2025-08-16 22:51:58.725159956 +0800
# @@ -5,6 +5,8 @@
#      kubectl.kubernetes.io/last-applied-configuration: |
#        {"apiVersion":"v1","kind":"Service","metadata":{"annotations":{},"name":"task-api","namespace":"svc-task"},"spec":{"ports":[{"name":"http","port":8080,"targetPort":8080}],"selector":{"app":"task-api"},"type":"ClusterIP"}}
#    creationTimestamp: "2025-08-16T14:40:34Z"
# +  labels:
# +    app: task-api
#    name: task-api
#    namespace: svc-task
#    resourceVersion: "3510"
```

- 同名（同 Kind / 同 Namespace）的 `kubectl apply` 会做增量 Patch 并触发 Deployment 的滚动更新，不需要先“暂停/下线”原实例。
- Kubernetes 会按策略边上线新 Pod 边下线旧 Pod，Service 会只把 **就绪（readiness=UP）** 的新 Pod 纳入负载，所以通常不会中断。

应用并等待滚动完成：

```bash
# 确保 kubeconfig 跟 aws 同步
aws eks update-kubeconfig --name "$CLUSTER" --region "$AWS_REGION" --profile "$PROFILE"
# 输出：
# Updated context arn:aws:eks:us-east-1:563149051155:cluster/dev in /home/renda/.kube/config

# 应用
kubectl -n "$NS" apply -f k8s/base/ns-sa.yaml
# 输出：
# namespace/svc-task configured
# serviceaccount/task-api created
kubectl -n "$NS" apply -f k8s/base/configmap.yaml
# 输出：
# configmap/task-api-config created
kubectl -n "$NS" apply -f k8s/base/deploy-svc.yaml
# 输出：
# deployment.apps/task-api configured
# service/task-api configured

# 观察滚动
kubectl -n "$NS" rollout status deploy/"$APP" --timeout=180s
# 输出：
# Waiting for deployment "task-api" rollout to finish: 1 out of 2 new replicas have been updated...
# Waiting for deployment "task-api" rollout to finish: 1 out of 2 new replicas have been updated...
# Waiting for deployment "task-api" rollout to finish: 1 out of 2 new replicas have been updated...
# Waiting for deployment "task-api" rollout to finish: 1 old replicas are pending termination...
# Waiting for deployment "task-api" rollout to finish: 1 old replicas are pending termination...
# deployment "task-api" successfully rolled out

kubectl -n "$NS" get deploy,svc -o wide
# 输出
# NAME                       READY   UP-TO-DATE   AVAILABLE   AGE   CONTAINERS   IMAGES                                                                                                                          SELECTOR
# deployment.apps/task-api   2/2     2            2           19m   task-api     563149051155.dkr.ecr.us-east-1.amazonaws.com/task-api@sha256:927d20ca4cebedc14f81770e8e5e49259684723ba65b76e7c59f3003cc9a9741   app=task-api

# NAME               TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)    AGE   SELECTOR
# service/task-api   ClusterIP   172.20.245.244   <none>        8080/TCP   19m   app=task-api
```

**预期：**

`rollout status` 成功；`task-api` 的 `IMAGE` 字段展示为 `…/${ECR_REPO}@sha256:…`（digest 形式），`READY`=2/2。

### 快速冒烟（集群内）

```bash
kubectl -n "$NS" port-forward svc/"$APP" 8080:8080 >/dev/null 2>&1 &
PF=$!; sleep 2
# 输出：
# [1] 109916

curl -s "http://127.0.0.1:8080/api/hello?name=Renda"; echo
# 输出：
# hello Renda

curl -s http://127.0.0.1:8080/actuator/health; echo
# 输出：
# {"status":"UP","groups":["liveness","readiness"]}

ps aux | grep kubectl
# 输出：
# renda     109916  0.0  0.1 1287760 48256 pts/0   Sl   23:05   0:00 kubectl -n svc-task port-forward svc/task-api 8080:8080

kill $PF >/dev/null 2>&1 || true
ps aux | grep kubectl
# 输出：
# [1]+  Terminated              kubectl -n "$NS" port-forward svc/"$APP" 8080:8080 > /dev/null 2>&1
```

---

## Step 2/5 - IRSA 用 Terraform，ALB Controller 用 Helm（进 `post-recreate.sh`）

**目标**：

1. 用 Terraform 创建 **ALBC 的 IRSA**（IAM Role + Policy + 绑定到 `kube-system/aws-load-balancer-controller` SA）；
2. 在 `post-recreate.sh` 里用 Helm 安装/升级 **AWS Load Balancer Controller**，并**等待就绪**；
3. 固定 Chart / 镜像版本 + 处理 **CRDs 升级**。

### Terraform：创建 IRSA（Role + Policy + SA 注解）

**目录建议**：把新增的 HCL 文件放到 `infra/aws/modules/irsa_albc` 目录下。

#### 准备官方策略（放文件，便于以后升级替换）

`infra/aws/modules/irsa_albc/policy.json`

> 内容使用 AWS 官方提供的 ALBC IAM Policy JSON（体量较大，这里不粘贴）。
> 第一次可以手动下载放入该路径；后续升级只需更新这个文件并 `terraform apply`。

#### HCL 代码（IRSA + SA 注解）

新增 `infra/aws/modules/irsa_albc/main.tf` 文件：

```hcl
// ---------------------------
// IRSA 模块：为 Kubernetes ServiceAccount 绑定 IAM 角色
// 用于 AWS Load Balancer Controller 访问 AWS API
// ---------------------------

resource "aws_iam_role" "aws_load_balancer_controller" {
  name        = var.name                                                            # IAM 角色名称
  description = "IRSA role for AWS Load Balancer Controller in ${var.cluster_name}" # 角色描述
  assume_role_policy = jsonencode(
    {
      Version = "2012-10-17"
      Statement = [
        {
          Action = "sts:AssumeRoleWithWebIdentity"
          Effect = "Allow"
          Principal = {
            Federated = var.oidc_provider_arn # EKS OIDC Provider ARN
          }
          Condition = {
            StringEquals = {
              "${var.oidc_provider_url_without_https}:sub" = "system:serviceaccount:${var.namespace}:${var.service_account_name}"
            }
          }
        }
      ]
    }
  )

  lifecycle {
    create_before_destroy = true # 先创建新角色再销毁旧角色
  }
}

# 创建 AWS Load Balancer Controller IAM 策略
resource "aws_iam_policy" "albc" {
  name        = "${var.cluster_name}-AWSLoadBalancerControllerPolicy"
  description = "Policy for AWS Load Balancer Controller"

  # 官方推荐的权限策略
  policy = file("${path.module}/policy.json")
}

resource "aws_iam_role_policy_attachment" "albc_attach" {
  role       = aws_iam_role.aws_load_balancer_controller.name # 关联的 IAM 角色
  policy_arn = aws_iam_policy.albc.arn                        # IAM 策略 ARN

  depends_on = [
    aws_iam_role.aws_load_balancer_controller,
    aws_iam_policy.albc
  ]

  lifecycle {
    create_before_destroy = true
  }
}
```

新增 `infra/aws/modules/irsa_albc/outputs.tf` 文件：

```hcl
// 输出 AWS Load Balancer Controller 所使用的 IAM 角色 ARN
output "albc_role_arn" {
  description = "IAM Role ARN for the AWS Load Balancer Controller"
  value       = aws_iam_role.aws_load_balancer_controller.arn
}
```

新增 `infra/aws/modules/irsa_albc/variables.tf` 文件：

```hcl
// AWS Load Balancer Controller IRSA 模块所需的变量定义
variable "name" {
  description = "Name of the IRSA Role"
  type        = string
}

variable "cluster_name" {
  description = "Name of the EKS cluster"
  type        = string
}

variable "namespace" {
  description = "K8s Namespace of the ServiceAccount"
  type        = string
}

variable "service_account_name" {
  description = "Name of the ServiceAccount in Kubernetes"
  type        = string
}

variable "oidc_provider_arn" {
  description = "ARN of the OIDC provider for the EKS cluster"
  type        = string
}

variable "oidc_provider_url_without_https" {
  description = "OIDC provider URL (without https://)"
  type        = string
}
```

新增 `infra/aws/modules/irsa_albc/versions.tf` 文件：

```hcl
// 模块使用的 Terraform 及 provider 版本
terraform {
  required_version = "~> 1.12"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }
}
```

更新 `infra/aws/main.tf`：

```hcl
# 新增如下内容

module "irsa_albc" {
  source                          = "./modules/irsa_albc"                      # IRSA 模块，为 ALBC 创建角色
  count                           = var.create_eks ? 1 : 0                     # 仅在创建 EKS 时启用
  name                            = var.albc_irsa_role_name                    # ALBC IAM 角色名称
  namespace                       = var.albc_namespace                         # ALBC 所在命名空间
  cluster_name                    = var.cluster_name                           # 集群名称
  service_account_name            = var.albc_service_account_name              # ALBC ServiceAccount 名称
  oidc_provider_arn               = module.eks.oidc_provider_arn               # OIDC Provider ARN
  oidc_provider_url_without_https = module.eks.oidc_provider_url_without_https # OIDC URL（无 https）
  depends_on                      = [module.eks]                               # 依赖 EKS 模块
}

resource "kubernetes_service_account" "aws_load_balancer_controller" {
  count = var.create_eks ? 1 : 0

  metadata {
    name      = var.albc_service_account_name
    namespace = var.albc_namespace
    annotations = {
      "eks.amazonaws.com/role-arn" = module.irsa_albc[0].albc_role_arn
    }
  }
}
```

更新 `infra/aws/outputs.tf`：

```hcl
# 新增如下内容
output "albc_role_arn" {
  description = "AWS Load Balancer Controller 使用的 IAM 角色 ARN"
  value       = var.create_eks ? module.irsa_albc[0].albc_role_arn : null
}
```

更新 `infra/aws/provider.tf`：

```hcl
# 新增如下内容
provider "kubernetes" {
  config_path = "~/.kube/config" # 与 helm 共用 kubeconfig
}
```

更新 `infra/aws/terraform.tfvars`：

```hcl
# 新增如下内容
# ALBC IRSA 配置
albc_irsa_role_name       = "aws-load-balancer-controller" # ALBC IRSA 角色名称
albc_service_account_name = "aws-load-balancer-controller" # ALBC ServiceAccount 名称
albc_namespace            = "kube-system"                  # ALBC 所在命名空间
```

更新 `infra/aws/variables.tf`：

```hcl
# 新增如下内容

variable "albc_irsa_role_name" {
  description = "Name of the IRSA role for AWS Load Balancer Controller"
  type        = string
  default     = "aws-load-balancer-controller"
}

variable "albc_service_account_name" {
  description = "Kubernetes ServiceAccount name for AWS Load Balancer Controller"
  type        = string
  default     = "aws-load-balancer-controller"
}

variable "albc_namespace" {
  description = "Namespace for AWS Load Balancer Controller ServiceAccount"
  type        = string
  default     = "kube-system"
}
```

更新 `infra/aws/versions.tf`，新增如下内容：

```hcl
terraform {
  required_version = "~> 1.12" # Terraform CLI 版本要求
  required_providers {
  ...
    # 新增 kubernetes
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.23"
    }
  ...
  }
}
```

#### 使用 Terraform 执行变更：

```bash
cd ${WORK_DIR}

terraform -chdir=infra/aws init -reconfigure
terraform -chdir=infra/aws apply -auto-approve -input=false \
        -var="region=us-east-1" \
        -var="create_nat=true" \
        -var="create_alb=true" \
        -var="create_eks=true"
# 输出
# Apply complete! Resources: 4 added, 0 changed, 0 destroyed.
# Outputs:
# alb_dns = "alb-demo-293119581.us-east-1.elb.amazonaws.com"
# albc_role_arn = "arn:aws:iam::563149051155:role/aws-load-balancer-controller"
# autoscaler_role_arn = "arn:aws:iam::563149051155:role/eks-cluster-autoscaler"
# private_subnet_ids = [
#   "subnet-0422bec13e7eec9e6",
#   "subnet-00630bdad3664ee18",
# ]
# public_subnet_ids = [
#   "subnet-066a65e68e06df5db",
#   "subnet-08ca22e6d15635564",
# ]
# vpc_id = "vpc-0b06ba5bfab99498b"
```

查看新建的 IAM Role：

```bash
# 使用命令检查角色的 ARN
aws iam list-roles \
  --query "Roles[?RoleName == 'aws-load-balancer-controller'].Arn" \
  --output text
# 输出：
# arn:aws:iam::563149051155:role/aws-load-balancer-controller

# 检查角色被授予的权限策略
aws iam list-attached-role-policies --role-name aws-load-balancer-controller
# 输出：
# {
#     "AttachedPolicies": [
#         {
#             "PolicyName": "dev-AWSLoadBalancerControllerPolicy",
#             "PolicyArn": "arn:aws:iam::563149051155:policy/dev-AWSLoadBalancerControllerPolicy"
#         }
#     ]
# }
```

查看 ServiceAccount 的详细信息：

```bash
kubectl -n kube-system describe serviceaccount aws-load-balancer-controller
# 输出：
# Name:                aws-load-balancer-controller
# Namespace:           kube-system
# Labels:              <none>
# Annotations:         eks.amazonaws.com/role-arn: arn:aws:iam::563149051155:role/aws-load-balancer-controller
# Image pull secrets:  <none>
# Mountable secrets:   <none>
# Tokens:              <none>
# Events:              <none>
```

已经确认：

- 生成了 IAM Role `aws-load-balancer-controller` 并附加策略 `dev-AWSLoadBalancerControllerPolicy`；
- `kube-system` 下出现了 SA `aws-load-balancer-controller`，并带有 `eks.amazonaws.com/role-arn` 注解。

### Helm 安装/升级 + CRDs：更新 `post-recreate.sh`

在 `scripts/post-recreate.sh` 里新增如下代码：

```sh
...

# AWS Load Balancer Controller settings
ALBC_CHART_NAME="aws-load-balancer-controller"
ALBC_RELEASE_NAME=${ALBC_CHART_NAME}
ALBC_SERVICE_ACCOUNT_NAME=${ALBC_CHART_NAME}
ALBC_CHART_VERSION="1.8.1"
ALBC_IMAGE_TAG="v2.8.1"
ALBC_IMAGE_REPO="602401143452.dkr.ecr.${REGION}.amazonaws.com/amazon/aws-load-balancer-controller"
ALBC_HELM_REPO_NAME="eks"
ALBC_HELM_REPO_URL="https://aws.github.io/eks-charts"
POD_ALBC_LABEL="app.kubernetes.io/name=${ALBC_RELEASE_NAME}"

...

# 检查 AWS Load Balancer Controller 部署状态
check_albc_status() {
  if ! kubectl -n $KUBE_DEFAULT_NAMESPACE get deployment $ALBC_RELEASE_NAME >/dev/null 2>&1; then
    echo "missing"
    return
  fi
  if kubectl -n $KUBE_DEFAULT_NAMESPACE get pod -l $POD_ALBC_LABEL \
      --no-headers 2>/dev/null | grep -v Running >/dev/null; then
    echo "unhealthy"
  else
    echo "healthy"
  fi
}

...

# 安装或升级 AWS Load Balancer Controller
install_albc_controller() {
  local status
  status=$(check_albc_status)
  case "$status" in
    healthy)
      log "✅ AWS Load Balancer Controller 已正常运行, 执行 Helm 升级以确保版本一致"
      ;;
    missing)
      log "⚙️  检测到 AWS Load Balancer Controller 未部署, 开始安装"
      ;;
    unhealthy)
      log "❌ 检测到 AWS Load Balancer Controller 状态异常, 删除旧 Pod 后重新部署"
      kubectl -n $KUBE_DEFAULT_NAMESPACE delete pod -l $POD_ALBC_LABEL --ignore-not-found
      ;;
    *)
      log "⚠️  未知的 AWS Load Balancer Controller 状态, 继续尝试安装"
      ;;
  esac

  if ! helm repo list | grep -q "^${ALBC_HELM_REPO_NAME}\b"; then
    log "🔧 添加 ${ALBC_HELM_REPO_NAME} Helm 仓库"
    helm repo add ${ALBC_HELM_REPO_NAME} ${ALBC_HELM_REPO_URL}
  fi
  helm repo update

  log "📦 应用 AWS Load Balancer Controller CRDs (version ${ALBC_CHART_VERSION})"
  tmp_dir=$(mktemp -d)
  helm pull ${ALBC_HELM_REPO_NAME}/${ALBC_CHART_NAME} --version ${ALBC_CHART_VERSION} --untar -d "$tmp_dir"
  kubectl apply -f "$tmp_dir/${ALBC_CHART_NAME}/crds"
  rm -rf "$tmp_dir"

  VPC_ID=$(aws eks describe-cluster --name "$CLUSTER_NAME" --region "$REGION" --profile "$PROFILE" --query "cluster.resourcesVpcConfig.vpcId" --output text)

  log "🚀 正在通过 Helm 安装或升级 AWS Load Balancer Controller..."
  helm upgrade --install ${ALBC_RELEASE_NAME} ${ALBC_HELM_REPO_NAME}/${ALBC_CHART_NAME} \
    -n $KUBE_DEFAULT_NAMESPACE \
    --version ${ALBC_CHART_VERSION} \
    --set clusterName=$CLUSTER_NAME \
    --set region=$REGION \
    --set vpcId=$VPC_ID \
    --set serviceAccount.create=false \
    --set serviceAccount.name=${ALBC_SERVICE_ACCOUNT_NAME} \
    --set image.repository=${ALBC_IMAGE_REPO} \
    --set image.tag=${ALBC_IMAGE_TAG}

  log "🔍 等待 AWS Load Balancer Controller 就绪"
  kubectl -n $KUBE_DEFAULT_NAMESPACE rollout status deployment/${ALBC_RELEASE_NAME} --timeout=180s
  kubectl -n $KUBE_DEFAULT_NAMESPACE get pod -l $POD_ALBC_LABEL
}

...

# 进行基础资源检查
perform_health_checks() {
  ...
  log "🔍 检查 AWS Load Balancer Controller 部署状态"
  albc_status=$(check_albc_status)
  log "AWS Load Balancer Controller status: $albc_status"
  ...
}

...

install_albc_controller
```

已经确保：

* **版本固定**：`ALBC_CHART_VERSION` 与 `ALBC_IMAGE_TAG` 固定，确保每日重建结果一致。
* **CRDs 先行**：`kubectl apply -f "$tmp_dir/${ALBC_CHART_NAME}/crds"` 解决升级场景下 CRDs 不更新的问题。
* **不创建 SA**：`serviceAccount.create=false`，避免与 Terraform 管理的 SA 冲突。
* **就绪等待**：`rollout status` 保障后续 Ingress 创建不“撞墙”。

### 验证

执行 `bash scripts/post-recreate.sh` 完成控制器安装。

验证：

```bash
kubectl -n kube-system rollout status deploy/aws-load-balancer-controller
kubectl -n kube-system get deploy aws-load-balancer-controller
kubectl -n kube-system get pod -l app.kubernetes.io/name=aws-load-balancer-controller
kubectl -n kube-system logs deploy/aws-load-balancer-controller | tail -n 100
```

验证结果：

Deployment 可用副本就绪，日志结尾无报错（若有子网标签/权限问题，日志会即时提示）。

```bash
$ kubectl -n kube-system logs deploy/aws-load-balancer-controller | grep error
Found 2 pods, using pod/aws-load-balancer-controller-8574d469c6-b4cr9
$ kubectl -n kube-system logs deploy/aws-load-balancer-controller | grep warning
Found 2 pods, using pod/aws-load-balancer-controller-8574d469c6-b4cr9
W0816 20:21:19.838633       1 warnings.go:70] v1 Endpoints is deprecated in v1.33+; use discovery.k8s.io/v1 EndpointSlice
W0816 20:21:19.840303       1 warnings.go:70] v1 Endpoints is deprecated in v1.33+; use discovery.k8s.io/v1 EndpointSlice
W0816 20:28:47.843007       1 warnings.go:70] v1 Endpoints is deprecated in v1.33+; use discovery.k8s.io/v1 EndpointSlice
W0816 20:34:04.845735       1 warnings.go:70] v1 Endpoints is deprecated in v1.33+; use discovery.k8s.io/v1 EndpointSlice
```

---

## Step 3/5 — 创建 Ingress（生成 ALB）+ 公网验证 + 写入 `post-recreate.sh`

目标：

用 **AWS Load Balancer Controller** 为 `task-api` 生成公网 **ALB**，健康检查走 `/actuator/health/readiness`，并把这一步自动化进你的每日重建脚本。

### 预检查（子网标签是否 OK）

```bash
export PROFILE=phase2-sso
export AWS_PROFILE=$PROFILE
export AWS_REGION=us-east-1
export CLUSTER=dev

VPC_ID=$(aws eks describe-cluster --name "$CLUSTER" --region "$AWS_REGION" \
  --query 'cluster.resourcesVpcConfig.vpcId' --output text)

aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPC_ID" \
  --query 'Subnets[].{Id:SubnetId,Pub:MapPublicIpOnLaunch,Tags:Tags}' --output table

# 看看公有子网是否有 kubernetes.io/role/elb=1、私有子网是否有 kubernetes.io/role/internal-elb=1
# 以及所有参与子网有 kubernetes.io/cluster/dev = shared/owned
```

### 写 Ingress 清单

> 文件：`${WORK_DIR}/task-api/k8s/ingress.yaml`

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: task-api
  namespace: svc-task
  annotations:
    alb.ingress.kubernetes.io/scheme: internet-facing      # 公网 ALB；若走内网改: internal
    alb.ingress.kubernetes.io/target-type: ip              # 直连 Pod（推荐）
    alb.ingress.kubernetes.io/healthcheck-path: /actuator/health/readiness
    alb.ingress.kubernetes.io/healthcheck-port: traffic-port
    alb.ingress.kubernetes.io/listen-ports: '[{"HTTP":80}]'
    # 如需 X-Forwarded-* 保留：
    alb.ingress.kubernetes.io/load-balancer-attributes: routing.http.xff_header_processing.mode=preserve
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

应用：

```bash
kubectl apply -f "${WORK_DIR}/task-api/k8s/ingress.yaml"
# 输出：
# ingress.networking.k8s.io/task-api created
kubectl -n svc-task get ingress task-api
# 输出：
# NAME       CLASS   HOSTS   ADDRESS                                                                PORTS   AGE
# task-api   alb     *       k8s-svctask-taskapi-c91e97499e-281967989.us-east-1.elb.amazonaws.com   80      13s
```

### 等待 ALB 分配地址并冒烟验证

```bash
# 等待 ALB DNS 出现
for i in {1..30}; do
  ALB_DNS=$(kubectl -n svc-task get ing task-api -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')
  [[ -n "$ALB_DNS" ]] && echo "ALB_DNS=$ALB_DNS" && break
  echo "waiting ALB..."; sleep 10
done
[[ -z "$ALB_DNS" ]] && echo "ALB 未就绪，请检查 Controller/子网标签/权限" && exit 1

# 输出：
# ALB_DNS=k8s-svctask-taskapi-c91e97499e-281967989.us-east-1.elb.amazonaws.com

# 冒烟
curl -s "http://$ALB_DNS/api/hello?name=Renda"; echo
# 输出：
# hello Renda
curl -s "http://$ALB_DNS/actuator/health"; echo
# 输出：
# {"status":"UP","groups":["liveness","readiness"]}
curl -sI "http://$ALB_DNS/" | sed -n '1,10p'
# 输出：
HTTP/1.1 404
Date: Sat, 16 Aug 2025 21:36:23 GMT
Content-Type: application/json
Connection: keep-alive
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
```

**预期：**

`/api/hello` 返回 `hello Renda`，健康检查 `{"status":"UP"}`；`curl -I` 首行 `HTTP/1.1 200 OK` 或 `HTTP/1.1 404` 或 302 取决于根路径是否有资源，但重要的是可达）。

### 把 Ingress 发布与等待写进 `post-recreate.sh`

> 新增以下函数与调用：

```bash
# ---- Ingress for task-api ----
ING_FILE="${ROOT_DIR}/task-api/k8s/ingress.yaml"

# 部署 taskapi ingress
deploy_taskapi_ingress() {
  set -euo pipefail
  local outdir="${SCRIPT_DIR}/.out"; mkdir -p "$outdir"

  log "📦 Apply Ingress (${APP}) ..."
  # 若无变更就不 apply（0=无差异，1=有差异，>1=出错）
  if kubectl -n "$NS" diff -f "$ING_FILE" >/dev/null 2>&1; then
    log "≡ No changes"
  else
    kubectl apply -f "$ING_FILE"
  fi

  # 如果已经有 ALB，就直接复用并返回
  local dns
  dns=$(kubectl -n "$NS" get ing "$APP" -o jsonpath='{.status.loadBalancer.ingress[0].hostname}' 2>/dev/null || true)
  if [[ -n "${dns}" ]]; then
    log "✅ ALB ready: http://${dns}"
    echo "${dns}" > "${outdir}/alb_${APP}_dns"
    return 0
  fi

  log "⏳ Waiting for ALB to be provisioned ..."
  local t=0; local timeout=600
  while [[ $t -lt $timeout ]]; do
    dns=$(kubectl -n "$NS" get ing "$APP" -o jsonpath='{.status.loadBalancer.ingress[0].hostname}' 2>/dev/null || true)
    [[ -n "${dns}" ]] && break
    sleep 5; t=$((t+5))
  done
  [[ -z "${dns}" ]] && { log "❌ Timeout waiting ALB"; return 1; }

  log "✅ ALB ready: http://${dns}"
  echo "${dns}" > "${outdir}/alb_${APP}_dns"

  log "🧪 Smoke"
  curl -s "http://${dns}/api/hello?name=Renda" | sed -n '1p'
  curl -s "http://${dns}/actuator/health" | sed -n '1p'
}

# 在脚本主流程合适位置调用（在 ALBC 安装完成之后）
deploy_taskapi_ingress
```

> 提醒：若后续要启用 HTTPS，只需在 Ingress 上加证书 ARN：
>
> `alb.ingress.kubernetes.io/certificate-arn: arn:aws:acm:us-east-1:<ACCOUNT_ID>:certificate/<ID>`
>
> 并把 `listen-ports` 改为 `[{"HTTP":80},{"HTTPS":443}]`，再在 `spec.tls` 中声明主机名。

---
