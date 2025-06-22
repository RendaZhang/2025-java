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
