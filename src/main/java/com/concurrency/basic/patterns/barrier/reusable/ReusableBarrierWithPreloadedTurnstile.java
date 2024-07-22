package com.concurrency.basic.patterns.barrier.reusable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ReusableBarrierWithPreloadedTurnstile {
    private static final int n = 10;
    private static int counter = 0;
    private static final Semaphore mutex = new Semaphore(1);
    private static final Semaphore turnstile1 = new Semaphore(0);
    private static final Semaphore turnstile2 = new Semaphore(0);

    private static class Runner implements Runnable {

        private final int num;

        public Runner(int num) {
            this.num = num;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 5; i++) {
                    System.out.println("Rendezvous " + this.num + "." + i);

                    mutex.acquire();
                    counter = counter + 1;
                    if (counter == n) {
                        for (int j = 0;j < n;j++) {
                            turnstile1.release();
                        }
                    }
                    mutex.release();

                    turnstile1.acquire();

                    System.out.println("Critical Section " + this.num + "." + i);

                    mutex.acquire();
                    counter = counter - 1;
                    if (counter == 0) {
                        for (int j = 0;j < n;j++) {
                            turnstile2.release();
                        }
                    }
                    mutex.release();

                    turnstile2.acquire();
                }
            } catch (InterruptedException ex) {
                System.out.println("Got exception while executing code for runner : " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Thread t = new Thread(new Runner(i));
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
