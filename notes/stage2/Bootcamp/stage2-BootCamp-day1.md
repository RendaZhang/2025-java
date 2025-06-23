# Day 1 · AWS VPC / ALB / IAM

---

## VPC Wizard 实操

### VPC

VPC ID: vpc-0e707170d90e574bb

#### Your AWS virtual network

```
dev-vpc
10.0.0.0/16
No IPv6
```

#### Subnets within this VPC

```
dev-subnet-public1-ap-southeast-1a
10.0.0.0/20
```

```
dev-subnet-private1-ap-southeast-1a
10.0.128.0/20
```

```
dev-subnet-public2-ap-southeast-1b
10.0.16.0/20
```

```
dev-subnet-private2-ap-southeast-1b
10.0.144.0/20
```

#### Route network traffic to resources

```
dev-rtb-public
2 subnet associations
2 routes including local
```

```
rtb-0902296680c7826d1
No subnet associations
1 route including local
```

```
dev-rtb-private2-ap-southeast-1b
1 subnet association
3 routes including local
```

```
dev-rtb-private1-ap-southeast-1a
1 subnet association
3 routes including local
```

#### Connections to other networks

```
dev-igw
Internet routes to 2 public subnets
2 private subnets route to the Internet
```

```
dev-nat-public1-ap-southeast-1a
Public NAT gateway
1 ENI with 1 EIP
```

```
dev-vpce-s3
Gateway endpoint to S3
```

---

## NAT & Route 校验

### 创建 **Public** 实例

1. 打开 **EC2 Console ▸ Instances ▸ Launch instance**
2. **AMI**：Amazon Linux 2023、x86\_64
3. **Instance type**：t2.micro（免费层）
4. **Network settings** →
   * VPC：`dev-vpc`
   * Subnet：选择 *public1*
   * **Auto-assign public IP**：**Enable**
   * Security group：允许 SSH 或 Session Manager（推荐“允许所有流出”即可）
5. 命名：`pub-test` → Launch

### 创建 **Private** 实例

1. 再 Launch instance：
   * 同样 AMI / 类型
   * Subnet：选择 *private1*
   * **Auto-assign public IP**：**Disable**（保持默认）
2. 命名：`pri-test` → Launch
3. **连接方式**：勾选 **Enable SSM**（或在 IAM Role 里附加 `AmazonSSMManagedInstanceCore`，这样不用公网 IP 也能登录）。

### 验证出网&路由

1. **连接 pub-test**（SSH 或 EC2 Instance Connect）：
   ```bash
   [ec2-user@ip-10-0-2-199 ~]$ curl -s ifconfig.me # 返回公网 IPv4
   18.143.103.157
   [ec2-user@ip-10-0-2-199 ~]$
   [ec2-user@ip-10-0-2-199 ~]$ TOKEN=$(curl -sX PUT "http://169.254.169.254/latest/api/token" -H "X-aws-ec2-metadata-token-ttl-seconds: 60")
   [ec2-user@ip-10-0-2-199 ~]$
   [ec2-user@ip-10-0-2-199 ~]$ curl -s -H "X-aws-ec2-metadata-token: $TOKEN" http://169.254.169.254/latest/meta-data/public-ipv4
   18.143.103.157
   ```
   * 两条输出应一致（直接通过 IGW 出网）。

2. **连接 pri-test**（使用 Session Manager ➜ Shell）：
   ```bash
   sh-5.2$ curl -s ifconfig.me
   54.151.176.59
   sh-5.2$
   sh-5.2$ TOKEN=$(curl -sX PUT "http://169.254.169.254/latest/api/token" -H "X-aws-ec2-metadata-token-ttl-seconds: 60")
   sh-5.2$
   sh-5.2$ curl -s -H "X-aws-ec2-metadata-token: $TOKEN" http://169.254.169.254/latest/meta-data/public-ipv4 # 预期 404
   <?xml version="1.0" encoding="iso-8859-1"?>
   <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
           "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
   <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
     <title>404 - Not Found</title>
    </head>
    <body>
     <h1>404 - Not Found</h1>
    </body>
   </html>
   ```
   * 第一条应返回 **NAT Gateway 的公网 EIP**（或与 Public 实例不同的 IP）。
   * 第二条应 404/无数据（私网实例本身无公网 IP）。

## 创建 & 验证 ALB

### 创建 Target Group

1. 进入 **EC2 Console ▸ Load Balancing ▸ Target Groups ▸ Create target group**。
2. **Target type** 选 **IP addresses**，因为稍后我们会直接把 CloudShell 内网 IP 注册进去。
3. **VPC** 选刚才的 **`dev-vpc`**。
4. 端口填写 **80 / HTTP**，**Health check path** 设为 `/`（L7 健康检查默认 5 xx 视为失败）。
5. 命名 `tg-python-demo` ➜ Create。

> *为什么选 IP 而不是实例？*
>
> * 省去为临时 Target 开安全组；IRSA 蓝绿发布时也会用到 IP-mode TargetGroup，提前练习。

### 部署临时 Python HTTP 服务器并注册 Target

1. **连接 `pub-test` 实例**

   ```bash
   sudo yum -y install python3
   nohup sudo python3 -m http.server 80 &
   ```
2. **修改安全组**
   * 找到 ALB 所用 SG（假设 `sg-0bf78fa180bd8c537`）。
   * 给 `pub-test` 的 SG 加一条：
     * **来源** = `sg-0bf78fa180bd8c537`
     * **端口** = 80 / TCP
3. **重注册 Target**
   * 在 **Target Groups ▸ Register targets**；
   * 选 “Instances” → 勾选 `pub-test` → Include as **instance ID** *或* 填入 `10.0.0.x` 私网 IP。
4. **等待健康检查**  (30 s × 2 次) → 状态应变 **healthy**。但是目前还没有任何 Load Balancer 把流量指向这个 Target Group，因此不会做健康检查。目前状态是 `Unused`，等你创建并关联 ALB 监听器后，它会先变成 initial → 几秒后变成 healthy（若探测通过）。

### 创建 Application Load Balancer

1. **EC2 ▸ Load Balancers ▸ Create load balancer ▸ Application Load Balancer**。
2. **基本配置**
   * Name：`alb-demo`
   * Scheme：**Internet-facing**
   * IP address type：**IPv4**
3. **Network mapping**
   * VPC：`dev-vpc`
   * Subnets：勾选 **2 个 public 子网**（`10.0.0.0/20`, `10.0.16.0/20`）确保跨 AZ。
4. **Security group**
   * 创建或选用允许 **0.0.0.0/0 → TCP 80** 的 SG。
5. **Listeners & routing**
   * Listener 1：**HTTP : 80** ➜ Target group 选 **`tg-python-demo`**。
6. 点击 **Create load balancer**；等待状态 **Active**。

### 验证 & 记录

1. 在负载均衡器详情页复制 **DNS name**。
2. DNS Name: alb-demo-1754203016.ap-southeast-1.elb.amazonaws.com
3. 本地浏览器访问 `http://<DNS-name>`，应显示目录索引或 200 OK。



