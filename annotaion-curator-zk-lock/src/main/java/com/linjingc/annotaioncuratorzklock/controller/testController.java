package com.linjingc.annotaioncuratorzklock.controller;

import com.linjingc.annotaioncuratorzklock.lock.LockType;
import com.linjingc.annotaioncuratorzklock.lock.annotaion.ZkLock;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @RequestMapping("test")
    @ZkLock(lockType = LockType.SemaphoreMutex)
    public String test() {
        return "success";
    }
}
