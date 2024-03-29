package com.linjingc.annotaioncuratorzklock.lock.annotaion;

import com.linjingc.annotaioncuratorzklock.lock.LockType;
import com.linjingc.annotaioncuratorzklock.lock.Strategy.LockTimeoutStrategy;
import com.linjingc.annotaioncuratorzklock.lock.Strategy.ReleaseTimeoutStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ZK锁 注解
 *
 * @author cxc
 * @date 2019年8月15日18:09:36
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ZkLock {

    /**
     * 锁的名称
     *
     * @return name
     */
    String name() default "";


    /**
     * 自定义业务key
     *
     * @return keys
     */
    String[] keys() default {};

    /**
     * 锁类型，默认可重入锁
     *
     * @return lockType
     */
    LockType lockType() default LockType.Mutex;

    /**
     * 尝试加锁，最多等待时间(秒)
     *
     * @return waitTime
     */
    long waitTime() default Long.MIN_VALUE;

    /**
     * 加锁超时的处理策略
     *
     * @return lockTimeoutStrategy
     */
    LockTimeoutStrategy lockTimeoutStrategy() default LockTimeoutStrategy.FAIL_FAST;

    /**
     * 自定义加锁超时的处理策略
     *
     * @return customLockTimeoutStrategy
     */
    String customLockTimeoutStrategy() default "";
    /**
     * 释放锁时已超时的处理策略
     *
     * @return releaseTimeoutStrategy
     */
    ReleaseTimeoutStrategy releaseTimeoutStrategy() default ReleaseTimeoutStrategy.FAIL_FAST;

    /**
     * 自定义释放锁时已超时的处理策略
     *
     * @return customReleaseTimeoutStrategy
     */
    String customReleaseTimeoutStrategy() default "";


}
