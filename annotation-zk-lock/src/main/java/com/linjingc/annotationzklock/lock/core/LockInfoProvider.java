package com.linjingc.annotationzklock.lock.core;


import com.linjingc.annotationzklock.lock.annotaion.ZkLock;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.linjingc.annotationzklock.lock.model.LockInfo;


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
    public static final String LOCK_NAME_PREFIX = "/Lock";
    public static final String LOCK_NAME_SEPARATOR = "/";


//    @Autowired
//    private RedisLockConfig redisLockConfig;
//
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
        //获取到切面的信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //根据自定义业务key 获取keyName
        String businessKeyName = businessKeyProvider.getKeyName(joinPoint, zkLock);
        //拼接lockName地址
        String lockPath = LOCK_NAME_PREFIX + LOCK_NAME_SEPARATOR + getName(zkLock.name(), signature)+businessKeyName;
        //实例化锁
        return new LockInfo(lockPath);
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


}
