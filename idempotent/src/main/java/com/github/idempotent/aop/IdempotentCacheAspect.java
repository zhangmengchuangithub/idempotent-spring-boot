package com.github.idempotent.aop;

import com.alibaba.fastjson.JSONObject;
import com.github.idempotent.annotation.IdempotentCache;
import com.github.idempotent.enums.CacheType;
import com.github.idempotent.enums.IdempotentErrorCode;
import com.github.idempotent.exception.IdempotentException;
import com.github.idempotent.util.RedisUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangmc
 * @create 2020-01-06 17:23
 */
@Aspect
public final class IdempotentCacheAspect extends AbstractCacheAspect {

    @Autowired
    private RedisUtils redisUtils;

    public static final String LOCK = ":idempotent:lock";

    public static final String CACHE = ":idempotent:cache";

    private static final String EXECUTED = "executed";

    @Pointcut("@annotation(com.github.idempotent.annotation.IdempotentCache)")
    @Override
    public void cacheAnnotationPointcut() {
    }

    @Around("cacheAnnotationPointcut()")
    @Override
    public Object invokeResourceWithAnnotation(ProceedingJoinPoint pjp) throws Throwable {
        Method originMethod = getMethod(pjp);

        IdempotentCache annotation = originMethod.getAnnotation(IdempotentCache.class);
        String[] fields = annotation.fields();

        // 获取指定field的值拼接作为唯一主键
        String uniqueKey = getUniqueKey(pjp.getArgs(), fields);
        Class returnType = originMethod.getReturnType();

        // 环绕切面逻辑处理
        return around(pjp, uniqueKey, annotation, returnType);
    }

    @Override
    protected void noParametersHandle() {
        throw new IdempotentException(IdempotentErrorCode.MUST_HAVE_AT_LEAST_ONE_PARAMETER,
                "The idempotent method must have at least one parameter");
    }

    @Override
    protected void fieldValueIsEmptyHandle(String field) {
        throw new IdempotentException(IdempotentErrorCode.THE_SPECIFIED_FIELD_CANNOT_BE_EMPTY,
                "specified field: " + field + " cannot be null or empty");
    }

    @Override
    protected void parametersAreAllEmptyHandle() {
        throw new IdempotentException(IdempotentErrorCode.PARAMETERS_ARE_ALL_EMPTY,
                "Idempotent method parameters are all empty");
    }

    /**
     * 环绕方法
     * @param pjp           切点
     * @param uniqueKey    请求唯一标示
     * @param annotation
     * @param returnType
     * @return
     * @throws Throwable
     */
    private Object around(ProceedingJoinPoint pjp, String uniqueKey, IdempotentCache annotation, Class returnType) throws Throwable {
        String requestId = UUID.randomUUID().toString();
        String lockKey = annotation.keyPrefix().concat(uniqueKey).concat(LOCK);
        String cacheKey = annotation.keyPrefix().concat(uniqueKey).concat(CACHE);
        int lockExpiredTime = annotation.lockExpiredTime();
        long blockTime = annotation.blockTime();
        boolean lock = false;
        try {
            // 先判断有没有缓存 有直接返回或抛异常
            CacheResult cacheResult = getCacheResult(cacheKey, annotation, returnType);
            if (cacheResult.isCached()) {
                return cacheResult.getResult();
            }

            // 获取分布式锁
            lock = redisUtils.tryLock(lockKey, requestId, lockExpiredTime, blockTime, 1000L);
            if (lock) {

                cacheResult = getCacheResult(cacheKey, annotation, returnType);
                if (cacheResult.isCached()) {
                    return cacheResult.getResult();
                }

                // 进行真实方法调用
                Object result = pjp.proceed();

                // 缓存处理结果
                setResultCache(cacheKey, annotation, result);
                return result;
            }
            throw new IdempotentException(IdempotentErrorCode.REPEATED_UNSUCCESSFUL_EXECUTION,
                    "Repeated submission of this request did not succeed");
        } finally {
            if (lock) {
                redisUtils.releaseLock(lockKey, requestId);
            }
        }
    }

    /**
     * 设置结果集缓存
     * @param cacheKey
     * @param annotation
     * @param result
     */
    private void setResultCache(String cacheKey, IdempotentCache annotation, Object result) {
        int cacheExpiredTime = annotation.cacheExpiredTime();
        CacheType cacheType = annotation.cacheType();
        switch (cacheType){
            case CACHE_RESULT:
                redisUtils.setEx(cacheKey, Optional.ofNullable(result).orElse(EXECUTED),
                        cacheExpiredTime, TimeUnit.SECONDS);
                break;
            default:
                redisUtils.setEx(cacheKey, EXECUTED, cacheExpiredTime, TimeUnit.SECONDS);
                break;
        }
    }


    /**
     * 开启缓存判断是否有结果集缓存 处理
     * @param cacheKey
     * @param annotation
     * @param returnType
     */
    private CacheResult getCacheResult(String cacheKey, IdempotentCache annotation, Class returnType) {
        CacheResult cacheResult = new CacheResult();
        CacheType cacheType = annotation.cacheType();
        String result = redisUtils.get(cacheKey);
        if (result != null) {
            cacheResult.setCached(true);
        }
        switch (cacheType) {
            case CACHE_RESULT:
                if (result != null && !EXECUTED.equals(result)) {
                    cacheResult.setResult(JSONObject.parseObject(result, returnType));
                }
                break;
            default:
                if (EXECUTED.equals(result)) {
                    throw new IdempotentException(IdempotentErrorCode.REPEATED_EXECUTION_SUCCESSFULLY_PROCESSED,
                            "Repeated submission of this request has been executed successfully");
                }
                break;
        }
        return cacheResult;
    }

}
