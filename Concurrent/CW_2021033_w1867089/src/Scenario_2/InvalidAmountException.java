package Scenario_2;

// Custom exception to indicate an invalid amount in a transaction.
public class InvalidAmountException extends Throwable {
    public InvalidAmountException(String invalidAmountException) {
        super(invalidAmountException);
    }
}

