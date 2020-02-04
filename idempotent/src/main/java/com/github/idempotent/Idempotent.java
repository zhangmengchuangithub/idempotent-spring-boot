package com.github.idempotent;

/**
 * @author zhangmc
 * @create 2020-01-17 11:01
 */
public class Idempotent {

    /**
     * 获取分布式锁
     */
    private int lockExpiredTime;

    private int cacheExpiredTime;

    private int blockTime;

    public int getLockExpiredTime() {
        return lockExpiredTime;
    }

    public void setLockExpiredTime(int lockExpiredTime) {
        this.lockExpiredTime = lockExpiredTime;
    }

    public int getCacheExpiredTime() {
        return cacheExpiredTime;
    }

    public void setCacheExpiredTime(int cacheExpiredTime) {
        this.cacheExpiredTime = cacheExpiredTime;
    }

    public int getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(int blockTime) {
        this.blockTime = blockTime;
    }
}
