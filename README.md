# Solo Framework

Solo Framework 是一款用于快速构建Spring Boot应用程序的框架, 简化了大部分web开发的常用配置, 并针对常用中间件提供了开箱即用的统一解决方案, 使用者可以更专注投入于业务逻辑

---
### Solo Framework 设计理念
随着云原生架构的普及，Kubernetes 等基础设施已逐步接管了 Spring Cloud 所解决的服务治理与运维问题。在这种背景下，开发者只需基于 Spring Boot，即可快速构建应用，无需再引入繁重的 Spring Cloud 全家桶。

然而，尽管 Spring Boot 大幅简化了开发配置，实际开发中仍有诸多繁琐细节需要手动处理。例如：
- **API 文档集成**  
  主流的 Swagger 需要手动集成，若需使用更友好的 UI（如 Knife4j），还需自行处理兼容性问题。
- **统一响应规范**  
  高质量系统通常需具备统一的异常处理机制、标准化的返回结构和错误码体系。这些都需要开发者从零搭建，缺乏统一规范。
- **参数校验增强**  
  虽然 Spring Boot 支持 JSR-303 参数校验，但对于分组校验、集合参数、枚举类型校验及错误提示优化，依然需开发者额外处理。
- **JSON 返回规范**  
  当前接口多采用 JSON 交互，但序列化格式（如日期格式：`yyyy-MM-dd` vs `yyyy-MM-dd HH:mm:ss`）、空字段处理（返回 `null` 还是忽略）等细节，往往缺乏统一标准，增加维护成本。
- **MyBatis Plus 配置繁琐**  
  作为目前最流行的 ORM 方案，MyBatis Plus 功能强大，但其配置项较多，开发前要先配置整合。

### 核心理念

**Solo Framework** 的核心目标是**抽象出一套统一的开发规范和最佳实践封装**，最大限度地减少这些重复配置，让开发者“**引入一个 Starter，做极少配置，即可专注业务开发**”。同时，也支持灵活扩展，满足个性化需求。

### 与其它框架的差异
市面上已有不少快速开发平台，它们往往集成了大量功能，如：
- 基础的 CRUD 权限管理
- 代码生成器
- 流程引擎
- 分布式定时任务

但这类平台通常要求开发者下载整个项目，在其架构上继续开发，**存在较强的侵入性**，限制了系统的灵活性和可维护性。

而 **Solo Framework** 采用标准的 **Spring Boot Starter** 接入方式：
- **非侵入式**：不强制绑定项目结构
- **极简整合**：引入依赖即可使用
- **专注业务**：开发者只需专注自身业务，框架细节由 Starter 接管

通过这种设计，Solo Framework 致力于成为开发者手中轻量、灵活、可控的生产力工具，让“业务即核心”成为真正的实践目标。

### Solo Framework架构图
![solo-framework架构图](https://github.com/2500639734/solo-framework/blob/master/solo-framework.png)

### Solo Framework应用案例
  * **基于Solo-Framework + DeepSeek仅需半天就能快速开发小程序所需的后端接口**
  ![轻点记账小程序](https://github.com/2500639734/solo-framework/blob/master/tapBill.png)

---
## 框架模块说明
* `solo-framework-starter-common`: 默认引入一些开发常用的工具类库以及框架提供的一些工具类
  * hutool
* `solo-framework-starter-core`: 框架核心部分, 一般无需关注 
  * 框架配置文件加载
  * 框架运行时上下文信息加载
  * 提供统一事件驱动支持（待规划）
* `solo-framework-starter-core`: WEB开发常用配置支持
  * 集成Swagger UI, Knife4j, 提供接口文档支持
  * 集成FastJson2, 并提供自定义序列化策略支持
  * 统一定义接口返参实体, 返参结果自动包装
  * 统一定义全局异常捕获, 处理通用异常信息
  * 接口参数校验支持，统一处理校验错误
  * 全局链路追踪日志traceId支持
  * 国际化支持
* `solo-framework-starter-mts`: 自动配置MybatisPlus, 优化如批量插入等功能的性能（待开发）
* `solo-framework-starter-rocketmq`（待规划）: 简化配置, 提供开箱即用的统一事务消息、顺序消息、消息幂等消费支持
* `solo-framework-starter-redis`（待规划）: 简化配置, 提供开箱即用的缓存、分布式锁、防重注解支持

## 快速开始

### 使用说明
1. 下载源码，本地install
2. 业务应用中引入框架依赖
```maven
<dependency>
    <groupId>com.solo.framework</groupId>
    <artifactId>solo-framework-starter-web</artifactId>
    <version>${solo-framework-web.version}</version>
</dependency>
```
3. application.yml中配置数据库连接
```yml
spring:
   datasource:
     driver-class-name: com.mysql.cj.jdbc.Driver
     url: jdbc:mysql://${your_url}?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
     username: ${your_username}
     password: ${your_password}
mybatis-plus:
  # Mapper扫描路径,可以省略
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: ${your_package_name}
```

4. 启动项目, 框架会打印两行日志, 说明启动成功, 可以直接开业务代码开发了
```logcatfilter
Solo Framework Start Success! applicationName: null(建议配置), serverPort: 8080,  contextPath: /
Swagger UI 接口文档地址: [http://localhost:8080//swagger-ui/index.html], Knife4j UI 接口文档地址: [http://localhost:8080//doc.html]
```

### 功能说明
* **Web模块**
  * **接口文档（Swagger 和 Knife4j）**
    * Swagger-UI: 访问 [Swagger-UI](http://localhost:8080/swagger-ui/index.html)
    * Knife4j-UI: 访问 [Knife4j-UI](http://localhost:8080/doc.html)
    * 自定义配置
      * 方式一: 编写yaml配置文件
        ```yaml
        solo:
          framework:
            # 框架web模块配置 
            web:
              # swagger配置
              swagger:
                # 是否启用swagger(生产环境建议关闭)
                enabled: true
                # swagger文档的标题
                title: Solo Framework API Documentation
                # swagger文档的描述
                description: API documentation for your application
                # swagger文档的版本
                version: v1.0.0
                # swagger文档扫描的包路径(支持多个,默认取scanBasePackages目录)
                base-packages:
                  # - com.x1
                  # - com.x2
                # swagger文档的联系人信息
                concat:
                  # 联系人姓名
                  name: 
                  # 联系人链接
                  url: 
                  # 联系人email地址
                  email: 
                
        ```
      * 方式二: 编写java配置类 ( java配置类的方式优先级高于yaml配置的方式 )
        ```java
        @Configuration
        public class TestSwaggerConfig {
        
            @Bean
            public Docket soloFrameworkSwaggerDocket() {
                return new Docket(DocumentationType.SWAGGER_2)
                        .apiInfo(apiInfo())
                        .enable(true)
                        .select()
                        .apis(RequestHandlerSelectors.basePackage("com.test"))
                        .paths(PathSelectors.any())
                        .build();
            }
         
           private ApiInfo apiInfo() {
                return new ApiInfoBuilder()
                        .title("测试自定义Swagger配置")
                        .description("自定义配置生效")
                        .version("2.0.0")
                        .build();
           }
       
        }
        ```
      
  * **FastJSON2**
    * 默认全局序列化策略
      * 默认字段为null不会进行序列化输出
      * 默认序列化的字符编码为UTF-8
      * 日期格式为yyyy-MM-dd HH:mm:ss
    * 自定义配置
      * 方式一: 编写yaml配置文件
        ```yaml
        solo:
          framework:
            # 框架web模块配置 
            web:
              # fastjson配置
              fastjson:
                # 针对http响应的序列化编码格式
                http-chart-set: UTF-8
                # 针对http响应处理的请求头
                supported-media-types: application/json
                # json序列化编码格式
                chart-set: UTF-8
                # json序列化/反序列化日期默认格式
                date-format: yyyy-MM-dd HH:mm:ss
                # json反序列化策略(https://github.com/alibaba/fastjson2/blob/main/docs/features_cn.md 驼峰式命名)
                reader-features: fieldBased
                # json序列化策略(https://github.com/alibaba/fastjson2/blob/main/docs/features_cn.md 驼峰式命名)
                writer-features: writeNulls,writeBooleanAsNumber
        ```
      * 方式二: 编写java配置类 ( java配置类的方式优先级高于yaml配置的方式 )
        ```java
        @Configuration
        public class TestFastJsonConfig {
  
            @Bean
            public FastJsonConfig fastJsonConfig() {
                FastJsonConfig fastJsonConfig = new FastJsonConfig();
                fastJsonConfig.setDateFormat("yyyy-MM");
                fastJsonConfig.setWriterFeatures(JSONWriter.Feature.WriteNulls, JSONWriter.Feature.WriteBooleanAsNumber);
                return fastJsonConfig;
            }
  
        }
        ```
      
  * **接口响应包装**
    * 框架提供了统一的接口响应类`com.solo.framework.web.response.ApiResponse`, 使用者无需自行定义
    * 编写接口时只需关注需要返回的实体, 框架会自动将其包装作为data属性, 然后将其包装为ApiResponse对象返回
      * 例如, 编写业务接口
      ```java
      @Api(tags = "用户模块")
      @RestController
      public class UserController {
  
          @ApiOperation(value = "查询用户")
          @PostMapping("/user")
          public UserResponse user(@RequestBody UserRequest request){
              UserResponse userResponse = new UserResponse();
              userResponse.setId(1L);
              userResponse.setUserName("张三");
              userResponse.setBalance(new BigDecimal("18.85"));
              userResponse.setCreateAt(new Date());
              userResponse.setRemark(null);
              userResponse.setEnabled(true);
              return userResponse;
          }
      }
      ```
      * 编写的Controller只返回了UserResponse, 但最终返回的是ApiResponse, UserResponse作为ApiResponse.data
        ```json
        {
            "data": {
                "balance": 18.85,
                "createAt": "2024-09-19 22:45:22",
                "enabled": 1,
                "id": 1,
                "userName": "张三"
            },
          "message": "请求成功",
          "code": 0,
          "timestamp": 1726757122136
        }
        ```
        * `ApiResponse`的属性说明
          * `code`: 请求响应码
          * `message`: 请求响应提示信息
          * `traceId`: 请求Id
          * `timestamp`: 请求响应时间戳
          * `exceptionClass`: 请求异常的类路径
          * `exceptionMessage`: 请求异常原始信息(适用于非自定义异常的场景)
    * 自定义配置
      * 如何扩展接口响应字段? 
        * 继承`com.solo.framework.web.response.ApiResponse`类, 增加自己的属性定义
      * 如何扩展接口响应逻辑?
        * 继承`com.solo.framework.web.handle.ApiResponseAdvice`类, 并重写beforeBodyWrite方法处理自定义逻辑
      * 部分接口需要屏蔽框架自动包装功能?
        * 通过`@NoApiResponse`注解标记, 这个注解可以用在类或者方法上, 框架默认会自动包装接口返参
      * 开发模式
       ```yaml
      solo:
        framework:
          # 框架web模块配置 
          web:
            # 全局响应配置
            response:
              # 是否启用响应包装（默认true）
              enabled: true
              # 接口地址错误时, API统一HTTP响应码（默认200）
              apiNotFoundCode: 200
              # 接口参数错误时, API统一HTTP响应码（默认200）
              apiBadRequestCode: 200
              # 系统异常时, API统一HTTP响应码（默认200）
              apiErrorCode: 200
              # 是否显示参数校验失败字段（默认true）
              # true: 接口参数字段校验失败时,错误信息会展示校验失败的字段,开发阶段可以打开用于快速调试
              showValidFailField: true
      ```

    * **全局异常处理**
      * 框架默认对异常做了全局统一处理, 这些异常分为两类
        * 通用异常: 例如404、400等, 建议让框架自行处理, 框架会将其包装为ApiResponse对象, 并提供了完善的信息和友好的提示, 调用者无需关心
          * 框架默认处理的异常类型, 默认会打印一条错误日志, 下面列出了每种异常对应的错误日志
            * `HttpMessageNotReadableException`: `WARN`
            * `MethodArgumentNotValidException`: `WARN`
            * `IErrorHttpNoFoundException`: `WARN`
            * `IErrorException`: `WARN`
            * `RuntimeException`: `ERROR`
            * `Exception`: `ERROR`
          * 框架对通用类型的异常类型进行了统一定义, 它们被定义在`com.solo.framework.web.enums.ErrorCodeEnums类中, 可以直接使用
            ```java
            SUCCESS(0, "请求成功"),
            ERROR(-1, "服务器错误, 请联系运维人员处理"),
            ERROR_REQUEST_REPEAT(-2, "服务器繁忙, 请稍后重试"),
            ERROR_REQUEST_URI_INVALID(-3, "请求地址无效"),
            ERROR_REQUEST_PARAMS_INVALID(-4, "请求参数缺失或无效"),
            ERROR_REQUEST_PARAMS_FORMAT_INVALID(-5, "请求参数格式不符合要求"),
            ERROR_REQUEST_REQUEST_FAIL(-6, "请求远程调用失败"),
            ERROR_REQUEST_NETWORK_CONNECTION_FAIL(-7, "请求网络连接失败"),;
            ```
        * 业务异常: 这类异常需要使用者自行定义, 框架会自动捕获并将其包装为ApiResponse对象后返回
          * 自行定义异常枚举类, 然后抛出IErrorException即可, 需要实现`com.solo.framework.web.enums.IErrorCode`接口
            ```java
            /**
              * 业务异常枚举统一接口
              */
              public interface IErrorCode {

               /**
                * 获取请求响应码
                * @return 请求响应码
                */
                Integer getCode();

               /**
                * 获取请求响应信息
                * @return 请求响应信息
                */
                String getMessage();

            }
            ```
            ```java
            throw new IErrorException(CustomErrorCodeEnums.ERROR);
            ```
      * 自定义配置
        * 如何扩展自定义的异常捕获处理?
          * 继承ApiResponseAdvice类, 增加自己的异常捕获逻辑
        * 如何自定义框架默认处理异常的日志级别?
          * 继承ApiResponseAdvice类, 构造方法中增加异常及对于的日志级别
          ```java
          @Slf4j
          @RestControllerAdvice
          public class TestApiResponseAdvice extends ApiResponseAdvice {
  
              public TestApiResponseAdvice() {
                  putExceptionLogLevel(RuntimeException.class, SoloFrameworkLoggingEnum.INFO);
              }
  
          }
          ```
  * **参数校验**
    * 默认支持Validator规范的校验，框架会自动捕获参数校验异常并返回统一ApiResponse
    * 内置了分组校验接口定义：[QueryGroup.java、CreateGroup.java、UpdateGroup.java]
    * 内置了集合校验包装类：[ValidationList.java]
      ```java
      public UserResponse user(@Validated({CreateGroup.class}) @NotEmpty(message = "用户列表不能为空") @RequestBody ValidationList<UserRequest> requestList)
      }
      ```
    * 支持枚举类型校验
      ```java
      @NotBlank(message = "用户类型不能为空", groups = { CreateGroup.class })
      @EnumPattern(message = "用户类型不在枚举范围内", type = UserTypeEnum.class, fieldName = "code", groups = { CreateGroup.class })
      private Integer userType;
      ```
  * **国际化支持**
    * 开启方式
    ```yml
    solo:
      framework:
        # 框架web模块配置 
        web:
          # 国际化配置
          internation:
            # 是否启用国际化（默认true）
            enabled: true
            # 国际化资源文件目录（默认i18n/solo-framework_messages、i18n/messages、i18n/validation/messages）
            baseNames: 
              - i18n/solo-framework_messages
              - i18n/messages
              - i18n/validation/messages
            # 国际化资源文件编码（默认UTF-8）
            encoding: UTF-8
            # 国际化区域解析器（默认SESSION）
            localeResolver: SESSION
            # 国际化语言环境（默认CHINA）
            locale: CHINA
    ```
    * 使用方式
      * 定义异常枚举的错误信息key
        ```java
        /**
         * 系统级别异常定义
         */
        SUCCESS                                                 (0, "success.message"),
        ERROR                                                   (-1, "error.message"),
        ERROR_REQUEST_REPEAT                                    (-2, "error.request.repeat.message"),
        ERROR_REQUEST_URI_INVALID                               (-3, "error.uri.invalid.message"),
        ERROR_REQUEST_WAY_INVALID                               (-4, "error.way.invalid.message"),
        ERROR_REQUEST_PARAMS_INVALID                            (-5, "request.error.params.invalid.message"),
        ERROR_REQUEST_PARAMS_FORMAT_INVALID                     (-6, "request.error.params.format.invalid.message"),
        ERROR_REQUEST_REQUEST_FAIL                              (-7, "request.error.request.fail.message"),
        ERROR_REQUEST_NETWORK_CONNECTION_FAIL                   (-8, "request.error.network.connection.fail.message"),;
        ```
      * 指定目标下新建资源文件,并定义提示信息（默认、中文环境、英文环境）
      * [i18n/solo-framework_messages.properties、i18n/solo-framework_messages_zh_CN.properties]
      ```properties
      success.message=请求成功
      error.message=服务器错误, 请联系运维人员处理
      error.request.repeat.message=服务器繁忙, 请稍后重试
      error.uri.invalid.message=请求地址无效
      error.way.invalid.message=请求方式错误
      request.error.params.invalid.message=请求参数缺失或无效
      request.error.params.format.invalid.message=请求参数格式不符合要求
      request.error.request.fail.message=请求远程调用失败
      request.error.network.connection.fail.message=请求网络连接失败
      ```
      * [i18n/solo-framework_messages_en_US.properties]
      ```properties
      success.message=Request successful
      error.message=Server error, please contact the maintenance personnel for processing
      error.request.repeat.message=Server busy, please try again later
      error.uri.invalid.message=Request address is invalid
      error.way.invalid.message=Request method error
      request.error.params.invalid.message=Request parameters are missing or invalid
      request.error.params.format.invalid.message=Request parameter format does not meet the requirements
      request.error.request.fail.message=Request remote call failed
      request.error.network.connection.fail.message=Network connection failed
      ```
      * 调用框架内置接口切换环境
      ```shell
      GET http://localhost:8080/solo-framework/set-locale?lang=en-US
      ```

* **Mts模块**
  * **MyBatisPlus自动配置**
    * 框架默认配置好了MybatisPlus, 开发者无需理会繁琐的配置, 只需要指定数据源, 开箱即用
    ```yml
    spring:
      datasource:
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://${your_url}?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
        username: ${your_username}
        password: ${your_password}
    mybatis-plus:
      # Mapper扫描路径,可以省略
      mapper-locations: classpath*:mapper/**/*.xml
      type-aliases-package: ${your_package_name}
    ```
    * 也可以通过自定义配置进行覆盖，引入框架后必须配置数据源，默认开发是一定需要数据源的，如果必须关闭，则通过排除包的方式将mts模块排除










