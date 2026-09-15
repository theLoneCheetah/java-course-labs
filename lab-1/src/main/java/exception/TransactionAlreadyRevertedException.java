package exception;

public class TransactionAlreadyRevertedException extends BankException {

    public TransactionAlreadyRevertedException(String message) {
        super(message);
    }
}