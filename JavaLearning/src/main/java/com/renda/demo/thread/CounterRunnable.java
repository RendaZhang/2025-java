package com.renda.demo.thread;

import com.renda.demo.tool.CounterInterface;

public class CounterRunnable implements Runnable {

    private final CounterInterface counter;

    public CounterRunnable(CounterInterface counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            counter.increment();
        }
    }
}
