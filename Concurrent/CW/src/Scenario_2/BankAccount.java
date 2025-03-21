package Scenario_2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class BankAccount {
    private final int userId;
    private BigDecimal balance;
    private List<String> transactions = new ArrayList<>();

    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);   // This is to insure the fairness
    private Lock readLock = rwLock.readLock();
    private Lock writeLock = rwLock.writeLock();

    public BankAccount(int userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public int getUserId() {
        return userId;
    }

    // current account balance
    public BigDecimal getBalance() {
        return balance;
    }

    // lock the account for exclusive writer actions
    public void lock() {
        writeLock.lock();
    }

    // unlock the account after complete
    public void unlock() {
        writeLock.unlock();
    }

    public void deposit(BigDecimal amount, int transactionId) throws InvalidAmountException {
        writeLock.lock();
        try {
            if (amount.doubleValue() > 0) {
                this.balance = this.balance.add(amount);            // Add amount to the balance
                transactions.add(Thread.currentThread().getName() + " deposit " + transactionId + " Amount " + amount);
            } else {
                throw new InvalidAmountException("Insufficient funds in the account");
            }
        } finally {
            writeLock.unlock();
        }
    }

    // Withdraw money from the account
    public void withdraw(BigDecimal amount, int transactionId) throws InsufficientBalanceException {
        writeLock.lock();
        try {
            if (amount.doubleValue() > 0 && getBalance().doubleValue() >= amount.doubleValue()) {
                this.balance = getBalance().subtract(amount);
                transactions.add(Thread.currentThread().getName() + " withdraw " + transactionId + " Amount " + amount);
            } else {
                throw new InsufficientBalanceException("Insufficient funds in the account");
            }
        } finally {
            writeLock.unlock();
        }
    }

    // Get the transaction history for the account
    public List<String> getTransactionsHistory() {
        readLock.lock();
        try {
            return this.transactions;       // list of transactions
        } finally {
            readLock.unlock();
        }
    }
}
