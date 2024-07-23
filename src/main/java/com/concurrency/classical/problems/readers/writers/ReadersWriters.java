package com.concurrency.classical.problems.readers.writers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ReadersWriters {

    private static final Semaphore mutex = new Semaphore(1);
    private static final Semaphore criticalSectionFree = new Semaphore(1);
    private static final List<String> shared = new ArrayList<>();
    private static int readers = 0;

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
                mutex.acquire();
                if (readers == 0) {
                    criticalSectionFree.acquire();
                }
                readers++;
                mutex.release();

                for (String str : shared) {
                    System.out.println("Reader " + id + " : " + str);
                }

                if (shared.isEmpty()) {
                    System.out.println("Reader " + id + " : Empty");
                }

                mutex.acquire();
                readers--;
                if (readers == 0) {
                    criticalSectionFree.release();
                }
                mutex.release();
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
