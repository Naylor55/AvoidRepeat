## AvoidRepeat

防止接口重复提交

## 幂等性

    多个相同的操作对系统数据和状态产生的影响是一致的，称之为幂等性。

    落到基于Web的系统来说，通常指多次对RESTFul接口发起同一个请求, 必须保证操作只能成功执行一次。
    


比如:
- 订单接口, 不能多次创建订单。
- 支付接口, 重复支付同一笔订单只能扣一次钱。
- 支付宝回调接口, 可能会多次回调, 必须处理重复回调，类似的还有mq消费消息。
- 普通表单提交接口, 因为网络超时等原因多次点击提交, 只能成功一次。
- 使用浏览器回退，在历史页面中继续操作。
- 等等

实现幂等：

> 系统性的解决方案需要前端，后端，中间件，数据库等多层配合处理

- 数据库唯一索引 

- token机制，防止重复提交

- 数据库悲观锁，获取数据的时候加锁

- 数据库乐观锁，基于版本号version实现, 在更新数据那一刻校验数据

- 分布式锁，redis(jedis、redisson)或zookeeper实现

- 状态机，状态变更, 更新数据时判断状态


## 接口防止重复请求

防止接口的重复请求，可以有效的解决系统幂等性问题。同时，减少了业务系统正常运行所需要的硬件算力和资源；提升系统的健壮性和稳定性。

### 如何识别请求是否是同一个
RESTFul类型的web系统，一个接口对应某种资源，根据请求参数的不同<b>大致</b>可以判断是否请求的是某种资源中的特定内容，所以根据接口名称加该接口请求参数<b>大致</b>可以判断请求是否是同一个。


### 如何防止重复请求
在处理请求前，判断该请求是否是重复的，若是重复的，不做处理，直接响应一个错误的提示。

1. 运用aop切面编程思想，在请求到达接口前获取接口名称和请求参数，将二者序列化之后得到一个特定的 token 。
2. 根据 token 去 Redis 中查询，看是否可以查询到结果。
3. 若没有查询到结果，说明请求是一个正常的请求。将token存到 Redis ，key 和 value 均为token，指定过期时间为 1s （1s仅用于演示，过期时间需要根据特定的业务场景设置）。
4. 若查询到了结果，说明是一个重复的请求，抛接口重复请求的异常，由全局异常捕获工具捕捉到异常之后，编排响应信息并返回给请求方。


##  工程概览

### Redis集成和配置

增加pom依赖
~~~
        <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
~~~
Redis配置类
~~~
package com.naylor.springboot.avoidrepeat.config;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;


/**
 * redis 配置类
 */
@Configuration
public class RedisConfig {

    /**
     * 自定义的redistemplate
     **/
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 创建一个RedisTemplate对象，为了方便返回key为string，value为Object
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // 设置json序列化配置
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance);
        // string的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用string的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // value采用jackson的序列化方式
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hashkey采用string的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // hashvalue采用jackson的序列化方式
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
}

~~~

编写一个service ，封装向 Redis 中新增数据，查询数据的功能。redis的访问工具类直接使用框架的RedisTemplate。添加数据的时候，给了默认的过期时间为1秒（仅用作演示，需要根据具体业务场景设置）。
~~~
@Service
public class RepeatTokenServiceImpl implements RepeatTokenService {

    @Autowired
    private RedisTemplate redisTemplate;

 
    
    /**
     * 生成key
     */
    @Override
    public void createToken(String key) {        
        redisTemplate.opsForValue().set(key,key,1,TimeUnit.SECONDS);
    }
    
    @Override
    public Boolean checkToken(String key) {
        return redisTemplate.hasKey(key);
    }
}
~~~
### Aop编写
定义切点 ApiIdempotent
~~~
package com.naylor.springboot.avoidrepeat.aop;


import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiIdempotent {
}
~~~
切面逻辑
请求到达接口前，获取接口的名称和本次请求携带的参数，将二者拼接得到一个token
，判断token是否已经在 redis 中存在，若存在继续执行，若不存在抛异常。
~~~
package com.naylor.springboot.avoidrepeat.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.naylor.springboot.avoidrepeat.constant.AvoidRepeatPrefix;
import com.naylor.springboot.avoidrepeat.constant.ResponseCode;
import com.naylor.springboot.avoidrepeat.domain.repeat.service.RepeatTokenService;
import com.naylor.springboot.avoidrepeat.exception.RepeatException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApiIdempotentPointcut {

    @Autowired
    RepeatTokenService repeatTokenService;

    @Pointcut("@annotation(ApiIdempotent)")
    public void ApiIdempotentAspect() {
    }

    @Around("ApiIdempotentAspect()")
    public Object methodAround(ProceedingJoinPoint point) throws Throwable {
        String typeName = point.getSignature().getDeclaringTypeName();
        String methodName = point.getSignature().getName();
        String methodFullName = typeName + "." + methodName;
        String argsJson = JSON.toJSONString(point.getArgs(), SerializerFeature.WriteClassName);
        String json = argsJson.replaceAll("\"", "");
        String token = methodFullName + json;
        System.out.println("Token is :" + token);
        if (repeatTokenService.checkToken(AvoidRepeatPrefix.Avoid_Repeat_User + token)) {
            // 重复请求
            String tips = "Request Is Repeat";
            System.out.println(tips);
            throw new RepeatException(ResponseCode.REPETITIVE_OPERATION);
        } else {
            // 正常请求
            String tips = "Request Is Normal";
            System.out.println(tips);
            repeatTokenService.createToken(AvoidRepeatPrefix.Avoid_Repeat_User + token);
            return point.proceed();
        }

    }

}
~~~


### 自定义接口重复异常
定义一个自定义异常类，继承 RuntimeException
~~~
package com.naylor.springboot.avoidrepeat.exception;

import com.naylor.springboot.avoidrepeat.constant.ResponseCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RepeatException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RepeatException(ResponseCode responseCode) {
        if (null == responseCode) {
            responseCode = ResponseCode.REPETITIVE_OPERATION;
        }
        this.code = responseCode.getCode();
        this.msg = responseCode.getMsg();
    }

    private Integer code;
    private String msg;
}

~~~

编写控制器增强，处理全局异常
~~~
package com.naylor.springboot.avoidrepeat.common;

import java.net.ConnectException;

import com.naylor.springboot.avoidrepeat.dto.ResponseData;
import com.naylor.springboot.avoidrepeat.exception.RepeatException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class BizControllerAdvice {

    /**
     * 捕获重复请求的异常 - RepeatException
     * 
     * @param exception
     * @return
     */
    @ResponseBody
    @ExceptionHandler(RepeatException.class)
    public ResponseEntity<ResponseData> serviceExceptionHandler(RepeatException exception) {
        ResponseData response = new ResponseData(Integer.valueOf(exception.getCode()), exception.getMsg(),
                exception.getStackTrace());
        return new ResponseEntity<ResponseData>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 捕获网络连接的异常 - ConnectException 这里会捕获 Redis 连接不上的异常
     * 
     * @param exception
     * @return
     */
    @ResponseBody
    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<ResponseData> connectExceptionHandler(ConnectException exception) {
        return new ResponseEntity<ResponseData>(new ResponseData(0, exception.getMessage(), exception.getStackTrace()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 捕获常规异常 -Exception
     * 
     * @param exception
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData> exceptionHandler(Exception exception) {
        return new ResponseEntity<ResponseData>(new ResponseData(0, exception.getMessage(), exception.getStackTrace()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

~~~

### 编写一个测试接口，并给接口添加  ApiIdempotent 注解
~~~
package com.naylor.springboot.avoidrepeat.domain.user.controller;

import com.naylor.springboot.avoidrepeat.aop.ApiIdempotent;
import com.naylor.springboot.avoidrepeat.dto.ResponseData;
import com.naylor.springboot.avoidrepeat.dto.User;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@Api(tags = "用户")
@RestController
@RequestMapping("/user")
public class UserController {



    @ApiIdempotent
    @PostMapping("/")
    public User createUser(@RequestBody User user) {
        user.setId(user.getId()+1);
        user.setName(user.getName() + user.getId());
        return user;
    }
}

~~~
### 测试
用postman做一个简单的并发测试，向后端连续发送6个请求，通过postman可以看到第一个请求返回的是200，后续的返回都是500。
如果扩大并发数量，假设并发100个，可以观测到有超过一个响应的是200。

![](https://www.hualigs.cn/image/603db18b94e81.jpg)


![](https://www.hualigs.cn/image/603db1575aeba.jpg)

## 拓展
- 增加自定义过期时间的功能，aop切片提供参数功能
- 判断是否是同一个请求中增加用户标识。