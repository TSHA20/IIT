package Scenario_1;

public class Barista implements Runnable {
    private CoffeeShop coffeeShop;

    public Barista(CoffeeShop coffeeShop) {
        this.coffeeShop = coffeeShop;
    }

    @Override
    public void run() {
        for (int i = 0; i <= 5; i++) {
            System.out.println(Thread.currentThread().getName() + " is starting order: " + i);
            coffeeShop.prepareOrder();
            System.out.println(Thread.currentThread().getName() + " prepared order: " + i + " successfully.");
            try {
                System.out.println(Thread.currentThread().getName() + " is taking a short break...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}