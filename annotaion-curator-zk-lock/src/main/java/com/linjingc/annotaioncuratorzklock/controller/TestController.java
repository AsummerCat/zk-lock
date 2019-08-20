package com.linjingc.annotaioncuratorzklock.controller;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 基础锁测试
 *
 * @author cxc
 * @date 2019年8月20日18:10:26
 */
@RestController
public class TestController {

    @Autowired
    private CuratorFramework curatorFramework;


    /**
     * 写锁
     * 只能存在一个
     *
     * @return
     */
    @RequestMapping("test")
    public String test() {
        InterProcessReadWriteLock interProcessReadWriteLock = new InterProcessReadWriteLock(curatorFramework, "/lock/test");
        try {
            boolean acquire = interProcessReadWriteLock.writeLock().acquire(4, TimeUnit.SECONDS);
            boolean acquire1 = interProcessReadWriteLock.writeLock().acquire(4, TimeUnit.SECONDS);
            if (acquire) {
                System.out.println("加锁成功");
            }
            if (acquire1) {
                System.out.println("加锁成功1");
            }

            interProcessReadWriteLock.writeLock().release();
            interProcessReadWriteLock.writeLock().release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }


    /**
     * 读锁
     * 能存在多个
     *
     * @return
     */
    @RequestMapping("test1")
    public String test1() {
        InterProcessReadWriteLock interProcessReadWriteLock = new InterProcessReadWriteLock(curatorFramework, "/lock/test");
        try {
            boolean acquire = interProcessReadWriteLock.readLock().acquire(4, TimeUnit.SECONDS);
            boolean acquire1 = interProcessReadWriteLock.readLock().acquire(4, TimeUnit.SECONDS);
            if (acquire) {
                System.out.println("加锁成功");
            }
            if (acquire1) {
                System.out.println("加锁成功1");
            }

            interProcessReadWriteLock.readLock().release();
            interProcessReadWriteLock.readLock().release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    /**
     * 可重入锁
     * 能存在多个 但是必须都解锁
     *
     * @return
     */
    @RequestMapping("test2")
    public String test2() {
        InterProcessMutex interProcessMutex = new InterProcessMutex(curatorFramework, "/lock/test");
        try {
            boolean acquire = interProcessMutex.acquire(4, TimeUnit.SECONDS);
            boolean acquire1 = interProcessMutex.acquire(4, TimeUnit.SECONDS);
            if (acquire) {
                System.out.println("加锁成功");
            }
            if (acquire1) {
                System.out.println("加锁成功1");
            }

            interProcessMutex.release();
            interProcessMutex.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    /**
     * 不可重入锁
     * 只能存在一个
     *
     * @return
     */
    @RequestMapping("test3")
    public String test3() {
        InterProcessSemaphoreMutex interProcessSemaphoreMutex = new InterProcessSemaphoreMutex(curatorFramework, "/lock/test");
        try {
            boolean acquire = interProcessSemaphoreMutex.acquire(4, TimeUnit.SECONDS);
            if (acquire) {
                System.out.println("加锁成功");
            }
            interProcessSemaphoreMutex.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }
}

