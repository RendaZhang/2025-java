# Day-0 · Docker & Kubernetes Refresher

## Play-with-Docker 快速实验

1. **登录沙盒**

   - 打开 [https://labs.play-with-docker.com/](https://labs.play-with-docker.com/)，用 **GitHub** 账号登录。
   - 页面右上角点 **+ Add New Instance**，启动 1 台默认 4 CPU / 8 GB Linux 主机。

1. **运行官方 Getting-Started 容器**
   在实例终端依次执行：

   ```bash
   docker run -d -p 80:80 docker/getting-started:pwd
   docker ps        # 确认容器正在运行
   ```

1. **验证 Web 输出**
   界面顶部会出现一个链接 **80**。点击它，浏览器应打开 “Getting Started with Docker” 页面。

1. **记录与保存**

- docker run：创建并启动容器。
- docker ps：查看正在运行的容器。
- docker images：查看本地存储的镜像。

## Killercoda Kubernetes Playground

1. **启动沙盒**

   - 打开 [https://killercoda.com/playgrounds/scenario/kubernetes](https://killercoda.com/playgrounds/scenario/kubernetes)（需开启 JavaScript）
   - 用 **GitHub** 登录后点击 **“Start Scenario”**，等左侧终端出现 `node01 ~$` 提示符。

1. **部署应用**

   ```bash
   controlplane:~$ kubectl create deployment hello-node --image=k8s.gcr.io/echoserver:1.4
   deployment.apps/hello-node created
   controlplane:~$ kubectl get deployments
   NAME         READY   UP-TO-DATE   AVAILABLE   AGE
   hello-node   0/1     1            0           8s
   controlplane:~$ kubectl get pods
   NAME                          READY   STATUS    RESTARTS   AGE
   hello-node-76998b7778-w4hz6   1/1     Running   0          15s
   ```

   - Deployment 会自动生成 ReplicaSet 来管理 Pod 副本。

1. **暴露服务**

   ```bash
   ontrolplane:~$ kubectl expose deployment hello-node --type=LoadBalancer --port=8080
   service/hello-node exposed
   controlplane:~$ kubectl get services
   NAME         TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
   hello-node   LoadBalancer   10.96.160.166   <pending>     8080:32312/TCP   14s
   kubernetes   ClusterIP      10.96.0.1       <none>        443/TCP          5d3h
   ```

   - Terminals 上方会显示一个 **Endpoint** 链接；点击应见 “Hello Kubernetes”。
   - 记下 `TYPE` 为 `LoadBalancer` 时外部 IP 的分配过程。

1. **扩容应用**

   ```bash
   controlplane:~$ kubectl scale deployment hello-node --replicas=4
   deployment.apps/hello-node scaled
   controlplane:~$ kubectl get pods
   NAME                          READY   STATUS              RESTARTS   AGE
   hello-node-76998b7778-2zq2q   1/1     Running             0          10s
   hello-node-76998b7778-bwvwg   0/1     ContainerCreating   0          10s
   hello-node-76998b7778-fsl4m   1/1     Running             0          10s
   hello-node-76998b7778-w4hz6   1/1     Running             0          2m
   controlplane:~$ kubectl get all
   NAME                              READY   STATUS    RESTARTS   AGE
   pod/hello-node-76998b7778-2zq2q   1/1     Running   0          31s
   pod/hello-node-76998b7778-bwvwg   1/1     Running   0          31s
   pod/hello-node-76998b7778-fsl4m   1/1     Running   0          31s
   pod/hello-node-76998b7778-w4hz6   1/1     Running   0          2m21s

   NAME                 TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
   service/hello-node   LoadBalancer   10.96.160.166   <pending>     8080:32312/TCP   101s
   service/kubernetes   ClusterIP      10.96.0.1       <none>        443/TCP          5d3h

   NAME                         READY   UP-TO-DATE   AVAILABLE   AGE
   deployment.apps/hello-node   4/4     4            4           2m21s

   NAME                                    DESIRED   CURRENT   READY   AGE
   replicaset.apps/hello-node-76998b7778   4         4         4       2m21s
   ```

   *观察*：Pod 数应增至 4；记录大约耗时（几秒内）。

## 本地环境对齐

### 安装或升级 Docker Desktop

1. 访问 **Docker 官方文档**，下载最新版 Docker Desktop Installer。
1. 安装完毕后启动 Docker Desktop。

### 2. 验证 Docker

```bash
docker run hello-world
```

看到 “Hello from Docker” 即表示 Docker Engine OK。

### 3. 安装 kubectl

- 官方二进制下载：

  ```powershell
  curl -LO https://dl.k8s.io/release/v1.33.0/bin/windows/amd64/kubectl.exe
  ```

  并把 `kubectl.exe` 放入 `%PATH%`。

- 验证：

  ```powershell
  kubectl version --client
  ```

  记下 Client Version；稍后连接 kind/minikube 时再看 Server Version。

### 4. 选择并安装本地 Kubernetes

| 选项 | 特点 | 安装命令 |
| ------------ | ------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------- |
| **kind** | 纯容器级集群、启动快、占用低；后续 Helm/Argo CD 演示够用 | `go install sigs.k8s.io/kind@v0.29.0` 或下载发行版二进制放入 PATH |
| **minikube** | 支持多 Driver；Docker Driver 在 WSL 2 上兼容好；内置 LoadBalancer / Ingress 插件 | 按官方 WSL Docker Driver 教程执行安装脚本；启动：`minikube start --driver=docker` |

> **推荐**：用 **kind**，命令少、镜像体积小，和 GitHub Actions 上的测试环境一致。
> **验证**：
>
> ```bash
> kind create cluster --name dev
> kubectl cluster-info
> kubectl get nodes
> ```
