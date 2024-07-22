package com.concurrency.basic.patterns.rendezvous;

import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

public class RendezVousWithMultipleThreads {

    private static final int n = 10;

    private static class Runner implements Runnable {

        private final Semaphore lock;

        public Runner(Semaphore lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                System.out.println("Rendezvous");
                this.lock.release();
                this.lock.acquire();
                System.out.println("Critical Section");
                this.lock.release();
            } catch (InterruptedException ex) {
                System.out.println("Got exception while executing code for runner : " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {

            Semaphore lock = new Semaphore(1 - n);

            List<Thread> threads = new ArrayList<>();
            for (int i = 0;i < n;i++) {
                Thread t = new Thread(new Runner(lock));
                t.start();
                threads.add(t);
            }

            for (Thread t : threads) {
                t.join();
            }
        } catch (InterruptedException ex) {
            System.out.println("Got exception while executing code for runner : " + ex.getMessage());
        }

        System.out.println("Completed");
    }
}
