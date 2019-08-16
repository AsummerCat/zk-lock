package com.linjingc.annotationzklock.controller;

import com.linjingc.annotationzklock.lock.annotaion.ZkLock;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @ZkLock(name = "小明")
    @RequestMapping("test")
    public String test(){


        return "success";
    }
}
