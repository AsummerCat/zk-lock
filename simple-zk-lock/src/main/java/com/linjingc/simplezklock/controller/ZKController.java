package com.linjingc.simplezklock.controller;

import com.linjingc.simplezklock.zklock.ZKlock;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/zk")
public class ZKController {

    @Autowired
    private CuratorFramework zkClient;
//    @Autowired
//    private ZkClient zkClient;

    @Autowired
    private ZKlock zklock;

    @GetMapping("/lock")
    public Boolean getLock() throws Exception {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(() -> {
            zklock.lock();
        });
        Thread.sleep(1000);
        for (int i = 0; i < 10; i++) {
            cachedThreadPool.execute(() -> {
                zklock.lock();
                zklock.unlock();
            });
        }
        return true;
    }
}