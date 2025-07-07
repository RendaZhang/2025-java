package com.renda.demo;

import com.renda.demo.thread.RendaRunnable;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomThreadPoolMain {

    public static void main(String[] args) {
        try (ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2, // Core Pool size
                4, // Maximum Pool Size
                60, // Keep-alive time for non-core threads
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10), // Bounded tasks queue (max waiting tasks)
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy() // rejection policy (throws exception)
        )) {
            for (int i = 0; i < 10; i++) {
                executor.submit(new RendaRunnable(i));
            }
            executor.shutdown();
        }
    }
}
