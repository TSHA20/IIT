package Scenario_1;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class CoffeeShop {
    public Queue<String> orderQueue = new LinkedList<>();      //hold customer orders
    private final int MAX_CAPACITY;

    private ReentrantLock lock = new ReentrantLock();     // explicit lock
    private Condition queueIsFull = lock.newCondition();  // condition on which the customer waits
    private Condition queueIsEmpty = lock.newCondition(); // condition on which the barista waits

    public CoffeeShop(int capacity) {
        this.MAX_CAPACITY = capacity;
    }

    public void placeOrder(String order) {
        lock.lock();
        try {
            while (orderQueue.size() == MAX_CAPACITY) {
                System.out.println(Thread.currentThread().getName() + " is waiting to place an order, queue is full.");
                try {
                    queueIsFull.await();            // if queue is full, customer waits
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            orderQueue.add(order);                 // if queue is not full , place order
            System.out.println(Thread.currentThread().getName() + " placed order: " + order);
            queueIsEmpty.signalAll();              // signal to baristas
        } finally {
            lock.unlock();
        }
    }

    public String prepareOrder() {
        lock.lock();
        try {
            while (orderQueue.isEmpty()) {
                System.out.println(Thread.currentThread().getName() + " is waiting, no orders in queue.");
                try {
                    queueIsEmpty.await();               // Barista waits until there is an order in the queue
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String order = orderQueue.poll();         // Remove and return the order from the queue
            System.out.println(Thread.currentThread().getName() + " is preparing order: " + order);
            queueIsFull.signalAll();                  // Signal to customers - available space to order
            return order;
        } finally {
            lock.unlock();
        }
    }
}
