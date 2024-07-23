package com.concurrency.classical.problems.producer.consumer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class FiniteBufferProducerConsumer {
    private static final int bufferSize = 3;
    private static final Semaphore mutex = new Semaphore(1);
    private static final Semaphore events = new Semaphore(0);
    private static final Semaphore capacity = new Semaphore(bufferSize);
    private static final Queue<String> buffer = new LinkedList<>();

    private static class ProducerRunner implements Runnable {

        private final int id;

        public ProducerRunner(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                System.out.println("Producer waiting for event");
                Thread.sleep(1000L * id);
                capacity.acquire();
                System.out.println("Capacity available to push in buffer : " + id);
                mutex.acquire();
                buffer.add("Event from producer : " + id);
                mutex.release();
                events.release();
                System.out.println("Pushed event from producer : " + id);
            } catch (InterruptedException ex) {
                System.out.println("Got exception while executing code for runner : " + ex.getMessage());
            }
        }
    }

    private static class ConsumerRunner implements Runnable {

        private final int id;

        public ConsumerRunner(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                events.acquire();
                mutex.acquire();
                String event = buffer.poll();
                mutex.release();
                capacity.release();
                System.out.println("Consumer " + id + " processed event : " + event);
            } catch (InterruptedException ex) {
                System.out.println("Got exception while executing code for runner : " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        List<Thread> producers = new ArrayList<>();
        List<Thread> consumers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Thread producer = new Thread(new ProducerRunner(i + 1));
            Thread consumer = new Thread(new ConsumerRunner(i + 1));

            producer.start();
            consumer.start();

            producers.add(producer);
            consumers.add(consumer);
        }

        for (int i = 0; i < 5; i++) {
            try {
                producers.get(i).join();
                consumers.get(i).join();
            } catch (InterruptedException ex) {
                System.out.println("Got exception while executing code for main : " + ex.getMessage());
            }
        }

        System.out.println("Completed");
    }
}
