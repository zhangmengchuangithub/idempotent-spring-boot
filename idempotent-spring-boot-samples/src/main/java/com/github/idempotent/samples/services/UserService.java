package com.github.idempotent.samples.services;

import com.github.idempotent.samples.entity.User;

/**
 * @author zhangmc
 * @create 2020-01-19 10:44
 */
public interface UserService {

    /**
     * 增加用户 单个入参 指定field作为唯一条件 实现设定时间内幂等 缓存执行标志 方法被执行后直接抛IdempotentParallelException</br>
     * 并发执行直接抛IdempotentParallelException
     * @param user
     * @return
     */
    int addUserDemo1(User user);

    /**
     * 增加用户 单个入参 指定field作为唯一条件 实现设定时间内幂等 缓存执行结果 方法被执行后直接返回缓存结果
     * 并发执行直接抛IdempotentParallelException
     * @param user
     * @return
     */
    int addUserDemo2(User user);

    /**
     * 增加用户 没有参数 抛出IdempotentParallelException
     * @return
     */
    int addUserDemo3();

    /**
     * 增加用户 单个或者多个参数 有任意一个指定的field在所有参数获取不到值 指定field作为唯一条件 抛出IdempotentParallelException
     * @return
     */
    int addUserDemo4(User user);

    /**
     * 增加用户 单个或者多个参数全部为空 未指定field作为唯一条件 抛出IdempotentParallelException
     * @return
     */
    int addUserDemo5(User user);

    /**
     * 增加用户 多个参数 任意一个参数中有指定field的值 从左到右依次获取 指定field作为唯一条件 实现设定时间内幂等 缓存执行标志</br>
     * 方法被执行后直接抛IdempotentParallelException</br>
     * 并发执行直接抛IdempotentParallelException
     * @param userOld
     * @param userNew
     * @return
     */
    int addUserDemo6(User userOld, User userNew);

    /**
     * 增加用户 多个参数 任意一个参数中有指定field的值 从左到右依次获取 指定field作为唯一条件 实现设定时间内幂等 缓存执行结果</br>
     * 方法被执行后直接返回缓存结果</br>
     * 并发执行直接抛IdempotentParallelException
     * @param userOld
     * @param userNew
     * @return
     */
    int addUserDemo7(User userOld, User userNew);

    /**
     * 增加用户 多个参数 未指定field作为唯一条件 用全部参数MD5作为唯一key 实现设定时间内幂等 缓存执行标志</br>
     * 方法被执行后直接抛IdempotentParallelException</br>
     * 并发执行直接抛IdempotentParallelException
     * @param userOld
     * @param userNew
     * @return
     */
    int addUserDemo8(User userOld, User userNew);

    /**
     * 增加用户 多个参数 未指定field作为唯一条件 用全部参数MD5作为唯一key 实现设定时间内幂等 缓存执行结果</br>
     * 方法被执行后直接返回缓存结果</br>
     * 并发执行直接抛IdempotentParallelException
     * @param userOld
     * @param userNew
     * @return
     */
    int addUserDemo9(User userOld, User userNew);

}
