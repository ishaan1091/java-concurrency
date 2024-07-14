package com.concurrency.rendezvous;

import java.util.concurrent.*;

public class RendezVous {
    private static class Runner implements Runnable {

        private final String name;
        private final Semaphore s1, s2;

        public Runner(String name, Semaphore s1, Semaphore s2) {
            this.name = name;
            this.s1 = s1;
            this.s2 = s2;
        }

        @Override
        public void run() {
            try {
                System.out.println(name + "1");
                this.s1.release();
                this.s2.acquire();
                System.out.println(name + "2");
            } catch (InterruptedException ex) {
                System.out.println("Got exception while executing code for runner : " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            Semaphore a = new Semaphore(0), b = new Semaphore(0);

            Thread t1 = new Thread(new Runner("a", a, b));

            Thread t2 = new Thread(new Runner("b", b, a));

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
