package com.linjingc.annotaioncuratorzklock.lock.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * zk 初始化配置
 *
 * @author cxc
 * @date 2019年8月13日18:33:28
 */
@Configuration
public class CuratorConfiguration {

    /**
     * 重试次数
     */
    @Value("${curator.retryCount}")
    private int retryCount;
    /**
     * 重试间隔时间
     */
    @Value("${curator.elapsedTimeMs}")
    private int elapsedTimeMs;
    /**
     * zk地址 集群 逗号隔开
     */
    @Value("${curator.connectString}")
    private String connectString;

    /**
     * 设定会话超时时间
     */
    @Value("${curator.sessionTimeoutMs}")
    private int sessionTimeoutMs;
    /**
     * 设定连接时间超时
     */
    @Value("${curator.connectionTimeoutMs}")
    private int connectionTimeoutMs;



    @Bean(initMethod = "start")
    public CuratorFramework curatorFramework() {

        return CuratorFrameworkFactory.builder()
                // 放入zookeeper服务器ip
                .connectString(connectString)
                .sessionTimeoutMs(sessionTimeoutMs)
                .connectionTimeoutMs(connectionTimeoutMs)
                //curator链接zookeeper的策略:ExponentialBackoffRetry
                .retryPolicy(new RetryNTimes(retryCount, elapsedTimeMs))
                .build();

//        return CuratorFrameworkFactory.newClient(
//                connectString,
//                sessionTimeoutMs,
//                connectionTimeoutMs,
//                new RetryNTimes(retryCount, elapsedTimeMs));
    }
}