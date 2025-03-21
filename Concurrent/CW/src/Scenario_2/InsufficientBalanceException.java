package Scenario_2;

// Custom exception to indicate insufficient balance in a bank account.
public class InsufficientBalanceException extends Throwable{

    public InsufficientBalanceException(String s) {
        super(s);
    }
}
