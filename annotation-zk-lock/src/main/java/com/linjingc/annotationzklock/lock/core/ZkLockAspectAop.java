package com.linjingc.annotationzklock.lock.core;


import com.linjingc.annotationzklock.lock.annotaion.ZkLock;
import com.linjingc.annotationzklock.lock.model.LockInfo;
import lombok.extern.log4j.Log4j2;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.zookeeper.CreateMode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * zk锁切面类
 * 用来包裹方法使用
 *
 * @author cxc
 * @date 2019年8月8日17:19:55
 */
@Aspect
@Component
//声明首先加载入spring
@Order(0)
@Log4j2
public class ZkLockAspectAop {
    @Autowired
    private LockInfoProvider lockInfoProvider;
    @Autowired
    private CuratorFramework zkClient;

    //当前锁
    ThreadLocal<LockInfo> currentThreadLock = new ThreadLocal<>();

    //当前节点
    ThreadLocal<String> currentPath = new ThreadLocal<>();
    //前节点
    ThreadLocal<String> beforePath = new ThreadLocal<>();


    /**
     * 方法 环绕  加锁
     *
     * @param joinPoint 切面
     * @param zkLock    锁类型
     * @return
     * @throws Throwable
     */
    @Around(value = "@annotation(zkLock)")
    public Object around(ProceedingJoinPoint joinPoint, ZkLock zkLock) throws Throwable {
        zkLock.name();
        zkLock.keys();
        //获取自定义锁信息
        LockInfo lockInfo = lockInfoProvider.get(joinPoint, zkLock);
        try {
            //根节点的初始化放在构造函数里面不生效
            if (zkClient.checkExists().forPath(lockInfo.getName()) == null) {
                System.out.println("初始化根节点==========>" + lockInfo.getName());
                zkClient.create().creatingParentsIfNeeded().forPath(lockInfo.getName());
                System.out.println("当前线程" + Thread.currentThread().getName() + "初始化根节点" + lockInfo.getName());
            }
        } catch (Exception e) {
            throw new RuntimeException("构建根节点失败");
        }
        try {
            //创建临时节点
            lockInfo.setName(zkClient.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(lockInfo.getName() + "/"));
            currentThreadLock.set(lockInfo);
        } catch (Exception e) {
            throw new RuntimeException("zk创建锁节点失败");
        }
        //检查是否获取成功锁 不成功阻塞线程
        checkLock();

        return joinPoint.proceed();
    }


    /**
     * 检查是否获取锁
     * 检查是否获取成功锁 不成功阻塞线程
     */
    private void checkLock() {
        //判断获取锁
        if (!tryLock()) {
            //监听
            waiForLock();
            //前锁解除继续判断获取
            checkLock();
        }
    }

    public void unlock() {
        try {
            zkClient.delete().guaranteed().deletingChildrenIfNeeded().forPath(currentPath.get());
            System.out.println(currentPath.get() + "解锁成功");
            currentPath.remove();
            beforePath.remove();
        } catch (Exception e) {
            //guaranteed()保障机制，若未删除成功，只要会话有效会在后台一直尝试删除
        }
    }

    /**
     * 阻塞监听节点  锁等待
     */
    private void waiForLock() {
        CountDownLatch cdl = new CountDownLatch(1);
        //创建监听器watch
        NodeCache nodeCache = new NodeCache(zkClient, beforePath.get());
        try {
            nodeCache.start(true);
            nodeCache.getListenable().addListener(new NodeCacheListener() {

                @Override
                public void nodeChanged() throws Exception {
                    System.out.println(nodeCache.getPath() + "节点监听事件触发");
                    cdl.countDown();
                }
            });
        } catch (Exception e) {
        }
        //如果前一个节点还存在，则阻塞自己
        try {
            if (zkClient.checkExists().forPath(beforePath.get()) != null) {
                cdl.await();
            }
        } catch (Exception e) {
        } finally {
            //阻塞结束，说明自己是最小的节点，则取消watch，开始获取锁
            try {
                nodeCache.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 获取锁
     *
     * @return
     */
    private boolean tryLock() {
        try {
            List<String> childrens = this.zkClient.getChildren().forPath(lockPath);
            Collections.sort(childrens);
            if (currentPath.get().equals(lockPath + "/" + childrens.get(0))) {
                System.out.println("当前线程获得锁" + currentPath.get());
                return true;
            } else {
                //取前一个节点
                int curIndex = childrens.indexOf(currentPath.get().substring(lockPath.length() + 1));
                //如果是-1表示children里面没有该节点
                beforePath.set(lockPath + "/" + childrens.get(curIndex - 1));
                System.out.println("前一个节点:" + lockPath + "/" + childrens.get(curIndex - 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
