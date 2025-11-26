# 📦 模块说明

> 本文档详细介绍 Solo Framework 的模块划分、依赖关系、按需引入指南。

---

## 目录

- [模块总览](#模块总览)
- [模块详解](#模块详解)
  - [solo-framework-starter-common](#solo-framework-starter-common)
  - [solo-framework-starter-core](#solo-framework-starter-core)
  - [solo-framework-starter-web](#solo-framework-starter-web)
  - [solo-framework-starter-mts](#solo-framework-starter-mts)
- [依赖关系](#依赖关系)
- [按需引入指南](#按需引入指南)
- [版本管理](#版本管理)

---

## 模块总览

Solo Framework 采用 **Maven 多模块父子结构**，模块化设计，按需引入。

| 模块名称 | 职责 | 必选/可选 | 依赖的核心框架 |
|---------|------|----------|---------------|
| **solo-framework-parent** | 父工程（统一依赖与版本管理） | - | - |
| **solo-framework-starter-common** | 通用工具类与基础定义 | ✅ 必选（被其他模块依赖）| Hutool、Lombok |
| **solo-framework-starter-core** | 核心配置体系、上下文、TraceId | ✅ 必选（核心基础）| Spring Boot、TransmittableThreadLocal |
| **solo-framework-starter-web** | Web 开发全家桶 | ⭐ 推荐（Web 应用首选）| Spring Web、Swagger、FastJSON2、Knife4j |
| **solo-framework-starter-mts** | MyBatis Plus 自动配置 | ⚙️ 可选（需要 ORM 时引入）| MyBatis Plus、MySQL Driver、Hikari |

---

## 模块详解

### solo-framework-starter-common

#### 职责定位

提供**通用工具类**与**基础定义**，不依赖任何业务框架，可独立复用。

#### 核心内容

| 类别 | 类名 | 说明 |
|------|------|------|
| **工具类** | `LogUtil` | 统一日志输出工具（支持日志级别枚举） |
| | `ReflectionUtils` | 反射工具（获取字段注解属性等） |
| **函数式接口** | `NoArgConsumer` | 无参消费型接口 |
| | `NoArgSupplier` | 无参供给型接口 |
| **枚举** | `SoloFrameworkLoggingEnum` | 日志级别枚举（TRACE/DEBUG/INFO/WARN/ERROR） |

#### 依赖的第三方库

```xml
<dependencies>
    <!-- Hutool 工具库 -->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```

#### 使用场景

- 需要使用框架提供的工具类（如 `LogUtil`）
- 其他模块的基础依赖（被 `core`、`web`、`mts` 依赖）

---

### solo-framework-starter-core

#### 职责定位

框架**核心配置体系**，定义全局属性、上下文、TraceId、运行时信息。

#### 核心内容

| 类别 | 类/接口 | 说明 |
|------|--------|------|
| **自动配置** | `SoloFrameworkCoreAutoConfiguration` | 核心模块自动配置类 |
| **属性体系** | `SoloFrameworkProperties` | 框架根属性配置 |
| | `SoloFrameworkWebProperties` | Web 模块属性配置 |
| | `SoloFrameworkWebSwaggerProperties` | Swagger 配置 |
| | `SoloFrameworkWebFastJsonProperties` | FastJSON2 配置 |
| | `SoloFrameworkWebResponseProperties` | 响应包装配置 |
| | `SoloFrameworkWebInternationProperties` | 国际化配置 |
| | `SoloFrameworkWebRemoteProperties` | 远程调用配置 |
| | `SoloFrameworkWebRequestLoggingProperties` | 请求日志配置 |
| **上下文** | `SoloFrameworkContextHolder` | 框架全局上下文 |
| | `SoloFrameworkTraceIdContextHolder` | TraceId 上下文（基于 TransmittableThreadLocal） |
| | `SoloFrameworkRuntimeInfo` | 运行时信息（应用名、端口、上下文路径等） |
| **常量** | `SoloFrameworkPropertiesPrefixConstant` | 配置前缀常量 |
| | `SoloFrameworkRequestHeaderConstant` | 请求头常量（`X-Request-Id`、`traceId`） |
| **接口** | `ISoloFrameworkTraceId` | TraceId 接口定义 |

#### 依赖关系

```xml
<dependencies>
    <!-- 依赖 common 模块 -->
    <dependency>
        <groupId>com.solo.framework</groupId>
        <artifactId>solo-framework-starter-common</artifactId>
    </dependency>
    
    <!-- Spring Boot 配置处理器 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-configuration-processor</artifactId>
    </dependency>
    
    <!-- TransmittableThreadLocal（TraceId 跨线程传递） -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>transmittable-thread-local</artifactId>
    </dependency>
</dependencies>
```

#### 配置示例

```yaml
solo:
  framework:
    web:
      swagger:
        enabled: true
      fastjson:
        date-format: yyyy-MM-dd HH:mm:ss
      response:
        enabled: true
```

#### 使用场景

- 所有模块的基础依赖
- 定义全局配置属性
- 提供 TraceId 上下文

---

### solo-framework-starter-web

#### 职责定位

Web 开发**全家桶**，提供开箱即用的 Web 开发能力。

#### 核心功能

| 功能模块 | 核心类/配置 | 说明 |
|---------|-----------|------|
| **Swagger/Knife4j** | `SoloFrameworkWebSwaggerAutoConfiguration` | 自动配置 Swagger 双 UI |
| **FastJSON2** | `SoloFrameworkWebFastJsonAutoConfiguration` | JSON 序列化/反序列化统一配置 |
| **统一响应包装** | `ApiResponseAdvice`<br/>`ApiResponse` | 自动包装返回值为 `ApiResponse` |
| **全局异常处理** | `ApiResponseAdvice` | 统一捕获并返回标准错误响应 |
| **参数校验增强** | `CreateGroup`/`UpdateGroup`/`QueryGroup`<br/>`ValidationList`<br/>`@EnumPattern` | 分组校验、集合校验、枚举校验 |
| **TraceId 链路追踪** | `SoloFrameworkTraceIdFilter`<br/>`RestTemplateTraceIdInterceptor` | 入站注入 + 出站透传 |
| **国际化支持** | `SoloFrameworkWebInternationAutoConfiguration` | 多语言切换 |
| **请求日志打印** | `HttpRequestLoggingInterceptor`<br/>`SoloFrameworkWebRequestLoggingAutoConfiguration` | 入站请求自动日志 |
| **远程调用** | `SoloFrameworkWebRemoteAutoConfiguration`<br/>`RestTemplateLoggingInterceptor` | RestTemplate 统一配置（OkHttp/HttpClient） |

#### 目录结构

```
solo-framework-starter-web/src/main/java/com/solo/framework/web/
├── annotation/          # 注解定义
│   ├── NoApiResponse.java
│   └── NoRequestLogging.java
├── configuration/       # 自动配置类
│   └── web/
│       ├── internation/
│       ├── json/
│       ├── remote/
│       ├── request/
│       ├── response/
│       └── swagger/
├── controller/          # 内置 Controller
│   └── SoloFrameworkDefaultController.java
├── enums/               # 枚举定义
│   ├── ErrorCodeEnums.java
│   └── IErrorCode.java
├── exception/           # 异常定义
│   ├── IErrorException.java
│   └── IErrorHttpNoFoundException.java
├── filter/              # 过滤器
│   └── SoloFrameworkTraceIdFilter.java
├── handle/              # 全局处理器
│   ├── ApiResponseAdvice.java
│   └── IApiResponseAdvice.java
├── interceptor/         # 拦截器
│   ├── HttpRequestLoggingInterceptor.java
│   ├── RestTemplateLoggingInterceptor.java
│   └── RestTemplateTraceIdInterceptor.java
├── response/            # 响应体定义
│   ├── ApiResponse.java
│   └── ApiResponseAbstract.java
├── util/                # 工具类
│   ├── HttpUtil.java
│   └── SoloFrameworkMessageUtil.java
├── validation/          # 参数校验
│   ├── enumd/
│   │   ├── EnumPattern.java
│   │   └── EnumPatternValidator.java
│   ├── CreateGroup.java
│   ├── UpdateGroup.java
│   ├── QueryGroup.java
│   └── ValidationList.java
└── wrapper/             # 包装类
    └── BufferingClientHttpResponseWrapper.java
```

#### 依赖关系

```xml
<dependencies>
    <!-- 依赖 core 模块 -->
    <dependency>
        <groupId>com.solo.framework</groupId>
        <artifactId>solo-framework-starter-core</artifactId>
    </dependency>
    
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Swagger -->
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger-ui</artifactId>
    </dependency>
    
    <!-- Knife4j -->
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-spring-boot-starter</artifactId>
    </dependency>
    
    <!-- FastJSON2 -->
    <dependency>
        <groupId>com.alibaba.fastjson2</groupId>
        <artifactId>fastjson2</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba.fastjson2</groupId>
        <artifactId>fastjson2-extension-spring5</artifactId>
    </dependency>
    
    <!-- OkHttp（可选，用于远程调用） -->
    <dependency>
        <groupId>com.squareup.okhttp3</groupId>
        <artifactId>okhttp</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- Apache HttpClient（可选，用于远程调用） -->
    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpclient</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

#### 使用场景

- Web 应用开发（推荐直接引入此模块）
- 需要 Swagger 文档、统一响应、全局异常、参数校验等能力

---

### solo-framework-starter-mts

#### 职责定位

MyBatis Plus **自动配置模块**，提供开箱即用的 ORM 能力。

#### 核心内容

| 类别 | 类名 | 说明 |
|------|------|------|
| **自动配置** | `SoloFrameworkMybatisPlusAutoConfiguration` | MyBatis Plus 自动配置 |
| **实体基类** | `BaseEntity` | 基础实体类（id、createTime、updateTime、deleted） |
| | `TenantEntity` | 租户实体类（继承 BaseEntity，新增 tenantId） |
| **枚举** | `DeletedEnum` | 逻辑删除枚举（NORMAL=0, DELETED=1） |
| **处理器** | `DefaultMetaObjectHandler` | 自动填充处理器（createTime、updateTime） |
| **属性配置** | `SoloFrameworkMtsProperties` | MTS 模块属性配置 |
| | `SoloFrameworkMtsDataSourceProperties` | 数据源配置 |
| | `SoloFrameworkMtsGlobalConfigProperties` | MyBatis Plus 全局配置 |

#### 依赖关系

```xml
<dependencies>
    <!-- 依赖 core 模块 -->
    <dependency>
        <groupId>com.solo.framework</groupId>
        <artifactId>solo-framework-starter-core</artifactId>
    </dependency>
    
    <!-- MyBatis Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
    </dependency>
    
    <!-- MySQL 驱动（默认 8.0.33） -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
    
    <!-- Hikari 连接池 -->
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
    </dependency>
</dependencies>
```

#### 配置示例

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456

mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: com.example.demo.entity
```

#### 使用场景

- 需要使用 MyBatis Plus 作为 ORM
- 需要基础实体类（`BaseEntity`、`TenantEntity`）
- 需要自动填充 createTime/updateTime

---

## 依赖关系

### 模块依赖图

```
solo-framework-parent (父工程)
│
├── solo-framework-starter-common (工具类与基础定义)
│
├── solo-framework-starter-core (核心配置与属性体系)
│   └── depends on: common
│
├── solo-framework-starter-web (Web 开发全家桶)
│   └── depends on: core (间接依赖 common)
│
└── solo-framework-starter-mts (MyBatis Plus 自动配置)
    └── depends on: core (间接依赖 common)
```

### 依赖传递规则

| 引入模块 | 自动传递依赖 |
|---------|-------------|
| **common** | Hutool、Lombok |
| **core** | common + Spring Boot 配置处理器 + TransmittableThreadLocal |
| **web** | core (含 common) + Spring Web + Swagger + Knife4j + FastJSON2 |
| **mts** | core (含 common) + MyBatis Plus + MySQL Driver + Hikari |

---

## 按需引入指南

### 场景 1：纯 Web 应用（推荐）

**需求**：开发 RESTful API，不使用数据库

**引入依赖**：
```xml
<dependency>
    <groupId>com.solo.framework</groupId>
    <artifactId>solo-framework-starter-web</artifactId>
    <version>${solo-framework.version}</version>
</dependency>
```

**包含能力**：
- ✅ Swagger/Knife4j 文档
- ✅ FastJSON2 序列化
- ✅ 统一响应包装
- ✅ 全局异常处理
- ✅ 参数校验增强
- ✅ TraceId 链路追踪
- ✅ 请求日志
- ✅ 远程调用

---

### 场景 2：Web 应用 + 数据库

**需求**：开发 Web 应用，使用 MyBatis Plus 操作数据库

**引入依赖**：
```xml
<!-- Web 模块 -->
<dependency>
    <groupId>com.solo.framework</groupId>
    <artifactId>solo-framework-starter-web</artifactId>
    <version>${solo-framework.version}</version>
</dependency>

<!-- MyBatis Plus 模块 -->
<dependency>
    <groupId>com.solo.framework</groupId>
    <artifactId>solo-framework-starter-mts</artifactId>
    <version>${solo-framework.version}</version>
</dependency>
```

**配置数据源**：
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/demo
    username: root
    password: 123456
```

---

### 场景 3：仅使用工具类（轻量）

**需求**：只需框架提供的工具类（如 `LogUtil`、`ReflectionUtils`）

**引入依赖**：
```xml
<dependency>
    <groupId>com.solo.framework</groupId>
    <artifactId>solo-framework-starter-common</artifactId>
    <version>${solo-framework.version}</version>
</dependency>
```

---

### 场景 4：自定义组合

**需求**：仅需部分功能（如只要 Swagger + 统一响应）

**引入依赖**：
```xml
<dependency>
    <groupId>com.solo.framework</groupId>
    <artifactId>solo-framework-starter-web</artifactId>
    <version>${solo-framework.version}</version>
</dependency>
```

**关闭不需要的功能**：
```yaml
solo:
  framework:
    web:
      fastjson:
        enabled: false  # 关闭 FastJSON2（使用默认 Jackson）
      remote:
        enabled: false  # 关闭远程调用
```

---

## 版本管理

### 父工程统一版本

`solo-framework-parent` 的 `pom.xml` 中统一管理所有依赖版本：

```xml
<properties>
    <!-- 框架版本 -->
    <solo-framework.version>1.0.0</solo-framework.version>
    
    <!-- Spring Boot 版本 -->
    <spring-boot.version>2.5.7</spring-boot.version>
    
    <!-- 第三方库版本 -->
    <hutool.version>5.8.11</hutool.version>
    <fastjson2.version>2.0.23</fastjson2.version>
    <swagger.version>2.9.2</swagger.version>
    <knife4j.version>2.0.9</knife4j.version>
    <mybatis-plus.version>3.5.1</mybatis-plus.version>
    <mysql.version>8.0.33</mysql.version>
    <transmittable-thread-local.version>2.12.2</transmittable-thread-local.version>
</properties>

<dependencyManagement>
    <dependencies>
        <!-- Spring Boot 依赖管理 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring-boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        
        <!-- 框架模块 -->
        <dependency>
            <groupId>com.solo.framework</groupId>
            <artifactId>solo-framework-starter-common</artifactId>
            <version>${solo-framework.version}</version>
        </dependency>
        <!-- 其他模块... -->
    </dependencies>
</dependencyManagement>
```

---

### 子模块版本继承

子模块 `pom.xml` 无需指定版本号（继承父工程）：

```xml
<parent>
    <groupId>com.solo.framework</groupId>
    <artifactId>solo-framework-parent</artifactId>
    <version>1.0.0</version>
</parent>

<dependencies>
    <dependency>
        <groupId>com.solo.framework</groupId>
        <artifactId>solo-framework-starter-core</artifactId>
        <!-- 无需指定 version，继承父工程 -->
    </dependency>
</dependencies>
```

---

## 构建与发布

### 本地安装

```bash
# 克隆项目
git clone https://github.com/solo-framework/solo-framework.git
cd solo-framework

# 安装到本地 Maven 仓库
mvn clean install -DskipTests
```

### 生成源码包

框架已配置 `maven-source-plugin`，执行以下命令生成源码包：

```bash
mvn clean package source:jar
```

---

## 🔗 相关文档

- [⚡ 快速开始](quick-start.md) - 5分钟上手指南
- [🔧 功能详解](features.md) - 详细配置与使用示例

---

**返回主文档**：[README.md](../README.md)
