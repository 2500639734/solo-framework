# 🔧 功能详解

> 本文档详细介绍 Solo Framework 各功能模块的配置、使用示例与扩展方法。

---

## 目录

- [接口文档（Swagger/Knife4j）](#接口文档swaggerknife4j)
- [JSON 序列化（FastJSON2）](#json-序列化fastjson2)
- [统一响应包装](#统一响应包装)
- [全局异常处理](#全局异常处理)
- [参数校验增强](#参数校验增强)
- [TraceId 链路追踪](#traceid-链路追踪)
- [国际化支持](#国际化支持)
- [请求日志打印](#请求日志打印)
- [远程调用（RestTemplate）](#远程调用resttemplate)

---

## 接口文档（Swagger/Knife4j）

### 功能说明

框架内置 **Swagger 2.x** 和 **Knife4j** 双 UI 接口文档，无需手动集成。

| 文档类型 | 访问地址 | 特点 |
|---------|---------|------|
| **Swagger UI** | `http://localhost:8080/swagger-ui/index.html` | 官方原生界面 |
| **Knife4j UI** | `http://localhost:8080/doc.html` | 美观、功能增强（推荐） |

---

### 默认配置

```yaml
solo:
  framework:
    web:
      swagger:
        enabled: true                       # 是否启用 Swagger（生产环境建议关闭）
        title: API Documentation            # 文档标题
        description: API documentation      # 文档描述
        version: v1.0.0                     # 文档版本
        base-packages: []                   # 扫描包路径（默认启动类所在包）
        concat:                             # 联系人信息
          name: ""
          email: ""
          url: ""
```

---

### 自定义配置方式

#### 方式一：YAML 配置（推荐）

```yaml
solo:
  framework:
    web:
      swagger:
        enabled: true
        title: Solo Framework API 文档
        description: 基于 Solo Framework 的示例项目
        version: v2.0.0
        base-packages:
          - com.example.demo.controller
          - com.example.demo.api
        concat:
          name: Solo Team
          email: team@solo.com
          url: https://github.com/solo
```

---

#### 方式二：Java 配置类（覆盖 YAML）

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class CustomSwaggerConfig {

    @Bean
    public Docket soloFrameworkSwaggerDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(true)  // 生产环境可设置为 false
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.demo"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("自定义 Swagger 文档")
                .description("通过 Java 配置类定义")
                .version("v3.0.0")
                .contact(new Contact("Solo Team", "https://github.com/solo", "team@solo.com"))
                .build();
    }
}
```

> 💡 **提示**：Java 配置类优先级高于 YAML 配置，适合需要精细控制的场景。

---

### 常用注解

```java
import io.swagger.annotations.*;

@Api(tags = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @ApiOperation(value = "创建用户", notes = "创建新用户并返回用户信息")
    @PostMapping("/create")
    public User createUser(
        @ApiParam(value = "用户信息", required = true) @RequestBody User user
    ) {
        return user;
    }
    
    @ApiOperation(value = "查询用户")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    })
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return new User();
    }
}
```

---

## JSON 序列化（FastJSON2）

### 功能说明

框架内置 **FastJSON2** 作为默认 JSON 序列化/反序列化引擎，提供统一配置与最佳实践。

---

### 默认序列化策略

| 配置项 | 默认值 | 说明 |
|-------|-------|------|
| **编码格式** | `UTF-8` | HTTP 响应与 JSON 序列化编码 |
| **日期格式** | `yyyy-MM-dd HH:mm:ss` | 日期字段序列化格式 |
| **null 值处理** | **不输出** | 字段为 null 时不序列化（默认） |
| **序列化特性** | `prettyFormat`（格式化） | 美化输出 JSON |
| **反序列化特性** | `fieldBased`（基于字段）<br/>`supportArrayToBean`（数组转对象） | 支持私有字段直接注入 |

---

### 自定义配置

#### 方式一：YAML 配置

```yaml
solo:
  framework:
    web:
      fastjson:
        http-chart-set: UTF-8                              # HTTP 响应编码
        supported-media-types: application/json            # 支持的媒体类型
        chart-set: UTF-8                                   # JSON 序列化编码
        date-format: yyyy-MM-dd                            # 日期格式（仅日期）
        writer-features: writeNulls,prettyFormat           # 序列化特性
        reader-features: fieldBased,supportArrayToBean     # 反序列化特性
```

**常用 WriterFeatures（序列化特性）**：

| 特性 | 说明 |
|------|------|
| `writeNulls` | null 值也会输出 |
| `prettyFormat` | 格式化输出（开发调试推荐） |
| `writeBooleanAsNumber` | 布尔值序列化为 0/1 |
| `writeMapNullValue` | Map 中的 null 值也输出 |

**常用 ReaderFeatures（反序列化特性）**：

| 特性 | 说明 |
|------|------|
| `fieldBased` | 基于字段反序列化（无需 setter） |
| `supportArrayToBean` | 支持数组转对象 |

> 📖 完整特性列表：[FastJSON2 官方文档](https://github.com/alibaba/fastjson2/blob/main/docs/features_cn.md)

---

#### 方式二：Java 配置类

```java
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomFastJsonConfig {

    @Bean
    public FastJsonConfig fastJsonConfig() {
        FastJsonConfig config = new FastJsonConfig();
        
        // 日期格式
        config.setDateFormat("yyyy-MM-dd");
        
        // 序列化特性：输出 null 值、布尔值序列化为 0/1
        config.setWriterFeatures(
            JSONWriter.Feature.WriteNulls,
            JSONWriter.Feature.WriteBooleanAsNumber
        );
        
        // 反序列化特性：基于字段
        config.setReaderFeatures(
            JSONReader.Feature.FieldBased
        );
        
        return config;
    }
}
```

---

### 示例：自定义日期格式

**配置**：
```yaml
solo:
  framework:
    web:
      fastjson:
        date-format: yyyy-MM-dd HH:mm:ss
```

**实体类**：
```java
import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Long id;
    private String userName;
    private Date createTime;  // 自动按配置格式序列化
}
```

**返回结果**：
```json
{
  "code": 0,
  "message": "请求成功",
  "data": {
    "id": 1,
    "userName": "张三",
    "createTime": "2024-01-01 10:30:00"
  }
}
```

---

## 统一响应包装

### 功能说明

框架自动将 Controller 返回值包装为统一的 `ApiResponse` 对象，统一响应格式。

**ApiResponse 结构**：

```json
{
  "code": 0,                         // 响应码（0 成功，其他失败）
  "message": "请求成功",              // 响应提示信息
  "data": { ... },                   // 业务数据
  "traceId": "abc-123-def-456",      // 链路追踪 ID
  "timestamp": 1735200000000,        // 响应时间戳（毫秒）
  "exceptionClass": null,            // 异常类名（仅异常时）
  "exceptionMessage": null           // 异常原始信息（仅异常时）
}
```

---

### 自动包装示例

**Controller 代码**：
```java
@RestController
public class UserController {

    @GetMapping("/user")
    public User getUser() {
        User user = new User();
        user.setId(1L);
        user.setUserName("张三");
        return user;  // 只需返回 User 对象
    }
}
```

**实际返回结果**（框架自动包装）：
```json
{
  "code": 0,
  "message": "请求成功",
  "data": {
    "id": 1,
    "userName": "张三"
  },
  "traceId": "abc-123-def-456",
  "timestamp": 1735200000000
}
```

---

### 配置项

```yaml
solo:
  framework:
    web:
      response:
        enabled: true                    # 是否启用响应包装（默认 true）
        show-valid-fail-field: true      # 参数校验失败时是否展示字段名
        api-not-found-code: 200          # 404 错误时的 HTTP 状态码
        api-bad-request-code: 200        # 参数错误时的 HTTP 状态码
        api-error-code: 200              # 系统异常时的 HTTP 状态码
```

---

### 屏蔽自动包装

使用 `@NoApiResponse` 注解标记接口（可用于类或方法）：

```java
import com.solo.framework.web.annotation.NoApiResponse;

@RestController
public class RawController {

    @NoApiResponse  // 此接口不会被包装
    @GetMapping("/raw")
    public Map<String, Object> rawResponse() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        return result;  // 直接返回原始数据
    }
}
```

**返回结果**（不包装）：
```json
{
  "status": "ok"
}
```

---

### 扩展 ApiResponse

#### 场景：增加自定义字段

**步骤 1：继承 ApiResponse**

```java
import com.solo.framework.web.response.ApiResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomApiResponse<T> extends ApiResponse<T> {
    
    private String customField;  // 自定义字段
    
    public static <T> CustomApiResponse<T> success(T data, String customField) {
        CustomApiResponse<T> response = new CustomApiResponse<>();
        response.setCode(0);
        response.setMessage("请求成功");
        response.setData(data);
        response.setCustomField(customField);
        return response;
    }
}
```

**步骤 2：继承 ApiResponseAdvice**

```java
import com.solo.framework.web.handle.ApiResponseAdvice;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomApiResponseAdvice extends ApiResponseAdvice {

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        
        // 在这里可以自定义响应包装逻辑
        Object wrappedBody = super.beforeBodyWrite(body, returnType, selectedContentType, selectedConverterType, request, response);
        
        // 示例：在特定条件下增加自定义字段
        if (wrappedBody instanceof ApiResponse) {
            CustomApiResponse<?> customResponse = new CustomApiResponse<>();
            customResponse.setCode(((ApiResponse<?>) wrappedBody).getCode());
            customResponse.setMessage(((ApiResponse<?>) wrappedBody).getMessage());
            customResponse.setData(((ApiResponse<?>) wrappedBody).getData());
            customResponse.setCustomField("custom-value");
            return customResponse;
        }
        
        return wrappedBody;
    }
}
```

---

## 全局异常处理

### 功能说明

框架自动捕获常见异常并包装为统一 `ApiResponse`，支持自定义异常与错误码。

---

### 内置异常处理

| 异常类型 | 错误码 | 说明 | 日志级别 |
|---------|-------|------|---------|
| `HttpMessageNotReadableException` | -6 | 请求参数格式错误 | WARN |
| `MethodArgumentNotValidException` | -5 | 参数校验失败 | WARN |
| `IErrorHttpNoFoundException` | -3 | 404 错误 | WARN |
| `IErrorException` | 自定义 | 业务自定义异常 | WARN |
| `RuntimeException` | -1 | 运行时异常 | ERROR |
| `Exception` | -1 | 未捕获的其他异常 | ERROR |

---

### 内置错误码枚举

```java
package com.solo.framework.web.enums;

public enum ErrorCodeEnums implements IErrorCode {
    SUCCESS(0, "请求成功"),
    ERROR(-1, "服务器错误, 请联系运维人员处理"),
    ERROR_REQUEST_REPEAT(-2, "服务器繁忙, 请稍后重试"),
    ERROR_REQUEST_URI_INVALID(-3, "请求地址无效"),
    ERROR_REQUEST_WAY_INVALID(-4, "请求方式错误"),
    ERROR_REQUEST_PARAMS_INVALID(-5, "请求参数缺失或无效"),
    ERROR_REQUEST_PARAMS_FORMAT_INVALID(-6, "请求参数格式不符合要求"),
    ERROR_REQUEST_REQUEST_FAIL(-7, "请求远程调用失败"),
    ERROR_REQUEST_NETWORK_CONNECTION_FAIL(-8, "请求网络连接失败");
    
    // ...
}
```

---

### 自定义业务异常

#### 步骤 1：定义业务错误码枚举

```java
import com.solo.framework.web.enums.IErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCodeEnums implements IErrorCode {

    USER_NOT_FOUND(-1001, "用户不存在"),
    USER_ALREADY_EXISTS(-1002, "用户已存在"),
    INSUFFICIENT_BALANCE(-1003, "余额不足");

    private final Integer code;
    private final String message;
}
```

---

#### 步骤 2：抛出业务异常

```java
import com.solo.framework.web.exception.IErrorException;

@Service
public class UserService {

    public User getUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            // 抛出自定义异常
            throw new IErrorException(CustomErrorCodeEnums.USER_NOT_FOUND);
        }
        return user;
    }
}
```

---

#### 步骤 3：异常自动被捕获并返回

**请求**：
```
GET http://localhost:8080/user/999
```

**返回结果**（框架自动处理）：
```json
{
  "code": -1001,
  "message": "用户不存在",
  "data": null,
  "traceId": "abc-123-def-456",
  "timestamp": 1735200000000,
  "exceptionClass": "com.solo.framework.web.exception.IErrorException",
  "exceptionMessage": "用户不存在"
}
```

---

### 自定义异常处理逻辑

继承 `ApiResponseAdvice` 并重写方法：

```java
import com.solo.framework.common.enumeration.SoloFrameworkLoggingEnum;
import com.solo.framework.web.handle.ApiResponseAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomApiResponseAdvice extends ApiResponseAdvice {

    public CustomApiResponseAdvice() {
        // 自定义异常日志级别（RuntimeException 改为 INFO）
        putExceptionLogLevel(RuntimeException.class, SoloFrameworkLoggingEnum.INFO);
    }

    @Override
    protected void printExceptionLog(Throwable ex) {
        // 自定义异常日志打印逻辑
        log.error("捕获异常: {}", ex.getMessage());
        super.printExceptionLog(ex);
    }
}
```

---

## 参数校验增强

### 功能说明

框架内置增强的参数校验能力，支持：
- ✅ **JSR-303 标准校验**（`@NotNull`、`@NotBlank` 等）
- ✅ **分组校验**（Create/Update/Query）
- ✅ **集合校验**（`ValidationList`）
- ✅ **枚举校验**（`@EnumPattern`）

---

### 内置分组接口

```java
package com.solo.framework.web.validation;

// 查询分组
public interface QueryGroup {}

// 创建分组
public interface CreateGroup {}

// 更新分组
public interface UpdateGroup {}
```

---

### 使用示例

#### 示例 1：基础校验

```java
import lombok.Data;
import javax.validation.constraints.*;

@Data
public class UserRequest {
    
    @NotBlank(message = "用户名不能为空")
    private String userName;
    
    @NotNull(message = "年龄不能为空")
    @Min(value = 1, message = "年龄必须大于0")
    @Max(value = 150, message = "年龄必须小于150")
    private Integer age;
    
    @Email(message = "邮箱格式不正确")
    private String email;
}
```

```java
@PostMapping("/create")
public User createUser(@Validated @RequestBody UserRequest request) {
    // 参数校验失败会自动返回错误响应
    return new User();
}
```

---

#### 示例 2：分组校验

```java
import com.solo.framework.web.validation.*;
import lombok.Data;
import javax.validation.constraints.*;

@Data
public class UserRequest {
    
    @NotNull(message = "用户ID不能为空", groups = {UpdateGroup.class})
    private Long id;
    
    @NotBlank(message = "用户名不能为空", groups = {CreateGroup.class, UpdateGroup.class})
    private String userName;
    
    @NotNull(message = "年龄不能为空", groups = {CreateGroup.class})
    @Min(value = 1, message = "年龄必须大于0", groups = {CreateGroup.class})
    private Integer age;
}
```

```java
@PostMapping("/create")
public User createUser(@Validated(CreateGroup.class) @RequestBody UserRequest request) {
    // 仅校验 CreateGroup 分组的字段
    return new User();
}

@PostMapping("/update")
public User updateUser(@Validated(UpdateGroup.class) @RequestBody UserRequest request) {
    // 仅校验 UpdateGroup 分组的字段
    return new User();
}
```

---

#### 示例 3：集合校验

```java
import com.solo.framework.web.validation.*;
import lombok.Data;
import javax.validation.constraints.*;
import java.util.List;

@Data
public class UserRequest {
    @NotBlank(message = "用户名不能为空", groups = {CreateGroup.class})
    private String userName;
}
```

```java
import com.solo.framework.web.validation.ValidationList;

@PostMapping("/batch-create")
public List<User> batchCreate(
    @Validated(CreateGroup.class) 
    @NotEmpty(message = "用户列表不能为空") 
    @RequestBody ValidationList<UserRequest> requestList
) {
    // ValidationList 支持集合内每个元素的校验
    return new ArrayList<>();
}
```

---

#### 示例 4：枚举校验

```java
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserTypeEnum {
    NORMAL(1, "普通用户"),
    VIP(2, "VIP用户"),
    ADMIN(3, "管理员");

    private final Integer code;
    private final String name;
}
```

```java
import com.solo.framework.web.validation.enumd.EnumPattern;
import lombok.Data;
import javax.validation.constraints.*;

@Data
public class UserRequest {
    
    @NotNull(message = "用户类型不能为空", groups = {CreateGroup.class})
    @EnumPattern(
        message = "用户类型不在枚举范围内", 
        type = UserTypeEnum.class, 
        fieldName = "code",  // 枚举字段名
        groups = {CreateGroup.class}
    )
    private Integer userType;
}
```

---

### 校验失败返回示例

**请求**：
```json
POST /user/create
{
  "userName": "",
  "age": 0
}
```

**返回结果**：
```json
{
  "code": -5,
  "message": "用户名不能为空:[userName]",
  "data": null,
  "traceId": "abc-123-def-456",
  "timestamp": 1735200000000
}
```

> 💡 **提示**：`show-valid-fail-field: true` 时会显示失败字段名，便于开发调试。

---

## TraceId 链路追踪

### 功能说明

框架自动为每个请求注入全局唯一的 `TraceId`，并在以下场景自动传播：
- ✅ **入站请求**：通过 `SoloFrameworkTraceIdFilter` 自动注入
- ✅ **出站调用**：通过 `RestTemplateTraceIdInterceptor` 自动透传
- ✅ **日志输出**：自动添加到 MDC 上下文

---

### 自动注入与透传

#### 入站请求（自动注入）

**客户端请求**：
```
GET http://localhost:8080/user/1
```

**框架行为**：
1. 从请求头 `X-Request-Id` 读取 TraceId
2. 如果不存在，自动生成 UUID
3. 存入 `SoloFrameworkTraceIdContextHolder` 和 `MDC`

---

#### 出站调用（自动透传）

```java
@Service
public class RemoteService {

    @Autowired
    private RestTemplate restTemplate;

    public String callRemoteApi() {
        // TraceId 自动添加到请求头 X-Request-Id
        return restTemplate.getForObject("http://remote-api/data", String.class);
    }
}
```

**请求头自动包含**：
```
X-Request-Id: abc-123-def-456
```

---

### 日志输出 TraceId

**logback-spring.xml 配置**：

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] [%X{traceId}] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

**日志输出示例**：
```
2024-01-01 10:30:00 [http-nio-8080-exec-1] [abc-123-def-456] INFO  c.e.d.UserController - 查询用户: 1
```

---

### 手动获取 TraceId

```java
import com.solo.framework.core.context.SoloFrameworkTraceIdContextHolder;

public class DemoService {

    public void doSomething() {
        String traceId = SoloFrameworkTraceIdContextHolder.getTraceId();
        System.out.println("当前 TraceId: " + traceId);
    }
}
```

---

## 国际化支持

### 功能说明

框架支持多语言切换，默认支持中文（`zh_CN`）和英文（`en_US`）。

---

### 开启国际化

```yaml
solo:
  framework:
    web:
      internation:
        enabled: true                          # 开启国际化（默认 false）
        base-names:                            # 资源文件路径
          - i18n/solo-framework_messages
          - i18n/messages
          - i18n/validation/messages
        encoding: UTF-8                        # 编码格式
        locale-resolver: SESSION               # 区域解析器（SESSION/COOKIE）
        locale: CHINA                          # 默认语言环境
```

---

### 配置资源文件

在 `src/main/resources/i18n` 目录下创建：

**solo-framework_messages.properties（默认）**：
```properties
success.message=请求成功
error.message=服务器错误, 请联系运维人员处理
```

**solo-framework_messages_zh_CN.properties（中文）**：
```properties
success.message=请求成功
error.message=服务器错误, 请联系运维人员处理
```

**solo-framework_messages_en_US.properties（英文）**：
```properties
success.message=Request successful
error.message=Server error, please contact maintenance personnel
```

---

### 使用示例

#### 定义错误码枚举（支持国际化）

```java
import com.solo.framework.web.enums.IErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCodeEnums implements IErrorCode {

    USER_NOT_FOUND(-1001, "user.not.found.message");  // 使用 i18n key

    private final Integer code;
    private final String message;
}
```

**资源文件**：
```properties
# zh_CN
user.not.found.message=用户不存在

# en_US
user.not.found.message=User not found
```

---

### 切换语言环境

框架内置切换接口：

```
GET http://localhost:8080/solo-framework/set-locale?lang=en-US
```

**支持的语言**：
- `zh-CN`：中文
- `en-US`：英文

**切换后响应示例**（英文）：
```json
{
  "code": 0,
  "message": "Request successful",
  "data": null
}
```

---

## 请求日志打印

### 功能说明

框架自动打印 HTTP 请求与响应日志，包括：
- ✅ 请求 URL、方法、参数、请求头
- ✅ 响应体、耗时

---

### 配置项

```yaml
solo:
  framework:
    web:
      request-logging:
        enabled: true                          # 是否启用请求日志（默认 true）
        exclude-uris:                          # 排除不打印日志的路径
          - /swagger-ui/**
          - /swagger-resources/**
          - /v2/api-docs
          - /doc.html
          - /webjars/**
          - /favicon.ico
          - /error
```

---

### 日志输出示例

**入站请求日志**：
```
INFO  HTTP请求开始: method=POST, uri=/user/create, params={"userName":"张三","age":25}
INFO  HTTP请求结束: method=POST, uri=/user/create, status=200, duration=120ms
```

---

## 远程调用（RestTemplate）

### 功能说明

框架内置 `RestTemplate` 配置，支持：
- ✅ **OkHttp（默认）** 或 **Apache HttpClient**
- ✅ **连接池管理**
- ✅ **TraceId 自动透传**
- ✅ **请求日志自动打印**

---

### 配置项

```yaml
solo:
  framework:
    web:
      remote:
        enabled: true                          # 是否启用远程调用（默认 true）
        type: OK_HTTP                          # 客户端类型（OK_HTTP/HTTP_CLIENT）
        enable-trace-id-propagation: true      # 是否启用 TraceId 透传
        enable-request-logging: true           # 是否启用请求日志
        
        # 连接池配置
        connection-pool:
          max-total: 200                       # 最大连接数
          max-per-route: 50                    # 每个路由最大连接数
          time-to-live: 900000                 # 连接存活时间（毫秒）
          connection-request-timeout: 5000     # 连接池获取连接超时（毫秒）
          evict-idle-connections: 60000        # 空闲连接清理间隔（毫秒）
        
        # 超时配置
        timeout:
          connect-timeout: 5000                # 连接超时（毫秒）
          read-timeout: 30000                  # 读取超时（毫秒）
          write-timeout: 30000                 # 写入超时（毫秒，仅 OkHttp）
        
        # 日志配置
        request-logging:
          max-response-body-length: 2048       # 响应体最大打印长度（字节）
```

---

### 使用示例

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RemoteService {

    @Autowired
    private RestTemplate restTemplate;  // 框架自动装配

    public String getData() {
        // GET 请求
        return restTemplate.getForObject("http://api.example.com/data", String.class);
    }

    public User createUser(UserRequest request) {
        // POST 请求
        return restTemplate.postForObject("http://api.example.com/user", request, User.class);
    }
}
```

---

### 自动日志输出

**日志示例**：
```
INFO  接口远程调用开始, url = http://api.example.com/data, method = GET, headers = {...}, params = {}
INFO  接口远程调用结束, url = http://api.example.com/data, response = {"status":"ok"}, duration = 150ms
```

---

### 切换 HTTP 客户端

**使用 Apache HttpClient**：
```yaml
solo:
  framework:
    web:
      remote:
        type: HTTP_CLIENT  # 切换为 HttpClient
```

> 💡 **提示**：需确保 `httpclient` 依赖已引入，否则会静默跳过配置。

---

## 🔗 相关文档

- [⚡ 快速开始](quick-start.md) - 5分钟上手指南
- [📦 模块说明](modules.md) - 模块依赖关系与按需引入指南

---

**返回主文档**：[README.md](../README.md)
