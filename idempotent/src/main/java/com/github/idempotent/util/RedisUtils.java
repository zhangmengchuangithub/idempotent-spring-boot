package com.github.idempotent.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangmc
 * @create 2020-01-17 15:10
 */
public class RedisUtils {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * lua脚本操作成功标识
     */
    private static final int LOCK_SUCCESS = 1;

    /**
     * 释放锁lua脚本
     */
    private static final String RELEASE_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    /**
     * 设置key value
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置key value
     * @param key
     * @param value     支持泛型 json序列化为字符串
     */
    public <T> void set(String key, T value) {
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(value));
    }

    public static void main(String[] args) {
        Integer x = null;
        System.out.println(JSONObject.toJSONString(" "));
        System.out.println(StringUtils.isEmpty(" "));
    }

    /**
     * 根据key获取value
     * @param key
     * @return
     */
    public String get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 根据key获取value 根据类型Json反序列化返回
     * @param key
     * @param type
     * @return
     */
    public <T> T get(String key, TypeReference<T> type){
        String value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value).map(o->JSONObject.parseObject(o, type)).orElse(null);
    }

    /**
     * 根据key获取value 根据类型Json反序列化返回
     * @param key
     * @param clazz
     * @return
     */
    public <T> T get(String key, Class<T> clazz){
        String value = redisTemplate.opsForValue().get(key);
        if (String.class == clazz) {
            return Optional.ofNullable(value).map(o->(T)o).orElse(null);
        } else {
            return Optional.ofNullable(value).map(o->JSONObject.parseObject(o, clazz)).orElse(null);
        }
    }

    /**
     * 删除key
     * @param key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 将值 value 关联到 key ，并将 key 的过期时间设为 timeout
     * @param key
     * @param value
     * @param timeout 过期时间
     * @param unit    时间单位, 天:TimeUnit.DAYS 小时:TimeUnit.HOURS 分钟:TimeUnit.MINUTES
     *                秒:TimeUnit.SECONDS 毫秒:TimeUnit.MILLISECONDS
     */
    public void setEx(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 将值 value 关联到 key ，并将 key 的过期时间设为 timeout
     * @param key
     * @param value   支持泛型
     * @param timeout 过期时间
     * @param unit    时间单位, 天:TimeUnit.DAYS 小时:TimeUnit.HOURS 分钟:TimeUnit.MINUTES
     *                秒:TimeUnit.SECONDS 毫秒:TimeUnit.MILLISECONDS
     */
    public <T> void setEx(String key, T value, long timeout, TimeUnit unit) {
        if (value instanceof String) {
            redisTemplate.opsForValue().set(key, value.toString(), timeout, unit);
        } else {
            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(value), timeout, unit);
        }
    }

    /**
     * 尝试获取锁
     * @param lockKey
     * @param requestId
     * @param expireSecond
     * @return
     */
    public boolean tryLock(String lockKey, String requestId, int expireSecond) {
        return redisTemplate.opsForValue()
                .setIfAbsent(lockKey, requestId, expireSecond, TimeUnit.SECONDS);
    }

    /**
     * 获取阻塞锁
     * @param lockKey 锁ID
     * @param requestId 请求Id
     * @param expireSecond 过期时间
     * @param blockMilliSecond 阻塞的毫秒数
     * @param sleepMilliSecond 每次请求睡眠的毫秒数
     * @return
     */
    public boolean tryLock(String lockKey, String requestId, int expireSecond, long blockMilliSecond, long sleepMilliSecond) {
        boolean lock;
        do {
            lock = tryLock(lockKey, requestId, expireSecond);
            if (lock) {
                return true;
            } else {
                blockMilliSecond -= sleepMilliSecond;
                try {
                    TimeUnit.MILLISECONDS.sleep(sleepMilliSecond);
                } catch (InterruptedException e) {
                    return false;
                }
            }
        } while (!lock && blockMilliSecond > 0);
        return false;
    }

    /**
     * 释放锁
     * @param lockKey   锁的key
     * @param requestId 加锁的请求ID
     * @return
     */
    public boolean releaseLock(String lockKey, String requestId) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(RELEASE_LOCK_SCRIPT);
        redisScript.setResultType(Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
        return result != null && LOCK_SUCCESS == result;
    }

}
