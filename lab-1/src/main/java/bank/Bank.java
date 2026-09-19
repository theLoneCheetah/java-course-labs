package bank;

import account.Account;
import account.AccountFactory;
import account.DebitAccount;
import account.DepositAccount;
import client.Client;
import exception.BankException;
import exception.DoubtfulClientLimitException;
import time.Clock;
import transaction.DepositTransaction;
import transaction.TransferTransaction;
import transaction.Transaction;
import transaction.TransactionHistory;
import transaction.WithdrawTransaction;
import notification.Observable;
import notification.Observer;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
/**
 * Банк: хранит клиентов, счета и историю транзакций,
 * задаёт условия (ставки, комиссии, лимиты) и выполняет операции
 */
public class Bank implements Observable {

    private final UUID id;
    private final String name;
    private final Clock clock;
    private final TransactionHistory history;

    // клиенты и счета хранятся в словарях с сохранением порядка
    private final Map<UUID, Client> clients = new LinkedHashMap<>();
    private final Map<UUID, Account> accounts = new LinkedHashMap<>();

    // Условия банка

    // Проценты для дебетовых карт
    private BigDecimal debitAnnualRate = new BigDecimal("0.0365");

    /**
     * Таблица ставок депозита: ключ — верхняя граница суммы включительно, значение — годовая ставка.
     * Последняя запись должна покрывать любые большие суммы.
     */
    private final NavigableMap<BigDecimal, BigDecimal> depositRates = new TreeMap<>();   // хранит отсортированными, поиск по диапазонам

    private int depositTermDays = 365;
    private BigDecimal creditLimit = new BigDecimal("100000");
    private BigDecimal creditDailyCommission = new BigDecimal("10");
    private BigDecimal doubtfulOperationLimit = new BigDecimal("10000");

    private final Set<Observer> observers = new LinkedHashSet<>();   // подписчики на уведомления

    // Инициализация симулятором часов
    public Bank(String name, Clock clock) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название банка обязательно");
        }
        if (clock == null) {
            throw new IllegalArgumentException("Clock обязателен");
        }
        this.id = UUID.randomUUID();
        this.name = name;
        this.clock = clock;
        this.history = new TransactionHistory();

        // Ставки по умолчанию: до 50 000 → 3%, до 100 000 → 3.5%, больше → 4%.
        depositRates.put(new BigDecimal("50000"), new BigDecimal("0.03"));
        depositRates.put(new BigDecimal("100000"), new BigDecimal("0.035"));
        depositRates.put(new BigDecimal("9999999999"), new BigDecimal("0.04"));
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TransactionHistory getHistory() {
        return history;
    }

    public Map<UUID, Client> getClients() {
        return Collections.unmodifiableMap(clients);
    }

    public Map<UUID, Account> getAccounts() {
        return Collections.unmodifiableMap(accounts);
    }

    // Клиенты

    // Добавление нового клиента
    public void registerClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Клиент обязателен");
        }
        clients.put(client.getId(), client);
    }

    // Получение клиента
    public Client getClient(UUID clientId) {
        Client client = clients.get(clientId);
        if (client == null) {
            throw new IllegalArgumentException("Клиент не найден: " + clientId);
        }
        return client;
    }

    // Открытие счетов

    // Дебетовая карта
    public UUID openDebitAccount(Client client, BigDecimal initialBalance) {
        ensureClientRegistered(client);
        DebitAccount account = AccountFactory.createDebit(client, initialBalance, clock, debitAnnualRate);
        accounts.put(account.getId(), account);   // добавление счёта в банк
        return account.getId();
    }

    // Депозит
    public UUID openDepositAccount(Client client, BigDecimal initialAmount) {
        ensureClientRegistered(client);
        BigDecimal rate = resolveDepositRate(initialAmount);   // получение депозитной ставки
        DepositAccount account = AccountFactory.createDeposit(
                client, initialAmount, clock, depositTermDays, rate);
        accounts.put(account.getId(), account);   // добавление счёта в банк
        return account.getId();
    }

    // Кредитная карта
    public UUID openCreditAccount(Client client, BigDecimal initialBalance) {
        ensureClientRegistered(client);
        Account account = AccountFactory.createCredit(
                client, initialBalance, clock, creditLimit, creditDailyCommission);
        accounts.put(account.getId(), account);   // добавление счёта в банк
        return account.getId();
    }

    // Получение счёта
    public Account getAccount(UUID accountId) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Счёт не найден: " + accountId);
        }
        return account;
    }

    // Проверка наличия счёта
    public boolean ownsAccount(Account account) {
        return account != null && accounts.containsKey(account.getId());
    }

    // Расчёт депозитной ставки
    private BigDecimal resolveDepositRate(BigDecimal amount) {
        // ceilingEntry — первая ставка, чья верхняя граница ≥ суммы
        Map.Entry<BigDecimal, BigDecimal> entry = depositRates.ceilingEntry(amount);
        if (entry != null) {
            return entry.getValue();
        }
        return depositRates.lastEntry().getValue();
    }

    // Операции

    // Внести сумму на указанный счёт
    public void deposit(UUID accountId, BigDecimal amount) throws BankException {
        Account account = getAccount(accountId);
        DepositTransaction tx = new DepositTransaction(clock, account, amount);
        history.execute(tx);   // записать в историю, оттуда же запуск транзакции
    }

    // Снять со счёта
    public void withdraw(UUID accountId, BigDecimal amount) throws BankException {
        Account account = getAccount(accountId);
        validateDoubtfulLimit(account, amount);   // проверка сомнительности клиента
        WithdrawTransaction tx = new WithdrawTransaction(clock, account, amount);
        history.execute(tx);   // записать и запустить
    }

    // Перевести со счёта на счёт
    public void transfer(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount)
            throws BankException {
        Account source = getAccount(sourceAccountId);
        Account destination = getAccount(destinationAccountId);
        validateDoubtfulLimit(source, amount);   // проверка сомнительности клиента
        TransferTransaction tx = new TransferTransaction(clock, source, destination, amount);
        history.execute(tx);   // записать и запустить
    }

    // Отмена транзакции
    public void undoTransaction(UUID transactionId) throws BankException {
        history.undo(transactionId);   // отмена через историю
    }

    /** Запись уже выполненной транзакции (используется CentralBank) */
    public void recordTransaction(Transaction transaction) throws BankException {
        history.execute(transaction);
    }

    /** Проверка лимита для сомнительных клиентов */
    public void validateDoubtfulLimit(Account account, BigDecimal amount)
            throws DoubtfulClientLimitException {
        Client owner = account.getOwner();
        if (owner.isDoubtful() && amount.compareTo(doubtfulOperationLimit) > 0) {
            throw new DoubtfulClientLimitException(
                    "Сомнительный клиент не может оперировать суммой больше "
                            + doubtfulOperationLimit);
        }
    }

    // Ежедневные / ежемесячные начисления

    public void dailyTick() {
        for (Account account : accounts.values()) {
            account.accrueDaily();
        }
    }

    public void monthlyTick() {
        for (Account account : accounts.values()) {
            account.applyMonthlyAccrual();
        }
    }

    // Наблюдатели

    // Добавить подписчика
    @Override
    public void subscribe(Observer observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Наблюдатель обязателен");
        }
        observers.add(observer);
    }

    // Отписать
    @Override
    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    // Разослать сообщение подписчикам
    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update("[" + name + "] " + message);
        }
    }

    // Изменение условий

    public BigDecimal getDebitAnnualRate() {
        return debitAnnualRate;
    }

    // Обновить процентную ставку для дебетовых карт
    public void updateDebitAnnualRate(BigDecimal newRate) {
        if (newRate == null || newRate.signum() < 0) {
            throw new IllegalArgumentException("Ставка не может быть отрицательной");
        }
        this.debitAnnualRate = newRate;
        notifyObservers("Изменена ставка по дебетовым счетам: " + newRate);   // уведомление
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    // Обновить кредитный лимит
    public void updateCreditLimit(BigDecimal newLimit) {
        if (newLimit == null || newLimit.signum() < 0) {
            throw new IllegalArgumentException("Кредитный лимит не может быть отрицательным");
        }
        this.creditLimit = newLimit;
        notifyObservers("Изменён кредитный лимит: " + newLimit);   // уведомление
    }

    public BigDecimal getCreditDailyCommission() {
        return creditDailyCommission;
    }

    // Обновить ежедневную кредитную комиссию
    public void updateCreditDailyCommission(BigDecimal newCommission) {
        if (newCommission == null || newCommission.signum() < 0) {
            throw new IllegalArgumentException("Комиссия не может быть отрицательной");
        }
        this.creditDailyCommission = newCommission;
    }

    public BigDecimal getDoubtfulOperationLimit() {
        return doubtfulOperationLimit;
    }

    // Обновить лимит для сомнительных клиентов
    public void updateDoubtfulOperationLimit(BigDecimal newLimit) {
        if (newLimit == null || newLimit.signum() < 0) {
            throw new IllegalArgumentException("Лимит не может быть отрицательным");
        }
        this.doubtfulOperationLimit = newLimit;
    }

    // Проверка наличия клиента
    private void ensureClientRegistered(Client client) {
        if (client == null || !clients.containsKey(client.getId())) {
            throw new IllegalArgumentException("Клиент не зарегистрирован в банке");
        }
    }

    @Override
    public String toString() {
        return "Bank{name='" + name + "', clients=" + clients.size()
                + ", accounts=" + accounts.size() + '}';
    }
}