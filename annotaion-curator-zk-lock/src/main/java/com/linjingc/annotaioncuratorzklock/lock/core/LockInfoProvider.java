package com.linjingc.annotaioncuratorzklock.lock.core;


import com.linjingc.annotaioncuratorzklock.lock.LockType;
import com.linjingc.annotaioncuratorzklock.lock.annotaion.ZkLock;
import com.linjingc.annotaioncuratorzklock.lock.model.LockInfo;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * 锁提供者  创建锁的相关信息都在这里生成
 *
 * @author cxc
 * @date 2019年8月9日18:02:25
 */
@Component
@Log4j2
public class LockInfoProvider {

    /**
     * 锁的key根路径
     */

    @Value("${curator.lockPath}")
    private String LOCK_NAME_PREFIX;
    public static final String LOCK_NAME_SEPARATOR = "/";
    /**
     * 加锁等待时间
     */
    @Value("${curator.lockWaitTime}")
    private Long lockWaitTime;


    /**
     * 自定义业务key
     */
    @Autowired
    private BusinessKeyProvider businessKeyProvider;


    /***
     * 获取锁信息
     * 锁的名称 = 根路径+子路径+锁名
     * @param joinPoint
     * @param zkLock
     * @return
     */
    public LockInfo get(ProceedingJoinPoint joinPoint, ZkLock zkLock) {
        //获取到锁类型
        LockType type = zkLock.lockType();
        //获取到切面的信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //根据自定义业务key 获取keyName
        String businessKeyName = businessKeyProvider.getKeyName(joinPoint, zkLock);
        //拼接lockName地址
        String lockPath = LOCK_NAME_PREFIX + LOCK_NAME_SEPARATOR + getName(zkLock.name(), signature) + businessKeyName;

        //获取等待时间 不设置则根据配置的lockWaitTime的生成
        long waitTime = getWaitTime(zkLock);
        //实例化锁
        return new LockInfo(lockPath,type,waitTime);
    }


    /**
     * 获取锁名称
     *
     * @param annotationName
     * @param signature
     * @return
     */
    private String getName(String annotationName, MethodSignature signature) {
        //如果keyname没有设置 则返回方法名称
        if (annotationName.isEmpty()) {
            return String.format("%s.%s", signature.getDeclaringTypeName(), signature.getMethod().getName());
        } else {
            return annotationName;
        }
    }


    /**
     * 如果默认是最大等待时间 则使用配置项内的时间 否则 使用自定义的时间
     *
     * @param lock
     * @return
     */
    private long getWaitTime(ZkLock lock) {
        return lock.waitTime() == Long.MIN_VALUE ?
                lockWaitTime : lock.waitTime();
    }

}
