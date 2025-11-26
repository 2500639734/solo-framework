# ⚡ 快速开始

> 本指南将帮助您在 5 分钟内快速接入 Solo Framework 并完成第一个接口开发。

---

## 📋 前置要求

- **JDK**：1.8 或以上
- **Spring Boot**：2.5.7（推荐）
- **构建工具**：Maven 或 Gradle

---

## 🚀 快速接入（3 步上手）

### 步骤 1：引入依赖

在项目 `pom.xml` 中引入 Web 模块（包含全套 Web 开发能力）：

```xml
<dependency>
    <groupId>com.solo.framework</groupId>
    <artifactId>solo-framework-starter-web</artifactId>
    <version>${solo-framework.version}</version>
</dependency>
```

> 💡 **提示**：引入 `solo-framework-starter-web` 后，将自动集成以下能力：
> - ✅ Swagger/Knife4j 接口文档
> - ✅ FastJSON2 序列化
> - ✅ 统一响应包装与全局异常处理
> - ✅ 参数校验增强（分组/集合/枚举）
> - ✅ TraceId 链路追踪
> - ✅ 请求日志打印

---

### 步骤 2：启动项目

启动 Spring Boot 应用，控制台输出以下日志即表示框架加载成功：

```
Solo Framework Start Success! 
  applicationName: your-app (建议配置), 
  serverPort: 8080, 
  contextPath: /

Swagger UI 接口文档地址: [http://localhost:8080/swagger-ui/index.html]
Knife4j UI 接口文档地址: [http://localhost:8080/doc.html]
```

---

### 步骤 3：编写第一个接口

#### 示例 1：简单查询接口

```java
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@Api(tags = "示例模块")
@RestController
@RequestMapping("/demo")
public class DemoController {

    @ApiOperation(value = "Hello 接口")
    @GetMapping("/hello")
    public String hello(@RequestParam String name) {
        return "Hello, " + name;
    }
}
```

**访问接口**：
```
GET http://localhost:8080/demo/hello?name=Solo
```

**返回结果**（框架自动包装为 ApiResponse）：
```json
{
  "code": 0,
  "message": "请求成功",
  "data": "Hello, Solo",
  "traceId": "abc-123-def-456",
  "timestamp": 1735200000000
}
```

---

#### 示例 2：带参数校验的接口

```java
import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class UserRequest {
    
    @NotBlank(message = "用户名不能为空")
    private String userName;
    
    @NotNull(message = "年龄不能为空")
    @Min(value = 1, message = "年龄必须大于0")
    private Integer age;
    
    @Email(message = "邮箱格式不正确")
    private String email;
}
```

```java
@Api(tags = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @ApiOperation(value = "创建用户")
    @PostMapping("/create")
    public UserRequest createUser(@Validated @RequestBody UserRequest request) {
        // 框架自动完成参数校验，校验失败直接返回友好提示
        return request;
    }
}
```

**请求示例**：
```json
POST http://localhost:8080/user/create
Content-Type: application/json

{
  "userName": "",
  "age": 0,
  "email": "invalid-email"
}
```

**返回结果**（校验失败）：
```json
{
  "code": -5,
  "message": "用户名不能为空",
  "data": null,
  "traceId": "abc-123-def-456",
  "timestamp": 1735200000000
}
```

---

#### 示例 3：访问接口文档

框架内置 **Swagger UI** 和 **Knife4j UI** 两种文档界面：

| 文档 UI | 访问地址 | 特点 |
|---------|---------|------|
| **Swagger UI** | `http://localhost:8080/swagger-ui/index.html` | 官方原生界面 |
| **Knife4j UI** | `http://localhost:8080/doc.html` | 更美观、功能增强 |

打开浏览器访问上述地址，即可查看自动生成的 API 文档。

---

## 🛠️ 可选配置（进阶）

### 配置应用信息

在 `application.yml` 中配置应用基本信息：

```yaml
spring:
  application:
    name: solo-demo  # 应用名称（建议配置）

server:
  port: 8080        # 服务端口
  servlet:
    context-path: / # 上下文路径
```

---

### 自定义 Swagger 文档信息

```yaml
solo:
  framework:
    web:
      swagger:
        enabled: true                      # 是否启用 Swagger（生产环境建议关闭）
        title: Solo Demo API               # 文档标题
        description: Solo Framework 示例项目 # 文档描述
        version: v1.0.0                    # 文档版本
        base-packages:                     # 扫描包路径（默认扫描启动类所在包）
          - com.example.demo.controller
        concat:                            # 联系人信息
          name: Solo Team
          email: team@solo.com
          url: https://github.com/solo
```

---

### 自定义 JSON 序列化策略

```yaml
solo:
  framework:
    web:
      fastjson:
        date-format: yyyy-MM-dd HH:mm:ss   # 日期格式
        writer-features: writeNulls,prettyFormat  # 序列化特性（写null值、格式化）
        reader-features: fieldBased        # 反序列化特性（基于字段）
```

**FastJSON2 序列化特性说明**：
- `writeNulls`：null 值也会序列化输出
- `prettyFormat`：格式化 JSON（开发调试推荐）
- `writeBooleanAsNumber`：布尔值序列化为 0/1

> 📖 完整特性列表：[FastJSON2 官方文档](https://github.com/alibaba/fastjson2/blob/main/docs/features_cn.md)

---

### 自定义响应包装配置

```yaml
solo:
  framework:
    web:
      response:
        enabled: true                     # 是否启用响应包装（默认 true）
        show-valid-fail-field: true       # 参数校验失败时是否展示字段名（开发调试推荐）
        api-not-found-code: 200          # 404 时的 HTTP 状态码
        api-bad-request-code: 200        # 参数错误时的 HTTP 状态码
        api-error-code: 200              # 系统异常时的 HTTP 状态码
```

---

## 🎯 下一步

您已成功接入 Solo Framework！接下来可以：

1. **引入 MyBatis Plus**：参考 [快速接入 MyBatis Plus](#引入-mybatis-plus-模块)
2. **开启国际化**：参考 [功能详解 - 国际化支持](features.md#国际化支持)
3. **配置远程调用**：参考 [功能详解 - 远程调用](features.md#远程调用resttemplate)
4. **自定义异常与错误码**：参考 [功能详解 - 全局异常处理](features.md#全局异常处理)

---

## 📦 引入 MyBatis Plus 模块

如果需要使用 MyBatis Plus，按以下步骤操作：

### 1. 引入依赖

```xml
<dependency>
    <groupId>com.solo.framework</groupId>
    <artifactId>solo-framework-starter-mts</artifactId>
    <version>${solo-framework.version}</version>
</dependency>
```

### 2. 配置数据源

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456

mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml  # Mapper XML 扫描路径
  type-aliases-package: com.example.demo.entity # 实体类包路径
```

### 3. 编写实体类与 Mapper

```java
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("user")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String userName;
    private Integer age;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
```

```java
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承 BaseMapper，自动获得 CRUD 方法
}
```

### 4. 编写业务接口

```java
@Api(tags = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @ApiOperation(value = "查询用户")
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userMapper.selectById(id);
    }
}
```

**访问接口**：
```
GET http://localhost:8080/user/1
```

**返回结果**：
```json
{
  "code": 0,
  "message": "请求成功",
  "data": {
    "id": 1,
    "userName": "张三",
    "age": 25,
    "createTime": "2024-01-01 10:00:00",
    "updateTime": "2024-01-01 10:00:00"
  },
  "traceId": "abc-123-def-456",
  "timestamp": 1735200000000
}
```

---

## ❓ 常见问题

### Q1：如何屏蔽某些接口的自动包装？

使用 `@NoApiResponse` 注解标记（可用于类或方法）：

```java
@RestController
public class RawController {

    @NoApiResponse  // 此接口不会被包装为 ApiResponse
    @GetMapping("/raw")
    public Map<String, Object> rawResponse() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        return result;
    }
}
```

---

### Q2：如何关闭 Swagger 文档（生产环境）？

```yaml
solo:
  framework:
    web:
      swagger:
        enabled: false  # 关闭 Swagger
```

---

### Q3：如何自定义全局异常处理逻辑？

继承 `ApiResponseAdvice` 并重写方法：

```java
import com.solo.framework.web.handle.ApiResponseAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomApiResponseAdvice extends ApiResponseAdvice {

    @Override
    protected void printExceptionLog(Throwable ex) {
        // 自定义异常日志打印逻辑
        super.printExceptionLog(ex);
    }
}
```

---

## 🔗 相关文档

- [🔧 功能详解](features.md) - 详细配置与使用示例
- [📦 模块说明](modules.md) - 模块依赖关系与按需引入指南

---

**返回主文档**：[README.md](../README.md)
