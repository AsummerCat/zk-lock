package com.linjingc.simplezklock.controller;

import com.linjingc.simplezklock.zklock.ZKlock;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

//        zklock.lock();
//
//        zklock.unlock();
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    zklock.lock();

                    zklock.unlock();
                }
            }).start();
        }
        return true;
    }
}