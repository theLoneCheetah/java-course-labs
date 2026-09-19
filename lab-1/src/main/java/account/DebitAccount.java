package account;

import client.Client;
import exception.BankException;
import exception.InsufficientFundsException;
import time.Clock;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Дебетовый счёт: процент на остаток, без овердрафта, без комиссий
 */
public class DebitAccount extends Account {

    private static final BigDecimal DAYS_IN_YEAR = BigDecimal.valueOf(365);

    /** Годовая ставка, например 0.0365 для 3.65% */
    private final BigDecimal annualRate;

    // Инициализация с учётом стартового баланса и годовой ставки
    public DebitAccount(Client owner, BigDecimal initialBalance, Clock clock, BigDecimal annualRate) {
        super(owner, AccountType.DEBIT, initialBalance, clock);
        if (annualRate == null || annualRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Годовая ставка не может быть отрицательной");
        }
        this.annualRate = annualRate;
    }

    public BigDecimal getAnnualRate() {
        return annualRate;
    }

    // Снятие
    @Override
    public void withdraw(BigDecimal amount) throws BankException {
        validateAmount(amount);
        if (balance.compareTo(amount) < 0) {   // нельзя снять больше текущего баланса
            throw new InsufficientFundsException("Недостаточно средств на дебетовом счёте");
        }
        balance = balance.subtract(amount).setScale(BALANCE_SCALE, RoundingMode.HALF_UP);
    }

    // Ежедневное начисление
    @Override
    public void accrueDaily() {
        // Дневной процент = баланс * годовая ставка / 365
        // Начисляем только если баланс положительный
        if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal daily = balance.multiply(annualRate)
                .divide(DAYS_IN_YEAR, ACCRUAL_SCALE, RoundingMode.HALF_UP);
        pendingInterest = pendingInterest.add(daily);
    }
}