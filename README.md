# Solo Framework

Solo Framework 是一款用于快速构建Spring Boot应用程序的框架, 简化了大部分web开发的常用配置, 并针对常用中间件提供了开箱即用的统一解决方案, 使用者可以更专注投入于业务逻辑

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
  * 全局链路追踪日志traceId支持（待规划）
* `solo-framework-starter-mts`: 简化配置, 扩展一些mybatis-plus的收费功能
* `solo-framework-starter-rocketmq`: 简化配置, 提供开箱即用的统一事务消息、顺序消息、消息幂等支持
* `solo-framework-starter-redis`: 简化配置, 提供开箱即用的缓存、分布式锁、防重注解支持
* 其它模块待后续规划

## solo-framework-starter-web (web开发模块)

### 使用说明
1. 引入框架依赖
```maven
<dependency>
    <groupId>com.solo.framework</groupId>
    <artifactId>solo-framework-starter-web</artifactId>
    <version>${solo-framework-web.version}</version>
</dependency>
```

2. 编写业务接口
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

3. 启动项目, 框架会打印两行日志
```logcatfilter
Solo Framework Start Success! applicationName: null(建议配置), serverPort: 8080,  contextPath: /
Swagger UI 接口文档地址: [http://localhost:8080//swagger-ui/index.html], Knife4j UI 接口文档地址: [http://localhost:8080//doc.html]
```

### 功能说明
* **Swagger 和 Knife4j**
  * Swagger-UI: 访问 [Swagger-UI](http://localhost:8080//swagger-ui/index.html)
  * Knife4j-UI: 访问 [Knife4j-UI](http://localhost:8080//doc.html)
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
      * 例如, 前面编写的Controller只返回了UserResponse, 但最终返回的是ApiResponse, UserResponse作为ApiResponse.data
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


















