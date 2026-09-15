package exception;

public class DoubtfulClientLimitException extends BankException {

    public DoubtfulClientLimitException(String message) {
        super(message);
    }
}