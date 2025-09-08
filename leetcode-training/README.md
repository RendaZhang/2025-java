# LeetCode Training 使用说明

本项目是一个简化的 LeetCode 练习环境，可在命令行或 VS Code 中运行指定题号的示例代码。

## 环境要求
- [JDK 21](https://openjdk.org/projects/jdk/21/)
- [Apache Maven](https://maven.apache.org/) 3.9+
- 如需在 VS Code 中运行，建议安装 [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)。

## 命令行运行
```bash
# 进入模块目录
cd leetcode-training

# 编译与运行指定题号，这里以题号 1 为例
mvn exec:java -Dexec.args="1"

# 查看可用的题号列表
mvn exec:java

# 清理构建产物
mvn clean
```

## VS Code 运行
仓库根目录下提供了 `.vscode/launch.json`，可以在 **Run and Debug** 面板选择 **Run LeetCode Problem** 配置运行：
1. 在弹出的输入框中填写要运行的题号，例如 `1`；
2. VS Code 会在内置终端中编译并运行该题目的示例代码；
3. 如需清理，仍可在终端执行 `mvn clean`。

## 新增题目（用例）
1. 在 `src/main/java/com/renda/leetcode/problems` 包下创建文件，例如 `LC123_NewProblem.java`；
2. 实现 `LeetCodeProblem` 接口，示例：
   ```java
   public class LC123_NewProblem implements LeetCodeProblem {
       @Override
       public String problemNumber() {
           return "123";
       }
       @Override
       public void run() {
           // TODO 示例代码
       }
   }
   ```
3. 在 `ProblemRegistry` 的静态代码块中注册：
   ```java
   register(new LC123_NewProblem());
   ```
4. 运行 `mvn exec:java -Dexec.args="123"` 验证。

### 命名规范
- 类名与文件名统一为 `LC<题号>_<描述>`，例如 `LC1_TwoSum`；
- 每个类位于 `com.renda.leetcode.problems` 包中；
- `problemNumber()` 必须返回题号，`run()` 用于展示算法示例。

## 常用命令
| 操作               | 命令                              |
|--------------------|-----------------------------------|
| 编译并运行题目     | `mvn exec:java -Dexec.args="1"`   |
| 查看可运行题目列表 | `mvn exec:java`                    |
| 清理构建目录       | `mvn clean`                        |
