package com.linjingc.annotaioncuratorzklock.lock.basiclock;

import com.linjingc.annotaioncuratorzklock.lock.model.LockInfo;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

import java.util.concurrent.TimeUnit;

/**
 * 读写锁 读锁
 *
 * @author cxc
 * @date 2019年8月19日17:10:22
 */
@Data
@Log4j2
public class ReadLock implements Lock {

    private InterProcessReadWriteLock interProcessLock;

    private final LockInfo lockInfo;

    private CuratorFramework curatorFramework;

    public ReadLock(CuratorFramework curatorFramework, LockInfo info) {
        this.curatorFramework = curatorFramework;
        this.lockInfo = info;
    }


    @Override
    public boolean acquire() {
        interProcessLock = new InterProcessReadWriteLock(curatorFramework, lockInfo.getLockPath());
        try {
            boolean acquire = interProcessLock.readLock().acquire(1000, TimeUnit.SECONDS);
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
        boolean acquiredInThisProcess = interProcessLock.readLock().isAcquiredInThisProcess();
        if (acquiredInThisProcess) {
            try {
                interProcessLock.readLock().release();
                System.out.println("解锁成功");
                return true;
            } catch (Exception e) {
                log.error("zk解锁错误", e);
                return false;
            }
        }
        return false;
    }
}
