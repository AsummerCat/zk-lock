package com.linjingc.annotaioncuratorzklock.lock.handler;

import com.linjingc.annotaioncuratorzklock.lock.model.LockInfo;

/**
 * 释放锁超时的处理逻辑接口
 *
 * @author cxc
 * @since 2019年8月8日18:19:18
 **/
public interface ReleaseTimeoutHandler {

    /**
     * 处理
     *
     * @param lockInfo 锁信息
     */
    void handle(LockInfo lockInfo);
}
