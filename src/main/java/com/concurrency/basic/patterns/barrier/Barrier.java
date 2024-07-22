package com.concurrency.basic.patterns.barrier;

import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

public class Barrier {

    private static final int n = 10;
    private static int counter = 0;
    private static final Semaphore mutex = new Semaphore(1);
    private static final Semaphore barrier = new Semaphore(0);

    private static class Runner implements Runnable {

        @Override
        public void run() {
            try {
                System.out.println("Rendezvous");

                mutex.acquire();
                counter = counter + 1;
                if (counter == n) {
                    barrier.release();
                }
                mutex.release();

                barrier.acquire();
                barrier.release();

                System.out.println("Critical Section");
            } catch (InterruptedException ex) {
                System.out.println("Got exception while executing code for runner : " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0;i < n;i++) {
            Thread t = new Thread(new Runner());
            t.start();
            threads.add(t);
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                System.out.println("Got exception while executing code for runner : " + ex.getMessage());
            }
        }


        System.out.println("Completed");
    }
}
