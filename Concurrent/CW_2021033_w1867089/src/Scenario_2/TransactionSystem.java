package Scenario_2;

import java.math.BigDecimal;
import java.util.Map;

public class TransactionSystem {
    private final Map<Integer, BankAccount> accounts;

    public TransactionSystem(Map<Integer, BankAccount> accounts) {
        this.accounts = accounts;
    }

    // Placeholder method for an overloaded transfer function
    public boolean transfer(BankAccount toAccount, BigDecimal amount, int transactionId)
            throws InsufficientBalanceException, InvalidAmountException {
        BankAccount firstLock;
        BankAccount secondLock;
        return false;
    }


    public boolean transfer(BankAccount toAccount, BankAccount fromAccount, BigDecimal amount, int transactionId)
            throws InsufficientBalanceException, InvalidAmountException {
        BankAccount firstLock, secondLock;
        boolean success = false;            // Indicates if the transaction was successful

        // Store the balances before the transaction
        BigDecimal beforeAmountFromAccount = fromAccount.getBalance();
        BigDecimal beforeAmountToAccount = toAccount.getBalance();
        BigDecimal afterAmountFromAccount = null;
        BigDecimal afterAmountToAccount = null;

        // Check for the largest accountNo to avoid the deadlock
        if (fromAccount.getUserId() > toAccount.getUserId()) {
            firstLock = fromAccount;
            secondLock = toAccount;
        } else {
            firstLock = toAccount;
            secondLock = fromAccount;
        }

        acquireLocksInOrder(firstLock, secondLock);         // Acquire locks in order
        firstLock.lock();
        secondLock.lock();
        try {
            // Attempt to withdraw from the source account and deposit into the target account
            fromAccount.withdraw(amount, transactionId);
            toAccount.deposit(amount, transactionId);
            success = true;                             // if no error transaction is success
        } catch (Exception e) {
            success = false;                            // if any error occur
        } finally {
            // Get the balance after a transaction
            afterAmountFromAccount = fromAccount.getBalance();
            afterAmountToAccount = toAccount.getBalance();

            firstLock.unlock();
            secondLock.unlock();
        }

        // Handle rollback in case of transaction failure
        if (!success) {
            firstLock.lock();
            secondLock.lock();
            try {
                // Reversing if any error occur
                if (beforeAmountFromAccount.doubleValue() != afterAmountFromAccount.doubleValue() + amount.doubleValue()) {
                    fromAccount.deposit(amount, transactionId);
                }
                if (beforeAmountToAccount.doubleValue() != afterAmountToAccount.doubleValue() - amount.doubleValue()) {
                    fromAccount.withdraw(amount, transactionId);
                }
            } finally {
                firstLock.unlock();
                secondLock.unlock();
            }
        }
        return success;
    }

    // Acquires locks for two accounts in a consistent order to avoid deadlocks.
    private void acquireLocksInOrder(BankAccount first, BankAccount second) {
        if (first.getUserId() < second.getUserId()) {
            first.lock();
            second.lock();
        } else {
            second.lock();
            first.lock();
        }
    }

    //Releases locks for two accounts in the correct order.
    private void releaseLocksInOrder(BankAccount first, BankAccount second) {
        if (first.getUserId() < second.getUserId()) {
            second.unlock();
            first.unlock();
        } else {
            first.unlock();
            second.unlock();
        }
    }

    // Reverses a transaction between two accounts in case of failure
    public void reverseTransaction(int fromAccountId, int toAccountId, BigDecimal amount) throws InvalidAmountException, InsufficientBalanceException {
        BankAccount fromAccount = accounts.get(fromAccountId);
        BankAccount toAccount = accounts.get(toAccountId);

        // Check if both accounts exist
        if (fromAccount == null || toAccount == null) {
            return;
        }

        acquireLocksInOrder(fromAccount, toAccount);

        try {
            // Deposit back to the source account, withdraw from the target account
            fromAccount.deposit(amount, 1);
            toAccount.withdraw(amount, 1);
        } finally {
            releaseLocksInOrder(fromAccount, toAccount);
        }
    }

    // Print account balance
    public void printAccountBalance(int accountId) {
        BankAccount account = accounts.get(accountId);
        if (account != null) {
            System.out.println("Account ID: " + accountId + " Balance: " + account.getBalance());
        } else {
            System.out.println("Account with ID " + accountId + " not found.");
        }
    }

}
