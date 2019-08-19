package com.linjingc.annotaioncuratorzklock.lock;

public enum LockType {
    /**
     * 可重入锁
     */
    Mutex,
    /**
     * 公平锁
     */
    SemaphoreMutex,
    /**
     * 读锁
     */
    Read,
    /**
     * 写锁
     */
    Write,
    /**
     * 联锁
     */
    MultiLock;

    LockType() {
    }

}