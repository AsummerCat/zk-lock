package com.linjingc.annotaioncuratorzklock.controller;

import com.linjingc.annotaioncuratorzklock.lock.annotaion.LockKey;
import com.linjingc.annotaioncuratorzklock.lock.annotaion.ZkLock;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    /**
     * 默认使用全路径
     *
     * @return
     */
    @ZkLock()
    @RequestMapping("test")
    public String test() {
        return "success";
    }

    @ZkLock()
    @RequestMapping("test1")
    public String test1(@LockKey String test) {
        return "success";
    }
}
