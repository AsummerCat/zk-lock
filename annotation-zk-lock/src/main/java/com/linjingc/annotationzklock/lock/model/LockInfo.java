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
     * 临时目录分隔符
     */
    public final static String SEPARATOR_CHARACTER="/";

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


    /**
     * 获取当前节点位置路径
     * @return
     */
    public String getNodePath(){
        return lockPath+SEPARATOR_CHARACTER+node;
    }
    /**
     * 获取当前前一个节点位置路径
     * @return
     */
    public String getLastNodePath(){
        return lockPath+SEPARATOR_CHARACTER+lastNode;
    }

}
