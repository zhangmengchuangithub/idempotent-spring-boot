# idempotent integration with Spring Boot

idempotent-spring-boot-starter 帮助你集成幂等插件到 Spring Boot。

idempotent-spring-boot-starter will help you use idempotent with Spring Boot.

Support idempotent 1.0.0

Support spring-boot-starter-aop 2.1.6.RELEASE

Support spring-boot-starter-data-redis 2.1.9.RELEASE

其它版本待验证，有需求的可自行修改为spring boot对应支持的版本，后续会考虑用maven私服的形式支持其它版本

## How to use

参考[idempotent-spring-boot-samples](https://github.com/zhangmengchuangithub/idempotent-spring-boot/tree/master/idempotent-spring-boot-samples)

reference[idempotent-spring-boot-samples](https://github.com/zhangmengchuangithub/idempotent-spring-boot/tree/master/idempotent-spring-boot-samples)

在 pom.xml 中添加如下依赖：

Add the following dependency to your pom.xml: 

```xml
<dependency>
    <groupId>com.github.idempotent</groupId>
    <artifactId>idempotent-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```



在spring配置文件中增加配置：

```properties
#redis 单机模式配置
spring.redis.database=
spring.redis.host=
spring.redis.port=
spring.redis.password=
spring.redis.timeout=
```



详细使用参考[接口](https://github.com/zhangmengchuangithub/idempotent-spring-boot/blob/master/idempotent-spring-boot-samples/src/main/java/com/github/idempotent/samples/services/UserService.java) [实现类](https://github.com/zhangmengchuangithub/idempotent-spring-boot/blob/master/idempotent-spring-boot-samples/src/main/java/com/github/idempotent/samples/services/UserServiceImpl.java)

