package transaction;

import account.Account;
import exception.BankException;
import time.Clock;

import java.math.BigDecimal;

/**
 * Перевод между двумя счетами.
 * Откат — обратный перевод: с целевого счёта возвращаем на исходный
 */
public class TransferTransaction extends Transaction {

    private final Account source;
    private final Account destination;
    private final BigDecimal amount;

    // Инициализация суммой и двумя счетами
    public TransferTransaction(Clock clock, Account source, Account destination, BigDecimal amount) {
        super(clock);
        if (source == null || destination == null) {
            throw new IllegalArgumentException("Исходный и целевой счета обязательны");
        }
        if (source == destination) {
            throw new IllegalArgumentException("Нельзя перевести на тот же счёт");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }
        this.source = source;
        this.destination = destination;
        this.amount = amount;
    }

    public Account getSource() {
        return source;
    }

    public Account getDestination() {
        return destination;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    // Выполнить перевод: снять с источника и положить получателю
    @Override
    protected void doExecute() throws BankException {
        source.withdraw(amount);
        destination.deposit(amount);
    }

    // Вернуть перевод: обратный процесс
    @Override
    protected void doUndo() {
        destination.applyRawDelta(amount.negate());
        source.applyRawDelta(amount);
    }

    @Override
    public String describe() {
        return "Перевод " + amount + " со счёта " + source.getId()
                + " на счёт " + destination.getId();
    }
}