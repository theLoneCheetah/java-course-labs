package transaction;

import exception.BankException;
import exception.TransactionAlreadyRevertedException;
import time.Clock;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Базовая транзакция (паттерн Command).
 * Выполнение и откат — шаблонный метод: наследники реализуют только doExecute() и doUndo(),
 * а флаги и защита от повторной отмены живут здесь.
 */
public abstract class Transaction {

    private final UUID id;
    private final Clock clock;
    private LocalDate executedAt;

    private boolean executed;   // перевод выполнен
    private boolean reverted;   // выполнен возврат

    // Нужна симуляция времени
    protected Transaction(Clock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("Clock обязателен");
        }
        this.id = UUID.randomUUID();
        this.clock = clock;
    }

    // Выполнить перевод
    public final void execute() throws BankException {
        if (executed) {   // запрет на повтор
            throw new BankException("Транзакция уже выполнена");
        }
        doExecute();
        executed = true;
        executedAt = clock.today();
    }

    // Выполнить возврат
    public final void undo() throws BankException {
        if (!executed) {   // защита от несуществующего возврата
            throw new BankException("Нельзя отменить невыполненную транзакцию");
        }
        if (reverted) {   // защита от повтора
            throw new TransactionAlreadyRevertedException(
                    "Транзакция " + id + " уже отменена");
        }
        doUndo();
        reverted = true;
    }

    protected abstract void doExecute() throws BankException;

    protected abstract void doUndo() throws BankException;

    public UUID getId() {
        return id;
    }

    public LocalDate getExecutedAt() {
        return executedAt;
    }

    public boolean isExecuted() {
        return executed;
    }

    public boolean isReverted() {
        return reverted;
    }

    /** Короткое описание для вывода в консоль / журнал */
    public abstract String describe();

    @Override
    public String toString() {
        return describe() + " [id=" + id
                + ", executed=" + executed
                + ", reverted=" + reverted + ']';
    }
}