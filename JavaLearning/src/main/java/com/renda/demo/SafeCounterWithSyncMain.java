package com.renda.demo;

import com.renda.demo.thread.CounterRunnable;
import com.renda.demo.tool.SafeCounterWithSync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SafeCounterWithSyncMain {

    private static final int NUMBER_OF_THREADS = 10;
    private static final int EXPECTED_COUNT = 100000;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        SafeCounterWithSync counter = new SafeCounterWithSync();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            executor.submit(new CounterRunnable(counter));
        }
        executor.shutdown();

        while (!executor.isTerminated()) {
            System.out.println("Waiting for executor to terminate... ");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Interrupted while waiting for executor to terminate in Main Thread");
            }
        }

        System.out.println("Final count is: " + counter.getCounter());
        System.out.println("Expected: " + EXPECTED_COUNT);
    }

}
