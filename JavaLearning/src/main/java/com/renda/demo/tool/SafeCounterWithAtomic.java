package com.renda.demo.tool;

import java.util.concurrent.atomic.AtomicInteger;

public class SafeCounterWithAtomic implements CounterInterface {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void increment() {
        counter.incrementAndGet();
    }

    @Override
    public int getCounter() {
        return counter.get();
    }
}
