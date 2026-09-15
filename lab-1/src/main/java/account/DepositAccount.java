package account;

import client.Client;
import exception.BankException;
import exception.DepositNotMatureException;
import exception.InsufficientFundsException;
import time.Clock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Депозит: снимать нельзя до конца срока, пополнять можно, на остаток начисляется процент.
 * Ставку определяет банк (в зависимости от начальной суммы) и передаёт сюда готовой
 */
public class DepositAccount extends Account {

    private static final BigDecimal DAYS_IN_YEAR = BigDecimal.valueOf(365);

    private final BigDecimal initialAmount;
    private final LocalDate maturityDate;   // срок погашения
    private final BigDecimal annualRate;

    // Инициализация со сроком депозита
    public DepositAccount(Client owner, BigDecimal initialAmount, Clock clock,
                          int termDays, BigDecimal annualRate) {
        super(owner, AccountType.DEPOSIT, initialAmount, clock);
        if (termDays <= 0) {
            throw new IllegalArgumentException("Срок депозита должен быть положительным");
        }
        if (annualRate == null || annualRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Годовая ставка не может быть отрицательной");
        }
        this.initialAmount = initialAmount;
        this.maturityDate = clock.today().plusDays(termDays);
        this.annualRate = annualRate;
    }

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public BigDecimal getAnnualRate() {
        return annualRate;
    }

    // Снятие
    @Override
    public void withdraw(BigDecimal amount) throws BankException {
        validateAmount(amount);
        if (getClock().today().isBefore(maturityDate)) {   // защита от преждевременного снятия
            throw new DepositNotMatureException(
                    "Снятие с депозита запрещено до " + maturityDate);
        }
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Недостаточно средств на депозите");
        }
        balance = balance.subtract(amount).setScale(BALANCE_SCALE, RoundingMode.HALF_UP);
    }

    // Ежедневное начисление
    @Override
    public void accrueDaily() {
        if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal daily = balance.multiply(annualRate)
                .divide(DAYS_IN_YEAR, ACCRUAL_SCALE, RoundingMode.HALF_UP);
        pendingInterest = pendingInterest.add(daily);
    }
}