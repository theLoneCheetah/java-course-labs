package account;

import client.Client;
import time.Clock;

import java.math.BigDecimal;

/**
 * Фабрика счетов, инкапсулирует создание конкретных типов,
 * чтобы вызывающий код работал с абстрактным {@link Account}
 */
public final class AccountFactory {

    private AccountFactory() {
    }

    // Распределение фабричных методов: дебетовая, кредитная карта, депозит

    public static DebitAccount createDebit(Client owner, BigDecimal initialBalance,
                                           Clock clock, BigDecimal annualRate) {
        return new DebitAccount(owner, initialBalance, clock, annualRate);
    }

    public static DepositAccount createDeposit(Client owner, BigDecimal initialAmount,
                                               Clock clock, int termDays, BigDecimal annualRate) {
        return new DepositAccount(owner, initialAmount, clock, termDays, annualRate);
    }

    public static CreditAccount createCredit(Client owner, BigDecimal initialBalance,
                                             Clock clock, BigDecimal creditLimit,
                                             BigDecimal dailyCommission) {
        return new CreditAccount(owner, initialBalance, clock, creditLimit, dailyCommission);
    }
}