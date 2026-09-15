package transaction;

import exception.BankException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Журнал транзакций банка.
 * Хранит все операции, позволяет выполнить операцию «с записью» и отменить её по id
 */
public class TransactionHistory {

    private final List<Transaction> transactions = new ArrayList<>();

    /** Выполнить транзакцию и сразу записать её в журнал */
    public void execute(Transaction transaction) throws BankException {
        if (transaction == null) {
            throw new IllegalArgumentException("Транзакция обязательна");
        }
        transaction.execute();
        transactions.add(transaction);
    }

    /**
     * Отменить транзакцию по id.
     * Повторная отмена обрабатывается в Transaction
     */
    public void undo(UUID transactionId) throws BankException {
        Transaction transaction = findById(transactionId)
                .orElseThrow(() -> new BankException(
                        "Транзакция не найдена: " + transactionId));
        transaction.undo();
    }

    // Найти объект транзакции по id, может ничего не вернуть
    public Optional<Transaction> findById(UUID transactionId) {
        return transactions.stream()
                .filter(t -> t.getId().equals(transactionId))
                .findFirst();
    }

    public List<Transaction> getAll() {
        return Collections.unmodifiableList(transactions);
    }
}