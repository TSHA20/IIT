package Scenario_3;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class FloorBathroom {
    private static final int BATHROOM_STALLS = 6;
    private static final int NUM_USERS = 100;
    private static Queue<Integer> queueBathroom = new LinkedList<>();     // to track available bathroom stalls

    public static final Semaphore bathroomStall = new Semaphore(BATHROOM_STALLS); // counting semaphore
    public static final Semaphore mutex = new Semaphore(1); // binary semaphore

    public static void main(String[] args) {
        System.out.println("\n--------------Starting Bathroom Simulation-------------");
        System.out.println("Initializing " + BATHROOM_STALLS + " bathroom stalls...");

        // Populate the stall queue with stall numbers
        for (int i = 1; i <= BATHROOM_STALLS; i++) {
            queueBathroom.add(i);
        }

        // bathroom users (Array of threads)
        Thread[] users = new Thread[NUM_USERS];

        // Create and start threads for all users
        for (int i = 1; i <= NUM_USERS; i++) {
            String userType = (i % 2 == 0) ? "Student" : "Employee"; // Assign user type
            users[i - 1] = new Thread(new BathroomUser(i, userType), "User: " + i);
            users[i - 1].start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // current state
            System.out.println("| Available permits: " + bathroomStall.availablePermits());
            System.out.println("| Remaining stalls in queue: " + queueBathroom.size());
        }

        System.out.println("\n------------- Simulation in Progress-------------");

        // Wait for all thread
        for (Thread user : users) {
            try {
                user.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // After all threads finish
        System.out.println("\n----------------- Simulation Complete------------------");
        System.out.println("All " + NUM_USERS + " users have completed their tasks.");
        System.out.println("Available permits: " + bathroomStall.availablePermits());
        System.out.println("Remaining stalls in queue: " + queueBathroom.size());
        System.out.println("------------------------------------------");

    }

    public static class BathroomUser implements Runnable {
        private int userId;
        private String userType;

        public BathroomUser(int userId, String userType) {
            this.userId = userId;
            this.userType = userType;
        }

        @Override
        public void run() {
            Integer stallNumber = null;

            try {
                mutex.acquire();
                System.out.println(userType + " " + userId + " is checking stall availability");

                if (!queueBathroom.isEmpty()) {
                    stallNumber = queueBathroom.poll();          // Get a stall from the queue
                    System.out.println(userType + " " + userId + " has been assigned stall " + stallNumber);
                } else {
                    System.out.println("\n-----------------ALERT-----------------");
                    System.out.println("ALERT: No stall available for " + userType + " " + userId);
                    System.out.println("------------------------------------------");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mutex.release();
            }

            if (stallNumber != null) {
                useBathroomStall(stallNumber);                  // Use the assigned stall
                // Return the stall to the queue after use
                try {
                    mutex.acquire();
                    queueBathroom.add(stallNumber);         // Add the stall back to the queue
                    System.out.println(userType + " " + userId + " has returned stall " + stallNumber + " to the queue");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mutex.release();
                }
            }
        }

        // Simulate using the bathroom stall
        public void useBathroomStall(int stallNumber) {
            try {
                bathroomStall.acquire();        // Acquire a stall permit
                System.out.println("\n>>> " + userType + " " + userId + " has entered bathroom stall " + stallNumber);

                // Simulate time spent in the stall (3000 to 5000 ms)
                int bathroomUsageTime = (int) (Math.random() * 2000) + 3000;
                System.out.println(userType + " " + userId + " will use stall " + stallNumber + " for " + bathroomUsageTime + "ms");
                Thread.sleep(bathroomUsageTime);

                System.out.println("User " + userId + " (" + userType + ") has left bathroom stall " + stallNumber);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                bathroomStall.release();
            }
        }
    }
}
