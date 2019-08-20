package com.linjingc.annotaioncuratorzklock.lock.core;


import com.linjingc.annotaioncuratorzklock.lock.annotaion.ZkLock;
import com.linjingc.annotaioncuratorzklock.lock.basiclock.Lock;
import com.linjingc.annotaioncuratorzklock.lock.basiclock.LockFactory;
import com.linjingc.annotaioncuratorzklock.lock.model.LockInfo;
import lombok.Data;
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
     * 该锁是否已经处理释放操作
     */
    private ThreadLocal<LockRes> currentThreadLockRes = new ThreadLocal<>();


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
        //设置当前锁状态
        currentThreadLockRes.set(new LockRes(lockInfo, false));
        //加锁
        boolean carryLock = lock.acquire();
        if (!carryLock) {
            if (log.isWarnEnabled()) {
                log.warn("Timeout while acquiring Lock({})", lockInfo.getNodePath());
            }
        }
        System.out.println("加锁成功");


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
        //释放锁
        releaseLock(zkLock, joinPoint);
        //清理线程副本
        cleanUpThreadLocal();
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
        //释放锁
        releaseLock(zkLock, joinPoint);
        //清理线程副本
        cleanUpThreadLocal();
        throw ex;
    }


    /**
     * 当前线程锁状态
     */
    @Data
    private class LockRes {

        private LockInfo lockInfo;
        /**
         * 当前锁是否执行释放操作过  true 执行 false 未执行
         */
        private Boolean useState;

        LockRes(LockInfo lockInfo, Boolean useState) {
            this.lockInfo = lockInfo;
            this.useState = useState;
        }
    }

    /**
     * 清除当前线程副本
     */
    private void cleanUpThreadLocal() {
        currentThreadLockRes.remove();
        currentThreadLock.remove();
    }

    /**
     * 释放锁 避免重复释放锁
     * 如: 执行完毕释放一次 throw时又释放一次
     */
    private void releaseLock(ZkLock zkLock, JoinPoint joinPoint) throws Throwable {
        LockRes lockRes = currentThreadLockRes.get();
        //未执行过释放锁操作
        if (!lockRes.getUseState()) {
            boolean releaseRes = currentThreadLock.get().release();
            // avoid release lock twice when exception happens below
            lockRes.setUseState(true);
            if (!releaseRes) {
                System.out.println("aop解锁失败");
//                handleReleaseTimeout(catLock, lockRes.getLockInfo(), joinPoint);
            }
        }
    }

}
