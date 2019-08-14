package com.linjingc.simplezklock.zklock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component("zklock")
public class ZKlock {

    @Autowired
    private CuratorFramework zkClient;

    //这里后期需要修改
    //这边固定了一个key
    //需要注意的是 现在有个问题 因为是监听临时节点 如果节点被删除了 可能就会产生锁雪崩的情况
    @Value("${curator.lockPath}")
    private String lockPath;

    ThreadLocal<String> currentPath = new ThreadLocal<>();
    ThreadLocal<String> beforePath = new ThreadLocal<>();


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

    public void lock() {
        try {
            //根节点的初始化放在构造函数里面不生效
            if (zkClient.checkExists().forPath(lockPath) == null) {
                System.out.println("初始化根节点==========>" + lockPath);
                zkClient.create().creatingParentsIfNeeded().forPath(lockPath);
                System.out.println("当前线程" + Thread.currentThread().getName() + "初始化根节点" + lockPath);
            }
        } catch (Exception e) {
            throw new RuntimeException("构建根节点失败");
        }
        try {
            currentPath.set(zkClient.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(lockPath + "/"));
        } catch (Exception e) {
            throw new RuntimeException("zk加锁失败");
        }

        checkLock();
    }


    /**
     * 检查是否获取锁
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


}