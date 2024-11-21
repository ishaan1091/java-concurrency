package com.concurrency.classical.problems.deferred.callback;

import java.util.Comparator;
import java.util.PriorityQueue;

public class DeferredCallback {

    private static class Callback implements Comparable<Callback> {

        public long executeAtInMillis;

        public void call() {
            System.out.println("Callback called");
        }


        @Override
        public int compareTo(Callback o) {
            return Long.compare(executeAtInMillis, o.executeAtInMillis);
        }
    }

    private static class CallbackService {

        private PriorityQueue<Callback> callbacks = new PriorityQueue<>();
        private static final CallbackService INSTANCE = new CallbackService();

        private CallbackService() {

        }

        public static CallbackService getInstance() {
            return INSTANCE;
        }

        public void registerCallback(Callback callback) {

        }


    }

    public static void main(String[] args) {

    }
}
