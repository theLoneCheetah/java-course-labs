package account;

import client.Client;
import exception.BankException;
import time.Clock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Базовый банковский счёт
 * Хранит владельца, баланс и накопленные (ещё не выплаченные) проценты и комиссии
 * Правила снятия и ежедневных начислений задаются конкретным наследником
 */
public abstract class Account {

    /** Точность хранения денег на балансе */
    protected static final int BALANCE_SCALE = 2;

    /** Точность накопления процентов/комиссий до месячной выплаты */
    protected static final int ACCRUAL_SCALE = 6;

    private final UUID id;
    private final Client owner;
    private final AccountType type;
    private final LocalDate openedAt;   // дата открытия счёта
    private final Clock clock;

    // BigDecimal позволяет избежать ошибок округления
    protected BigDecimal balance;
    protected BigDecimal pendingInterest;
    protected BigDecimal pendingCommission;

    protected Account(Client owner, AccountType type, BigDecimal initialBalance, Clock clock) {
        if (owner == null) {
            throw new IllegalArgumentException("Владелец счёта обязателен");
        }
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Начальный баланс не может быть отрицательным");
        }
        if (clock == null) {   // обязательна ссылка на объект симулятора времени
            throw new IllegalArgumentException("Clock обязателен");
        }
        this.id = UUID.randomUUID();
        this.owner = owner;
        this.type = type;
        this.openedAt = clock.today();   // открыт сегодня
        this.clock = clock;
        this.balance = initialBalance.setScale(BALANCE_SCALE, RoundingMode.HALF_UP);
        this.pendingInterest = BigDecimal.ZERO;   // проценты
        this.pendingCommission = BigDecimal.ZERO;   // комиссии
    }

    public UUID getId() {
        return id;
    }

    public Client getOwner() {
        return owner;
    }

    public AccountType getType() {
        return type;
    }

    public LocalDate getOpenedAt() {
        return openedAt;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getPendingInterest() {
        return pendingInterest;
    }

    public BigDecimal getPendingCommission() {
        return pendingCommission;
    }

    protected Clock getClock() {
        return clock;
    }

    /**
     * Пополнение доступно всем типам счетов и работает одинаково
     */
    public void deposit(BigDecimal amount) throws BankException {
        validateAmount(amount);
        balance = balance.add(amount).setScale(BALANCE_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Снятие — правила определяет конкретный тип счёта (овердрафт, срок депозита и т. п.)
     */
    public abstract void withdraw(BigDecimal amount) throws BankException;

    /**
     * Ежедневное начисление процентов и комиссий, вызывается банком при симуляции времени
     */
    public abstract void accrueDaily();

    /**
     * Применение накопленных процентов и комиссий к балансу, вызывается раз в месяц.
     */
    public void applyMonthlyAccrual() {
        balance = balance.add(pendingInterest)
                .subtract(pendingCommission)
                .setScale(BALANCE_SCALE, RoundingMode.HALF_UP);
        pendingInterest = BigDecimal.ZERO;
        pendingCommission = BigDecimal.ZERO;
    }

    // Внутренняя проверка суммы операции с выбросом исключения
    protected void validateAmount(BigDecimal amount) throws BankException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankException("Сумма операции должна быть положительной");
        }
    }

    @Override
    public String toString() {
        return type + "Account{id=" + id
                + ", owner=" + owner.getFirstName() + " " + owner.getLastName()
                + ", balance=" + balance + '}';
    }
}