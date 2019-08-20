package com.linjingc.annotaioncuratorzklock.lock.basiclock;

import com.linjingc.annotaioncuratorzklock.lock.model.LockInfo;
import lombok.extern.log4j.Log4j2;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 工厂模式 根据lock的类型自动加载 对应的锁类型
 *
 * @author cxc
 * @date 2019年8月8日17:50:38
 */
@Log4j2
@Component
public class LockFactory {
    @Autowired
    private CuratorFramework curatorFramework;

    public Lock getLock(LockInfo lockInfo) {
        switch (lockInfo.getType()) {
            case SemaphoreMutex:
                return new InterProcessSemaphoreMutexLock(curatorFramework, lockInfo);
            case Read:
                return new ReadLock(curatorFramework, lockInfo);
            case Write:
                return new WriteLock(curatorFramework, lockInfo);
            default:
                return new InterProcessMutexLock(curatorFramework, lockInfo);
        }
    }

}
