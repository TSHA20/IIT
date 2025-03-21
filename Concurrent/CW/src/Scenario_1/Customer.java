package Scenario_1;

public class Customer implements Runnable {
    private CoffeeShop coffeeShop;
    private String order;

    public Customer(CoffeeShop coffeeShop, String order) {
        this.coffeeShop = coffeeShop;
        this.order = order;
    }

    @Override
    public void run() {
        for (int i = 0; i <= 5; i++) {
            System.out.println(Thread.currentThread().getName() + " is waiting in line to place order.");
            coffeeShop.placeOrder(order);
            System.out.println(Thread.currentThread().getName() + " placed order successfully.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
