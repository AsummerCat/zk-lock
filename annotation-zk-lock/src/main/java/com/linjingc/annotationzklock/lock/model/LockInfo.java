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
     * 当前锁节点
     */
    private String node;

    /**
     * 当前锁 前一节点
     */
    private String lastNode;


    /**
     * 当前锁的索引路径
     */
    private String lockPath;
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

    public LockInfo(String lockPath) {
        this.lockPath = lockPath;
    }
}
