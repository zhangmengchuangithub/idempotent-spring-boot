package com.github.idempotent.autoconfigure;

import com.github.idempotent.aop.IdempotentCacheAspect;
import com.github.idempotent.util.RedisUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


/**
 * @author zhangmc
 * @create 2020-01-06 15:54
 */
@Configuration
@ConditionalOnBean(name = {"redisTemplate"}, value = AopAutoConfiguration.class)
@AutoConfigureAfter(value = {RedisAutoConfiguration.class, AopAutoConfiguration.class})
public class IdempotentAutoConfiguration {

    @Bean
    public IdempotentCacheAspect idempotentCacheAspect() {
        return new IdempotentCacheAspect();
    }

    @Bean
    public RedisUtils redisUtils(){
        return new RedisUtils();
    }

    @PostConstruct
    public void init(){
        System.out.println("IdempotentAutoConfiguration init");
    }

}
