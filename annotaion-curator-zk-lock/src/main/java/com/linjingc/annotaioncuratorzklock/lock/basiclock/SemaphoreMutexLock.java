package com.linjingc.annotaioncuratorzklock.lock.basiclock;

import com.linjingc.annotaioncuratorzklock.lock.model.LockInfo;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.TimeUnit;

/**
 * 可重入锁
 *
 * @author cxc
 * @date 2019年8月19日17:10:22
 */
@Data
@Log4j2
public class SemaphoreMutexLock implements Lock {

    private InterProcessLock interProcessLock;

    private final LockInfo lockInfo;

    private CuratorFramework curatorFramework;

    public SemaphoreMutexLock(CuratorFramework curatorFramework, LockInfo info) {
        this.curatorFramework = curatorFramework;
        this.lockInfo = info;
    }


    @Override
    public boolean acquire() {
        interProcessLock = new InterProcessMutex(curatorFramework, lockInfo.getLockPath());
        try {
            boolean acquire = interProcessLock.acquire(1000, TimeUnit.SECONDS);
            if (acquire) {
                return true;
            }
        } catch (Exception e) {
            log.error("zk加锁错误", e);
            return false;
        }
        return false;
    }

    @Override
    public boolean release() {
        boolean acquiredInThisProcess = interProcessLock.isAcquiredInThisProcess();
        if (acquiredInThisProcess) {
            try {
                interProcessLock.release();
                return true;
            } catch (Exception e) {
                log.error("zk解锁错误", e);
                return false;
            }
        }
        return false;
    }
}
