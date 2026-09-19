package transaction;

import account.Account;
import exception.BankException;
import time.Clock;

import java.math.BigDecimal;

/**
 * Снятие со счёта
 */
public class WithdrawTransaction extends Transaction {

    private final Account account;
    private final BigDecimal amount;

    // Инициализация суммой
    public WithdrawTransaction(Clock clock, Account account, BigDecimal amount) {
        super(clock);
        if (account == null) {
            throw new IllegalArgumentException("Счёт обязателен");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }
        this.account = account;
        this.amount = amount;
    }

    // Снять, т.е. снять со счёта
    @Override
    protected void doExecute() throws BankException {
        account.withdraw(amount);
    }

    // Вернуть, т.е. добавить сумму снятия
    @Override
    protected void doUndo() {
        account.applyRawDelta(amount);
    }

    @Override
    public String describe() {
        return "Снятие " + amount + " со счёта " + account.getId();
    }
}