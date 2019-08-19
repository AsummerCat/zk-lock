package com.linjingc.annotaioncuratorzklock.lock.annotaion;

import com.linjingc.annotaioncuratorzklock.lock.LockType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这个注解用来标记 参数 使用key lock
 *
 * @author cxc
 * @date 2019年8月8日17:52:34
 */
@Target(value = {ElementType.PARAMETER, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface LockKey {
    String value() default "";

    /**
     * 锁类型，默认可重入锁
     *
     * @return lockType
     */
    LockType lockType() default LockType.Mutex;

//    /**
//     * 尝试加锁，最多等待时间
//     *
//     * @return waitTime
//     */
//    long waitTime() default Long.MIN_VALUE;

    /**
     * 自定义业务key
     *
     * @return keys
     */
    String[] keys() default {};


}
