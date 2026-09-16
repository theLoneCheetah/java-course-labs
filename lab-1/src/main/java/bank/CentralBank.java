package bank;

import account.Account;
import exception.BankException;
import time.SimulationClock;
import transaction.TransactionHistory;
import transaction.TransferTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Центральный банк: регистрирует банки, хранит их список,
 * выполняет межбанковские переводы и управляет симуляцией времени
 */
public class CentralBank {

    private final String name;
    private final SimulationClock clock;
    private final List<Bank> banks = new ArrayList<>();
    private final TransactionHistory history = new TransactionHistory();

    private LocalDate lastMonthlyProcessing;

    // Инициализация симулятором часов
    public CentralBank(String name, SimulationClock clock) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название центрального банка обязательно");
        }
        if (clock == null) {
            throw new IllegalArgumentException("SimulationClock обязателен");
        }
        this.name = name;
        this.clock = clock;
        this.lastMonthlyProcessing = clock.today();
    }

    public String getName() {
        return name;
    }

    public SimulationClock getClock() {
        return clock;
    }

    public TransactionHistory getHistory() {
        return history;
    }

    public List<Bank> getBanks() {
        return Collections.unmodifiableList(banks);
    }

    // Добавление банка
    public void registerBank(Bank bank) {
        if (bank == null) {
            throw new IllegalArgumentException("Банк обязателен");
        }
        if (banks.contains(bank)) {
            throw new IllegalArgumentException("Банк уже зарегистрирован");
        }
        banks.add(bank);
    }

    // Определить банк, в котором зарегистрирован счёт
    public Bank findBankByAccount(Account account) {
        return banks.stream()
                .filter(b -> b.ownsAccount(account))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Счёт не принадлежит ни одному зарегистрированному банку"));
    }

    // Переводы между банками

    // Выполнить перевод между банками по id счетов
    public void transferBetweenBanks(UUID sourceAccountId, UUID destinationAccountId,
                                     BigDecimal amount) throws BankException {
        Account source = findAccount(sourceAccountId);   // определить счёт источника и назначения
        Account destination = findAccount(destinationAccountId);
        Bank sourceBank = findBankByAccount(source);   // определить банк отправителя

        sourceBank.validateDoubtfulLimit(source, amount);   // проверка сомнительного клиента

        TransferTransaction tx = new TransferTransaction(clock, source, destination, amount);
        history.execute(tx);   // запуск через историю
    }

    // Отмена перевода между банками
    public void undoTransaction(UUID transactionId) throws BankException {
        history.undo(transactionId);
    }

    // Найти счёт по id
    private Account findAccount(UUID accountId) {
        for (Bank bank : banks) {
            if (bank.getAccounts().containsKey(accountId)) {
                return bank.getAccount(accountId);
            }
        }
        throw new IllegalArgumentException("Счёт не найден ни в одном банке: " + accountId);
    }

    // Симуляция времени

    /**
     * Продвинуть время на {@code days} дней.
     * Каждый день у всех банков вызывается dailyTick,
     * а при смене календарного месяца — ещё и monthlyTick
     */
    public void advanceTime(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Число дней должно быть положительным");
        }
        for (int i = 0; i < days; i++) {
            clock.advanceDays(1);
            // Ежедневные начисления
            for (Bank bank : banks) {
                bank.dailyTick();
            }
            LocalDate today = clock.today();
            // Ежемесячные начисления 1го числа
            if (isNewMonth(today)) {
                for (Bank bank : banks) {
                    bank.monthlyTick();
                }
                lastMonthlyProcessing = today;
            }
        }
    }

    // Определение нового месяца
    private boolean isNewMonth(LocalDate today) {
        return today.getYear() != lastMonthlyProcessing.getYear()
                || today.getMonth() != lastMonthlyProcessing.getMonth();
    }

    @Override
    public String toString() {
        return "CentralBank{name='" + name + "', banks=" + banks.size()
                + ", today=" + clock.today() + '}';
    }
}