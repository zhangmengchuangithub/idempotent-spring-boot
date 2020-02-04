package com.github.idempotent.samples.services;

import com.github.idempotent.aop.IdempotentCacheAspect;
import com.github.idempotent.exception.IdempotentException;
import com.github.idempotent.samples.BaseTest;
import com.github.idempotent.samples.entity.User;
import com.github.idempotent.util.RedisUtils;
import com.github.idempotent.util.UniqueKeyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangmc
 * @create 2020-01-19 15:27
 */

public class UserServiceTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtils redisUtils;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private static final int RESULT = 1;

    private User user;

    private Object[] singleParameter;

    private Object[] multipleParameters;

    private User userNew;

    private static final String[] fields = {"email"};

    @Before
    public void before(){
        user = new User();
        user.setEmail("190933370@qq.com");
        user.setName("张梦川");
        user.setAge(29);
        user.setGender((byte)1);

        userNew = new User();
        BeanUtils.copyProperties(user, userNew);

        singleParameter = new Object[]{user};

        multipleParameters = new Object[]{null, user};
    }

    private void ThreadSleepSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void latchaAwait(final CountDownLatch latch){
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void addUserDemo1() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        new Thread(() ->{
            int result = userService.addUserDemo1(user);
            Assert.assertEquals(RESULT, result);
            latch.countDown();
        }).start();
        new Thread() {
            @Rule
            private final ExpectedException exception = ExpectedException.none();
            @Override
            public void run() {
                try {
                    ThreadSleepSeconds(2);
                    exception.expect(IdempotentException.class);
                    exception.expectMessage("Repeated submission of this request did not succeed");
                    userService.addUserDemo1(user);
                } finally {
                    latch.countDown();
                }
            }
        }.start();
        new Thread() {
            @Rule
            private final ExpectedException exception = ExpectedException.none();
            @Override
            public void run() {
                try {
                    ThreadSleepSeconds(6);
                    exception.expect(IdempotentException.class);
                    exception.expectMessage("Repeated submission of this request has been executed successfully");
                    userService.addUserDemo1(user);
                } finally {
                    latch.countDown();
                }
            }
        }.start();
        latchaAwait(latch);
        String uniqueKey = UniqueKeyUtils.getUniqueKey(singleParameter, fields);
        String cacheKey = UserServiceImpl.KEY_PREFIX1
                .concat(uniqueKey).concat(IdempotentCacheAspect.CACHE);
        redisUtils.delete(cacheKey);
    }

    @Test
    public void addUserDemo2() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        new Thread(() ->{
            int result = userService.addUserDemo2(user);
            Assert.assertEquals(RESULT, result);
            latch.countDown();
        }).start();
        new Thread() {
            @Rule
            private final ExpectedException exception = ExpectedException.none();
            @Override
            public void run() {
                try {
                    ThreadSleepSeconds(2);
                    exception.expect(IdempotentException.class);
                    exception.expectMessage("Repeated submission of this request did not succeed");
                    userService.addUserDemo2(user);
                } finally {
                    latch.countDown();
                }
            }
        }.start();
        new Thread(() ->{
            ThreadSleepSeconds(6);
            int result = userService.addUserDemo2(user);
            Assert.assertEquals(RESULT, result);
            latch.countDown();
        }).start();
        latchaAwait(latch);
        String uniqueKey = UniqueKeyUtils.getUniqueKey(singleParameter, fields);
        String cacheKey = UserServiceImpl.KEY_PREFIX2
                .concat(uniqueKey).concat(IdempotentCacheAspect.CACHE);
        redisUtils.delete(cacheKey);
    }

    @Test
    public void addUserDemo3() {
        exception.expect(IdempotentException.class);
        exception.expectMessage("The idempotent method must have at least one parameter");
        userService.addUserDemo3();
    }

    @Test
    public void addUserDemo4() {
        exception.expect(IdempotentException.class);
        exception.expectMessage("specified field: ");
        userService.addUserDemo4(null);
    }

    @Test
    public void addUserDemo5() {
        exception.expect(IdempotentException.class);
        exception.expectMessage("Idempotent method parameters are all empty");
        userService.addUserDemo5(null);
    }

    @Test
    public void addUserDemo6() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        new Thread(() ->{
            int result = userService.addUserDemo6(null, userNew);
            Assert.assertEquals(RESULT, result);
            latch.countDown();
        }).start();
        new Thread() {
            @Rule
            private final ExpectedException exception = ExpectedException.none();
            @Override
            public void run() {
                try {
                    ThreadSleepSeconds(2);
                    exception.expect(IdempotentException.class);
                    exception.expectMessage("Repeated submission of this request did not succeed");
                    userService.addUserDemo6(null, userNew);
                } finally {
                    latch.countDown();
                }
            }
        }.start();
        new Thread() {
            @Rule
            private final ExpectedException exception = ExpectedException.none();
            @Override
            public void run() {
                try {
                    ThreadSleepSeconds(6);
                    exception.expect(IdempotentException.class);
                    exception.expectMessage("Repeated submission of this request has been executed successfully");
                    userService.addUserDemo6(null, userNew);
                } finally {
                    latch.countDown();
                }
            }
        }.start();
        latchaAwait(latch);
        String uniqueKey = UniqueKeyUtils.getUniqueKey(multipleParameters, fields);
        String cacheKey = UserServiceImpl.KEY_PREFIX6
                .concat(uniqueKey).concat(IdempotentCacheAspect.CACHE);
        redisUtils.delete(cacheKey);
    }

    @Test
    public void addUserDemo7() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        new Thread(() ->{
            int result = userService.addUserDemo7(null, userNew);
            Assert.assertEquals(RESULT, result);
            latch.countDown();
        }).start();
        new Thread() {
            @Rule
            private final ExpectedException exception = ExpectedException.none();
            @Override
            public void run() {
                try {
                    ThreadSleepSeconds(2);
                    exception.expect(IdempotentException.class);
                    exception.expectMessage("Repeated submission of this request did not succeed");
                    userService.addUserDemo7(null, userNew);
                } finally {
                    latch.countDown();
                }
            }
        }.start();
        new Thread(() ->{
            ThreadSleepSeconds(6);
            int result = userService.addUserDemo7(null, userNew);
            Assert.assertEquals(RESULT, result);
            latch.countDown();
        }).start();
        latchaAwait(latch);
        String uniqueKey = UniqueKeyUtils.getUniqueKey(multipleParameters, fields);
        String cacheKey = UserServiceImpl.KEY_PREFIX7
                .concat(uniqueKey).concat(IdempotentCacheAspect.CACHE);
        redisUtils.delete(cacheKey);
    }

    @Test
    public void addUserDemo8() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        new Thread(() ->{
            int result = userService.addUserDemo8(null, userNew);
            Assert.assertEquals(RESULT, result);
            latch.countDown();
        }).start();
        new Thread() {
            @Rule
            private final ExpectedException exception = ExpectedException.none();
            @Override
            public void run() {
                try {
                    ThreadSleepSeconds(2);
                    exception.expect(IdempotentException.class);
                    exception.expectMessage("Repeated submission of this request did not succeed");
                    userService.addUserDemo8(null, userNew);
                } finally {
                    latch.countDown();
                }
            }
        }.start();
        new Thread() {
            @Rule
            private final ExpectedException exception = ExpectedException.none();
            @Override
            public void run() {
                try {
                    ThreadSleepSeconds(8);
                    exception.expect(IdempotentException.class);
                    exception.expectMessage("Repeated submission of this request has been executed successfully");
                    userService.addUserDemo8(null, userNew);
                } finally {
                    latch.countDown();
                }
            }
        }.start();
        latchaAwait(latch);
        String uniqueKey = UniqueKeyUtils.getUniqueKey(multipleParameters, null);
        String cacheKey = UserServiceImpl.KEY_PREFIX8
                .concat(uniqueKey).concat(IdempotentCacheAspect.CACHE);
        redisUtils.delete(cacheKey);
    }

    @Test
    public void addUserDemo9() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        new Thread(() ->{
            int result = userService.addUserDemo9(null, userNew);
            Assert.assertEquals(RESULT, result);
            latch.countDown();
        }).start();
        new Thread() {
            @Rule
            private final ExpectedException exception = ExpectedException.none();
            @Override
            public void run() {
                try {
                    ThreadSleepSeconds(2);
                    exception.expect(IdempotentException.class);
                    exception.expectMessage("Repeated submission of this request did not succeed");
                    userService.addUserDemo9(null, userNew);
                } finally {
                    latch.countDown();
                }
            }
        }.start();
        new Thread(() ->{
            ThreadSleepSeconds(8);
            int result = userService.addUserDemo9(null, userNew);
            Assert.assertEquals(RESULT, result);
            latch.countDown();
        }).start();
        latchaAwait(latch);
        String uniqueKey = UniqueKeyUtils.getUniqueKey(multipleParameters, null);
        String cacheKey = UserServiceImpl.KEY_PREFIX9
                .concat(uniqueKey).concat(IdempotentCacheAspect.CACHE);
        redisUtils.delete(cacheKey);
    }
}
