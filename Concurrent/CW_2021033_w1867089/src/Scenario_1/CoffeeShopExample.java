package Scenario_1;

import java.util.ArrayList;
import java.util.List;

public class CoffeeShopExample {
    public static void main(String[] args) {
        CoffeeShop coffeeShop = new CoffeeShop(5); // Create a coffee shop with a limited order queue capacity
        List<Thread> threads = new ArrayList<>(); // Use a list to hold threads

        // Create baristas and customers
        createBaristas(4, coffeeShop, threads);
        createCustomers(4, coffeeShop, threads);

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("All customers and baristas have finished their work.");
    }

    //create the barista threads and adds to thread list.
    private static void createBaristas(int count, CoffeeShop coffeeShop, List<Thread> threads) {
        for (int i = 1; i <= count; i++) {
            Thread barista = new Thread(new Barista(coffeeShop), "Barista " + i);
            threads.add(barista);               // Add the barista thread to the list
        }
    }

    //create the customer threads and adds to thread list.
    private static void createCustomers(int count, CoffeeShop coffeeShop, List<Thread> threads) {
        for (int i = 1; i <= count; i++) {
            Thread customer = new Thread(new Customer(coffeeShop, "Order " + i), "Customer " + i);
            threads.add(customer);              // Add the customer thread to the list
        }
    }
}