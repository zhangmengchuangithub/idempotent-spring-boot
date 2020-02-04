package com.github.idempotent.samples.services;

import com.github.idempotent.annotation.IdempotentCache;
import com.github.idempotent.enums.CacheType;
import com.github.idempotent.samples.entity.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangmc
 * @create 2020-01-19 15:25
 */
@Service
public class UserServiceImpl implements UserService {

    private void ThreadSleepSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static final String KEY_PREFIX1 = "1:";

    @Override
    @IdempotentCache(keyPrefix = KEY_PREFIX1, fields = "email")
    public int addUserDemo1(User user) {
        ThreadSleepSeconds(4);
        return 1;
    }


    public static final String KEY_PREFIX2 = "2:";
    @Override
    @IdempotentCache(keyPrefix = KEY_PREFIX2, fields = "email", cacheType = CacheType.CACHE_RESULT)
    public int addUserDemo2(User user) {
        ThreadSleepSeconds(4);
        return 1;
    }

    @Override
    @IdempotentCache(keyPrefix = "3:", fields = "email")
    public int addUserDemo3() {
        return 1;
    }

    @Override
    @IdempotentCache(keyPrefix = "4:", fields = "email")
    public int addUserDemo4(User user) {
        return 1;
    }

    @Override
    @IdempotentCache(keyPrefix = "5:")
    public int addUserDemo5(User user) {
        return 1;
    }

    public static final String KEY_PREFIX6 = "66:";
    @Override
    @IdempotentCache(keyPrefix = KEY_PREFIX6, fields = "email")
    public int addUserDemo6(User userOld, User userNew) {
        ThreadSleepSeconds(4);
        return 1;
    }

    public static final String KEY_PREFIX7 = "77:";
    @Override
    @IdempotentCache(keyPrefix = KEY_PREFIX7, fields = "email", cacheType = CacheType.CACHE_RESULT)
    public int addUserDemo7(User userOld, User userNew) {
        ThreadSleepSeconds(4);
        return 1;
    }

    public static final String KEY_PREFIX8 = "88888:";
    @Override
    @IdempotentCache(keyPrefix = KEY_PREFIX8)
    public int addUserDemo8(User userOld, User userNew) {
        ThreadSleepSeconds(4);
        return 1;
    }

    public static final String KEY_PREFIX9 = "99:";
    @Override
    @IdempotentCache(keyPrefix = KEY_PREFIX9, cacheType = CacheType.CACHE_RESULT)
    public int addUserDemo9(User userOld, User userNew) {
        ThreadSleepSeconds(4);
        return 1;
    }

}
