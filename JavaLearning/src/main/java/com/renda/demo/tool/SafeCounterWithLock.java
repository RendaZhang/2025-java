package com.renda.demo.tool;

import java.util.concurrent.locks.ReentrantLock;

public class SafeCounterWithLock implements CounterInterface {
    private final ReentrantLock lock = new ReentrantLock();
    private int counter;

    @Override
    public void increment() {
        lock.lock();
        try {
            counter++;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getCounter() {
        return counter;
    }
}
