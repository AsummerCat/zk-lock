package com.linjingc.annotaioncuratorzklock.controller;

import com.linjingc.annotaioncuratorzklock.lock.LockType;
import com.linjingc.annotaioncuratorzklock.lock.annotaion.ZkLock;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注解测试
 *
 * @author cxc
 * @date 2019年8月20日18:10:08
 */
@RestController
public class LockController {

    //todo 手动捕获这个错误  IllegalMonitorStateException

    /**
     * 可重入锁
     *
     * @return
     */
    @ZkLock(lockType = LockType.Mutex)
    @RequestMapping("locktest")
    public String test() {
        return "success";
    }

    /**
     * 不可重入锁
     *
     * @param test
     * @return
     */
    @ZkLock(lockType = LockType.SemaphoreMutex)
    @RequestMapping("locktest1")
    public String test1(String test) {
        return "success";
    }

    /**
     * 读锁
     *
     * @param test
     * @return
     */
    @ZkLock(lockType = LockType.Read)
    @RequestMapping("locktest2")
    public String test2(String test) {
        return "success";
    }

    /**
     * 写锁
     *
     * @param test
     * @return
     */
    @ZkLock(lockType = LockType.Write)
    @RequestMapping("locktest3")
    public String test3(String test) {
        return "success";
    }
}
