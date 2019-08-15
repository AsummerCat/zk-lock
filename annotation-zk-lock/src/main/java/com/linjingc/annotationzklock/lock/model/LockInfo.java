package com.linjingc.annotationzklock.lock.model;

import lombok.Data;

/**
 * 锁基本 信息
 *
 * @author cxc
 * @date 2019年8月8日17:13:18
 */
@Data
public class LockInfo {

    /**
     * 锁名称
     */
    private String name;
//    /**
//     * 等待时间
//     */
//    private long waitTime;
//    /**
//     * 续约时间 ->处理时间 达到该时间会自动解锁
//     */
//    private long leaseTime;

    public LockInfo() {
    }

    public LockInfo(String name) {
        this.name = name;
    }
}
