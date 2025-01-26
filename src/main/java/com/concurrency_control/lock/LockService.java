package com.concurrency_control.lock;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class LockService {

    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public ReentrantLock acquireLock(String lockName) {
        ReentrantLock lock = locks.computeIfAbsent(lockName, key -> new ReentrantLock());
        lock.lock();
        return lock;
    }

    public void releaseLock(ReentrantLock lock) {
        lock.unlock();
    }
}