package com.concurrency.counter;

import java.util.concurrent.*;

public class Counter {

    private static int counter = 0;
    private static Semaphore lock = new Semaphore(1);

    private static class Runner implements Runnable {

        @Override
        public void run() {
            try {
                lock.acquire();
                int currCounter = counter;
                System.out.println(currCounter);
                counter = currCounter + 1;
                System.out.println(counter);
                lock.release();
            } catch (InterruptedException ex) {
                System.out.println("Got exception while executing code for runner : " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {

            Thread t1 = new Thread(new Runner());

            Thread t2 = new Thread(new Runner());

            t1.start();
            t2.start();

            t1.join();
            t2.join();
        } catch (InterruptedException ex) {
            System.out.println("Got exception while executing code for runner : " + ex.getMessage());
        }

        System.out.println("Completed");
    }
}