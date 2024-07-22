package com.concurrency.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class LeaderFollowerExclusiveQueue {

    private static final Semaphore leaderMutex = new Semaphore(1);
    private static final Semaphore followerMutex = new Semaphore(1);
    private static final Semaphore leaderAvailability = new Semaphore(0);
    private static final Semaphore followerAvailability = new Semaphore(0);
    private static final Semaphore rendezvous = new Semaphore(0);

    private static class LeaderRunner implements Runnable {

        private final int id;

        public LeaderRunner(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                leaderMutex.acquire();
                leaderAvailability.release();
                followerAvailability.acquire();
                System.out.println("Leader dancing in the show : " + id);
                rendezvous.acquire();
                leaderMutex.release();
            } catch (InterruptedException ex) {
                System.out.println("Got exception while executing code for runner : " + ex.getMessage());
            }
        }
    }

    private static class FollowerRunner implements Runnable {

        private final int id;

        public FollowerRunner(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                followerMutex.acquire();
                followerAvailability.release();
                leaderAvailability.acquire();
                System.out.println("Follower dancing in the show : " + id);
                rendezvous.release();
                followerMutex.release();
            } catch (InterruptedException ex) {
                System.out.println("Got exception while executing code for runner : " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        List<Thread> leaders = new ArrayList<>();
        List<Thread> followers = new ArrayList<>();
        for (int i = 0;i < 5;i++) {
            Thread leader = new Thread(new LeaderRunner(i + 1));
            Thread follower = new Thread(new FollowerRunner(i + 1));

            leader.start();
            follower.start();

            leaders.add(leader);
            followers.add(follower);
        }

        for (int i = 0;i < 5;i++) {
            try {
                leaders.get(i).join();
                followers.get(i).join();
            } catch (InterruptedException ex) {
                System.out.println("Got exception while executing code for main : " + ex.getMessage());
            }
        }

        System.out.println("Completed");
    }
}
