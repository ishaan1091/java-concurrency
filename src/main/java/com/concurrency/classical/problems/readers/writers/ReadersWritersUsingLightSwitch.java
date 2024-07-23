package com.concurrency.classical.problems.readers.writers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ReadersWritersUsingLightSwitch {

    private static final Semaphore criticalSectionFree = new Semaphore(1);
    private static final List<String> shared = new ArrayList<>();
    private static final LightSwitch lightSwitch = new LightSwitch();

    private static class LightSwitch {

        private final Semaphore mutex;
        private int counter;


        public LightSwitch() {
            this.mutex = new Semaphore(1);
            this.counter = 0;
        }

        public void lock(Semaphore criticalSectionLock) throws InterruptedException {
            mutex.acquire();
            if (counter == 0) {
                criticalSectionLock.acquire();
            }
            counter++;
            mutex.release();
        }

        public void unlock(Semaphore criticalSectionLock) throws InterruptedException {
            mutex.acquire();
            counter--;
            if (counter == 0) {
                criticalSectionLock.release();
            }
            mutex.release();
        }
    }

    private static class Writer implements Runnable {

        private final int id;

        public Writer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                criticalSectionFree.acquire();
                shared.add("Writer-" + id);
                criticalSectionFree.release();
            } catch (InterruptedException e) {
                System.out.println("Got exception while executing code for runner : " + e.getMessage());
            }
        }
    }

    private static class Reader implements Runnable {

        private final int id;

        public Reader(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                lightSwitch.lock(criticalSectionFree);

                for (String str : shared) {
                    System.out.println("Reader " + id + " : " + str);
                }

                if (shared.isEmpty()) {
                    System.out.println("Reader " + id + " : Empty");
                }

                lightSwitch.unlock(criticalSectionFree);
            } catch (InterruptedException e) {
                System.out.println("Got exception while executing code for runner : " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        List<Thread> readers = new ArrayList<>();
        List<Thread> writers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Thread reader = new Thread(new Reader(i + 1));
            Thread writer = new Thread(new Writer(i + 1));

            reader.start();
            writer.start();

            readers.add(reader);
            writers.add(writer);
        }

        for (int i = 0; i < 5; i++) {
            try {
                readers.get(i).join();
                writers.get(i).join();
            } catch (InterruptedException ex) {
                System.out.println("Got exception while executing code for main : " + ex.getMessage());
            }
        }

        System.out.println("Completed");
    }
}
