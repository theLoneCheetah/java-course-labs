package exception;

public class DepositNotMatureException extends BankException {

    public DepositNotMatureException(String message) {
        super(message);
    }
}