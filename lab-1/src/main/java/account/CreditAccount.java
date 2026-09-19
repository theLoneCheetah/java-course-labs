package account;

import client.Client;
import exception.BankException;
import exception.InsufficientFundsException;
import time.Clock;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Кредитный счёт: можно уходить в минус в пределах кредитного лимита, процент на остаток не начисляется,
 * при отрицательном балансе списывается фиксированная дневная комиссия
 */
public class CreditAccount extends Account {

    private final BigDecimal creditLimit;
    private final BigDecimal dailyCommission;

    // Инициализация с кредитным лимитом и комиссией
    public CreditAccount(Client owner, BigDecimal initialBalance, Clock clock,
                         BigDecimal creditLimit, BigDecimal dailyCommission) {
        super(owner, AccountType.CREDIT, initialBalance, clock);
        if (creditLimit == null || creditLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Кредитный лимит не может быть отрицательным");
        }
        if (dailyCommission == null || dailyCommission.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Комиссия не может быть отрицательной");
        }
        this.creditLimit = creditLimit;
        this.dailyCommission = dailyCommission;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public BigDecimal getDailyCommission() {
        return dailyCommission;
    }

    // Снятие
    @Override
    public void withdraw(BigDecimal amount) throws BankException {
        validateAmount(amount);
        BigDecimal newBalance = balance.subtract(amount);
        if (newBalance.compareTo(creditLimit.negate()) < 0) {   // ограничение максимального долга
            throw new InsufficientFundsException("Превышен кредитный лимит");
        }
        balance = newBalance.setScale(BALANCE_SCALE, RoundingMode.HALF_UP);
    }

    // Ежедневная комиссия
    @Override
    public void accrueDaily() {
        // Комиссия списывается только при отрицательном балансе.
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            pendingCommission = pendingCommission.add(dailyCommission);
        }
    }
}