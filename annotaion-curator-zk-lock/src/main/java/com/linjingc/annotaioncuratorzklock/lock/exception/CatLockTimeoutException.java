package com.linjingc.annotaioncuratorzklock.lock.exception;


/**
 * 自定义锁超时错误
 *
 * @author cxc
 * @date 2019年8月8日18:16:08
 */
public class CatLockTimeoutException extends RuntimeException {

    public CatLockTimeoutException() {
    }

    public CatLockTimeoutException(String message) {
        super(message);
    }

    public CatLockTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
