package Scenario_2;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        BankAccount accountA = new BankAccount(1, new BigDecimal(1000));
        BankAccount accountB = new BankAccount(2, new BigDecimal(1000));
        BankAccount accountC = new BankAccount(3, new BigDecimal(1000));

        Map<Integer, BankAccount> accountsMap = new HashMap<>();
        accountsMap.put(accountA.getUserId(), accountA);
        accountsMap.put(accountB.getUserId(), accountB);
        accountsMap.put(accountC.getUserId(), accountC);

        // Create a TransactionSystem instance with the accounts
        TransactionSystem transactionSystem = new TransactionSystem(accountsMap);

        // Thread 1: Transfer money from A to B
        Thread thread1 = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    System.out.println("Thread 1 attempting transfer from A to B");
                    boolean success = false;
                    try {
                        success = transactionSystem.transfer(accountA, accountB, new BigDecimal(100), i);
                    } catch (InsufficientBalanceException | InvalidAmountException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Thread 1 transfer " + (success ? "succeeded" : "failed"));
                    Thread.sleep(100); // Small delay between transactions
                }
            } catch (Exception e) {
                System.out.println("Thread 1 error: " + e.getMessage());
            }
        }, "TransferThread-1");

        // Thread 2: Transfer money from B to C
        Thread thread2 = new Thread(() -> {
            try {
                for (int i = 5; i < 10; i++) {
                    System.out.println("Thread 2 attempting transfer from B to C");
                    boolean success = false;
                    try {
                        success = transactionSystem.transfer(accountB, accountC, new BigDecimal(150), i);
                    } catch (InsufficientBalanceException | InvalidAmountException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Thread 2 transfer " + (success ? "succeeded" : "failed"));
                    Thread.sleep(150); // added delay between transactions
                }
            } catch (Exception e) {
                System.out.println("Thread 2 error: " + e.getMessage());
            }
        }, "TransferThread-2");

        // Thread 3: Transfer money from C to A
        Thread thread3 = new Thread(() -> {
            try {
                for (int i = 10; i < 15; i++) {
                    System.out.println("Thread 3 attempting transfer from C to A");
                    boolean success = false;
                    try {
                        success = transactionSystem.transfer(accountC, accountA, new BigDecimal(200), i);
                    } catch (InsufficientBalanceException | InvalidAmountException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Thread 3 transfer " + (success ? "succeeded" : "failed"));
                    Thread.sleep(200); // Small delay between transactions
                }
            } catch (Exception e) {
                System.out.println("Thread 3 error: " + e.getMessage());
            }
        }, "TransferThread-3");

        // Print initial balances
        System.out.println("\nInitial Account Balances:");
        System.out.println("Account A: " + accountA.getBalance());
        System.out.println("Account B: " + accountB.getBalance());
        System.out.println("Account C: " + accountC.getBalance());
        System.out.println("-----------------------------------------------");

        // Start all threads
        thread1.start();
        thread2.start();
        thread3.start();

        // Wait for all threads to complete
        try {
            thread1.join(100);
            thread2.join(100);
            thread3.join(100);
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted: " + e.getMessage());
        }

        // Print final balances
        System.out.println("-----------------------------------------------");
        System.out.println("\nFinal Account Balances:");
        System.out.println("Account A: " + accountA.getBalance());
        System.out.println("Account B: " + accountB.getBalance());
        System.out.println("Account C: " + accountC.getBalance());
        System.out.println("-----------------------------------------------");


        // Print transaction history for each account
        System.out.println("\nTransaction History:");
        System.out.println("Account A transactions:");
        accountA.getTransactionsHistory().forEach(System.out::println);

        System.out.println("\nAccount B transactions:");
        accountB.getTransactionsHistory().forEach(System.out::println);

        System.out.println("\nAccount C transactions:");
        accountC.getTransactionsHistory().forEach(System.out::println);
        System.out.println("-----------------------------------------------");

    }
}