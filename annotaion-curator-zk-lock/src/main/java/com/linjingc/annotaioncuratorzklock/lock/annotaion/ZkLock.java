package com.linjingc.annotaioncuratorzklock.lock.annotaion;

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


}
