# README

## 1. 画出案例程序的交互流程图
```mermaid
flowchart TD
    A([开始])

    subgraph Manager["华经理"]
        M1["使用神器生成一批口算练习"]
        M2["每天选择三套练习<br/>加法 / 减法 / 加减混合"]
        M3["打印练习纸"]
        M4["接收小明答案文件"]
        M5["导入练习文件和答案文件"]
        M6["查看成绩、错题和练习记录"]
        M7{"是否需要重点练习？"}
        M8["挑选错题或较差题目<br/>组成再练习"]
        M9["给予针对性指导"]
    end

    subgraph System["神器程序"]
        S1["生成练习题"]
        S2["保存练习题和标准答案"]
        S3["自动判题"]
        S4["计算得分"]
        S5["保存练习结果"]
        S6["统计错误较多的题目"]
    end

    subgraph Print["打印输出"]
        P1["输出纸质练习"]
    end

    subgraph Student["小明"]
        X1["在纸上完成口算练习"]
        X2["写下答案"]
    end

    subgraph Mother["小明妈妈"]
        W1["用办公软件录入答案"]
        W2["把答案文件传给华经理"]
    end

    A --> M1
    M1 --> S1
    S1 --> S2
    S2 --> M2
    M2 --> M3
    M3 --> P1
    P1 --> X1
    X1 --> X2
    X2 --> W1
    W1 --> W2
    W2 --> M4
    M4 --> M5
    M5 --> S3
    S3 --> S4
    S4 --> S5
    S5 --> S6
    S6 --> M6
    M6 --> M7
    M7 -- "是" --> M8
    M8 --> M2
    M7 -- "否" --> M9
    M9 --> Z([结束])
```

## 2. 用 CSV 文件格式存储数据

程序使用 CSV 保存练习题、标准答案和批改结果，方便用办公软件查看和录入。

- 练习题文件：`index,left,operator,right,expression`
- 标准答案文件：`index,answer`
- 学生作答文件：`index,studentAnswer`
- 批改结果文件：`index,left,operator,right,expression,correctAnswer,studentAnswer,correct`

默认运行主程序会按配置文件生成练习题和标准答案 CSV：

```bash
java com.sylphy.ArithmeticGenerator
```

也可以直接传入生成题目数量，例如生成 20 道题：

```bash
java com.sylphy.ArithmeticGenerator 20
```

批改时使用：

```bash
java com.sylphy.ArithmeticGenerator grade <problems.csv> <student-answers.csv> <results.csv>
```

代码位置：

- `src/main/java/com/sylphy/ArithmeticGenerator.java`：主程序入口，支持生成和批改命令。
- `src/main/java/com/sylphy/csv/CsvFile.java`：CSV 文件读写、转义和解析。
- `src/main/java/com/sylphy/writer/ProblemFileWriter.java`：写出练习题 CSV 和标准答案 CSV。
- `src/main/java/com/sylphy/writer/GradingReportWriter.java`：写出批改结果 CSV。
- `src/main/resources/application.properties`：默认 CSV 输出路径配置。

## 3. 防御性编程，如何处理错误和异常

代码在配置、CSV、题目和批改流程中进行输入校验。

- `GeneratorConfig` 校验题目数量、取值范围和输出路径。
- `CsvFile` 校验空文件、重复表头、列数不一致和引号未闭合。
- `ProblemRecord` 校验题号、运算符和减法非负约束。
- `GradingService` 校验题目不能为空、题号不能重复、作答不能缺失或多余。

代码位置：

- `src/main/java/com/sylphy/config/GeneratorConfig.java`
- `src/main/java/com/sylphy/csv/CsvFile.java`
- `src/main/java/com/sylphy/model/ProblemRecord.java`
- `src/main/java/com/sylphy/model/StudentAnswerRecord.java`
- `src/main/java/com/sylphy/model/GradingReport.java`
- `src/main/java/com/sylphy/service/GradingService.java`

## 4. 字符串和正则表达式处理应用在哪些地方？

字符串处理主要用于 CSV 转义、CSV 解析、题目表达式格式化和配置读取。测试中的 JSON 配置用例使用正则表达式读取字段，避免为了少量测试数据引入额外依赖。

代码位置：

- `src/main/java/com/sylphy/csv/CsvFile.java`：CSV 字符串转义和解析。
- `src/main/java/com/sylphy/model/arithmericproblem/AbstractBinaryArithmeticProblem.java`：题目表达式格式化。
- `src/test/java/com/sylphy/config/GeneratorConfigTest.java`：使用正则表达式读取 JSON 测试数据。

## 5. 数据建模和数据结构有哪些？

主要数据模型包括：

- `ArithmeticProblem`：题目接口。
- `ProblemBatch`：一批题目。
- `ProblemRecord`：CSV 中的一道题。
- `StudentAnswerRecord`：小明的一条作答。
- `GradingResult`：单题批改结果。
- `GradingReport`：一次练习的批改报告。

主要数据结构包括 `List` 保存有序题目、结果和策略配置，`Map` 按题号匹配作答或按运算符匹配策略，`Set` 检查重复题号和重复运算符。

代码位置：

- `src/main/java/com/sylphy/model/arithmericproblem/ArithmeticProblem.java`
- `src/main/java/com/sylphy/model/ProblemBatch.java`
- `src/main/java/com/sylphy/model/ProblemRecord.java`
- `src/main/java/com/sylphy/model/StudentAnswerRecord.java`
- `src/main/java/com/sylphy/model/GradingResult.java`
- `src/main/java/com/sylphy/model/GradingReport.java`
- `src/main/java/com/sylphy/service/GradingService.java`

## 6. 使用表驱动编程应用在哪些地方？

`GeneratorConfig` 保存策略列表，`ProblemCsvReader` 根据这组策略建立 `Map<Character, ArithmeticProblemStrategy>`。程序先通过运算符查表，再把匹配到的策略传给 `ProblemRecord`，由策略创建题目并计算答案。新增运算符时，可以增加一个策略并放入配置，而不是在多个地方写判断分支。

代码位置：

- `src/main/java/com/sylphy/config/GeneratorConfig.java`
- `src/main/java/com/sylphy/reader/ProblemCsvReader.java`
- `src/main/java/com/sylphy/model/ProblemRecord.java`
- `src/main/java/com/sylphy/strategy/ArithmeticProblemStrategy.java`
- `src/main/java/com/sylphy/strategy/AdditionProblemStrategy.java`
- `src/main/java/com/sylphy/strategy/SubtractionProblemStrategy.java`

## 7. 契约式编程应用在哪些地方？

构造方法和服务入口使用参数契约限制非法状态，例如题号必须大于 0、减法结果不能为负、批改结果不能为空、CSV 必须包含指定字段。违反契约时抛出 `IllegalArgumentException`。

代码位置：

- `src/main/java/com/sylphy/config/GeneratorConfig.java`
- `src/main/java/com/sylphy/model/ProblemRecord.java`
- `src/main/java/com/sylphy/model/StudentAnswerRecord.java`
- `src/main/java/com/sylphy/model/GradingReport.java`
- `src/main/java/com/sylphy/reader/ProblemCsvReader.java`
- `src/main/java/com/sylphy/reader/StudentAnswerCsvReader.java`
- `src/main/java/com/sylphy/service/GradingService.java`

## 8. 编写设计文档

当前文档说明了故事 5 的业务流程、CSV 数据格式、异常处理、数据模型、表驱动设计、契约式约束、测试和代码规范。

代码位置：

- `README.md`
- `doc/README-v2.md`

## 9. 测试数据和单元测试

已覆盖核心测试：

- 题目生成、策略和迭代器测试。
- CSV 写入和读取测试。
- 批改服务测试。
- 从 CSV 读取题目和答案、生成批改结果的工作流测试。

代码位置：

- `src/test/java/com/sylphy/service/ArithmeticProblemGeneratorTest.java`
- `src/test/java/com/sylphy/writer/ProblemFileWriterTest.java`
- `src/test/java/com/sylphy/csv/CsvFileTest.java`
- `src/test/java/com/sylphy/service/GradingServiceTest.java`
- `src/test/java/com/sylphy/service/GradingCsvWorkflowTest.java`
- `src/test/resources/generator-config-cases.json`

## 10. Git 代码管理和编程规范

代码按职责分包：

- `config`：配置读取和校验。
- `csv`：CSV 读写基础工具。
- `model`：题目、作答和批改数据模型。
- `reader`：CSV 读取器。
- `service`：生成和批改业务服务。
- `writer`：CSV 写入器。

生成文件仍放在 `output/`，测试和构建产物不应提交。

# 故事 6：用户交互的软件构造

## 1. 设计用户界面原型

控制台界面原型如下，运行主程序后循环显示，用户输入数字并按回车执行：

```text
100以内的口算练习程序
============================================================
功能列表（请输入功能前面对应的数字，按回车键执行）：
------------------------------------------------------------
1. 批量产生练习题
2. 选择并打印练习题
3. 编辑答题结果并保存
4. 选择一次练习并批改
5. 批量批改所有的题目
6. 选择一套练习并在机器完成
0. 退出
============================================================
请选择......
```

代码位置：

- `src/main/java/com/sylphy/ArithmeticGenerator.java`：唯一 `main` 入口。
- `src/main/java/com/sylphy/ConsoleApplication.java`：控制台界面、菜单循环和功能调度。

## 2. 用户菜单导航（CLI 下的）

菜单功能说明：

- `1`：按配置或输入数量批量生成练习题和标准答案 CSV。
- `2`：从题目 CSV 中选择一段题目，保存为可打印的练习 CSV。
- `3`：家长按题目逐题录入答案，并保存学生答案 CSV。
- `4`：选择一次练习题和学生答案进行批改。
- `5`：批量批改默认题目文件和学生答案文件。
- `6`：小明直接在电脑上答题，程序即时批改并保存结果。
- `0`：退出程序。

## 3. 程序只有一个 main 入口

程序只有一个启动入口：

```java
com.sylphy.ArithmeticGenerator.main()
```

`main` 只负责启动 `ConsoleApplication`，具体功能由菜单类分发，避免多个程序入口造成使用顺序混乱。

## 4. 交互设计的原则是什么？

本程序采用以下交互原则：

- 单入口：用户只运行一个主程序。
- 数字菜单：每个功能对应一个数字，降低记忆成本。
- 默认路径：常用文件路径提供默认值，减少重复输入。
- 即时反馈：生成、录入、批改后立即提示文件位置和成绩。
- 可退出循环：每次执行完功能后返回菜单，由用户决定何时退出。
- 错误可见：输入错误、文件错误和批改错误显示中文提示，不静默失败。

## 5. 静态程序分析

从故障类型看，当前设计的防护如下：

- 数据故障：`GeneratorConfig` 校验数量和范围，`ProblemRecord` 校验题号、运算符和减法约束。
- 控制故障：`ConsoleApplication` 使用 `switch` 控制菜单状态，非法菜单项给出提示并返回菜单。
- 输入/输出故障：CSV 读取校验空文件、重复表头、列数不一致；写文件前创建目录。
- 接口故障：`ProblemCsvReader` 使用配置中的策略表匹配运算符，避免题目读取和策略实现不一致。
- 存储管理故障：批量数据使用不可变 `ProblemBatch` 和 `GradingReport`，生成文件集中放入 `output/`。

代码位置：

- `src/main/java/com/sylphy/ConsoleApplication.java`
- `src/main/java/com/sylphy/config/GeneratorConfig.java`
- `src/main/java/com/sylphy/csv/CsvFile.java`
- `src/main/java/com/sylphy/reader/ProblemCsvReader.java`
- `src/main/java/com/sylphy/service/GradingService.java`

## 6. 给出程序的类结构 UML 图

```mermaid
classDiagram
    direction LR

    class ArithmeticGenerator {
        +main(String[] args) void
    }

    class ConsoleApplication {
        +run() void
    }

    class GeneratorConfig {
        +questionCount int
        +minValue int
        +maxValue int
        +outputPath Path
        +answerOutputPath Path
        +strategies List~ArithmeticProblemStrategy~
    }

    class ArithmeticProblem {
        <<interface>>
        +format() String
        +answer() int
    }

    class AdditionProblem
    class SubtractionProblem
    class ArithmeticProblemStrategy {
        <<interface>>
        +operator() char
        +create(int, int) ArithmeticProblem
    }

    class ArithmeticProblemGenerator {
        +generate(GeneratorConfig) ProblemBatch
    }

    class ProblemFileWriter {
        +write(ProblemBatch, Path, Path) void
    }

    class ProblemCsvReader {
        +read(Path) List~ProblemRecord~
    }

    class StudentAnswerCsvReader {
        +read(Path) List~StudentAnswerRecord~
    }

    class GradingService {
        +grade(List~ProblemRecord~, List~StudentAnswerRecord~) GradingReport
    }

    class GradingReportWriter {
        +write(GradingReport, Path) void
    }

    ArithmeticGenerator --> ConsoleApplication
    ConsoleApplication --> GeneratorConfig
    ConsoleApplication --> ArithmeticProblemGenerator
    ConsoleApplication --> ProblemFileWriter
    ConsoleApplication --> ProblemCsvReader
    ConsoleApplication --> StudentAnswerCsvReader
    ConsoleApplication --> GradingService
    ConsoleApplication --> GradingReportWriter
    AdditionProblem ..|> ArithmeticProblem
    SubtractionProblem ..|> ArithmeticProblem
    ArithmeticProblemGenerator --> ArithmeticProblemStrategy
    ArithmeticProblemStrategy --> ArithmeticProblem
```

## 7. 菜单结构中各个菜单间的状态转换 UML 图

```mermaid
stateDiagram-v2
    [*] --> Menu
    Menu --> GenerateBatch: 1
    GenerateBatch --> Menu: 完成/失败
    Menu --> SelectAndPrint: 2
    SelectAndPrint --> Menu: 完成/失败
    Menu --> EditAnswers: 3
    EditAnswers --> Menu: 完成/失败
    Menu --> GradeOne: 4
    GradeOne --> Menu: 完成/失败
    Menu --> GradeBatch: 5
    GradeBatch --> Menu: 完成/失败
    Menu --> ComputerPractice: 6
    ComputerPractice --> Menu: 完成/失败
    Menu --> Exit: 0
    Exit --> [*]
```

## 8. 测试数据和单元测试

故事 6 新增了菜单流程测试：

- `src/test/java/com/sylphy/ConsoleApplicationTest.java`：覆盖批量生成、批量批改、机器练习。

已有测试继续覆盖生成、CSV、批改和策略逻辑。当前验证命令：

```bash
mvn test
```

## 9. Git 代码管理和编程规范

代码继续按职责分包。故事 6 新增的交互层放在：

- `src/main/java/com/sylphy/ConsoleApplication.java`

编程规范：

- `main` 保持薄入口。
- 菜单交互集中在 `ConsoleApplication`。
- 生成、读写、批改逻辑继续复用原有 service、reader、writer。
- 不提交 `target/` 和 `output/`。

## 10. 提交到 GitHub 上

本地已使用 Git 管理代码。提交到 GitHub 时执行：

```bash
git push
git push origin v4
```

如果当前分支还没有远端跟踪分支，需要先设置远端分支。
