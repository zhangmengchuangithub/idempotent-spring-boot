package com.github.idempotent.annotation;

import com.github.idempotent.enums.CacheType;

import java.lang.annotation.*;

/**
 * @author zhangmc
 * @create 2020-01-06 17:21
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface IdempotentCache {

    /**
     * redis key的前缀
     * @return
     */
    String keyPrefix() default "";

    /**
     * 第一个入参对象的成员变量集合，没有配置取所有参数toString的MD5
     * @return
     */
    String[] fields() default {};

    /**
     * 并发执行 分布式锁的过期时间, 默认60s
     * @return
     */
    int lockExpiredTime() default 60;

    /**
     * 执行结果的过期时间, 默认600s 必须大于0 否则无法实现幂等
     * @return
     */
    int cacheExpiredTime() default 600;

    /**
     * 缓存的类型 方法执行的标识/方法执行的结果
     * @return
     */
    CacheType cacheType() default CacheType.CACHE_FLAG;

    /**
     * 并发执行 未获取到锁 阻塞时间(获取锁最长等待时间) 默认60s</br>
     * 默认小于1表示不不阻塞
     * 超过时间throw
     * @return
     */
    long blockTime() default 0L;

}
