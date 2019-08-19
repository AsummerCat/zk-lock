package com.linjingc.annotaioncuratorzklock.lock.core;


import com.linjingc.annotaioncuratorzklock.lock.annotaion.ZkLock;
import com.linjingc.annotaioncuratorzklock.lock.basiclock.Lock;
import com.linjingc.annotaioncuratorzklock.lock.basiclock.LockFactory;
import com.linjingc.annotaioncuratorzklock.lock.model.LockInfo;
import lombok.extern.log4j.Log4j2;
import org.apache.curator.framework.CuratorFramework;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * zk锁切面类
 * 用来包裹方法使用
 *
 * @author cxc
 * @date 2019年8月8日17:19:55
 */
@Aspect
@Component
//声明首先加载入spring
@Order(0)
@Log4j2
public class ZkLockAspectAop {
    @Autowired
    private LockInfoProvider lockInfoProvider;
    @Autowired
    private LockFactory lockFactory;

    /**
     * 当前锁
     */
    private ThreadLocal<Lock> currentThreadLock = new ThreadLocal<>();


    /**
     * 方法 环绕  加锁
     *
     * @param joinPoint 切面
     * @param zkLock    锁类型
     * @return
     * @throws Throwable
     */
    @Around(value = "@annotation(zkLock)")
    public Object around(ProceedingJoinPoint joinPoint, ZkLock zkLock) throws Throwable {
        //获取自定义锁信息
        LockInfo lockInfo = lockInfoProvider.get(joinPoint, zkLock);
        Lock lock = lockFactory.getLock(lockInfo);

        //加锁
        boolean carryLock = lock.acquire();
        if (!carryLock) {
            if (log.isWarnEnabled()) {
                log.warn("Timeout while acquiring Lock({})", lockInfo.getNodePath());
            }
        }

        currentThreadLock.set(lock);

        return joinPoint.proceed();
    }


    /**
     * 方法执行完毕 释放锁
     *
     * @param joinPoint
     * @param
     * @throws Throwable
     */
    @AfterReturning(value = "@annotation(zkLock)")
    public void afterReturning(JoinPoint joinPoint, ZkLock zkLock) throws Throwable {
        releaseLock();
    }

    /**
     * 切面 异常处理
     *
     * @param joinPoint
     * @param
     * @param ex
     * @throws Throwable
     */
    @AfterThrowing(value = "@annotation(zkLock)", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, ZkLock zkLock, Throwable ex) throws Throwable {
        releaseLock();

        throw ex;
    }



    private void releaseLock() throws Throwable {
        currentThreadLock.get().release();
        currentThreadLock.remove();
    }
}
