package transaction;

import account.Account;
import exception.BankException;
import time.Clock;

import java.math.BigDecimal;

/**
 * Пополнение счёта
 */
public class DepositTransaction extends Transaction {

    private final Account account;
    private final BigDecimal amount;

    // Инициализация суммой
    public DepositTransaction(Clock clock, Account account, BigDecimal amount) {
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

    // Перевести, т.е. пополнить счёт
    @Override
    protected void doExecute() throws BankException {
        account.deposit(amount);
    }

    // Вернуть, т.е. добавить ту же сумму как отрицательную
    @Override
    protected void doUndo() {
        account.applyRawDelta(amount.negate());
    }

    @Override
    public String describe() {
        return "Пополнение " + amount + " на счёт " + account.getId();
    }
}