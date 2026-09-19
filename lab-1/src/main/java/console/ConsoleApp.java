package console;

import account.Account;
import bank.Bank;
import bank.CentralBank;
import client.Client;
import client.ClientBuilder;
import exception.BankException;
import notification.EmailNotifier;
import notification.NotificationChannel;
import notification.PushNotifier;
import notification.SmsNotifier;
import time.SimulationClock;
import transaction.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Консольное приложение банковской системы.
 * Создаёт центральный банк с симулируемыми часами и предоставляет меню
 * для работы с банками, клиентами, счетами, транзакциями и перемоткой времени
 */
public class ConsoleApp {

    private final Scanner scanner = new Scanner(System.in);   // сканер консоли
    private final SimulationClock clock = new SimulationClock(LocalDate.of(2024, 1, 1));   // симуляция часов от 01.01.2024
    private final CentralBank centralBank = new CentralBank("Central Bank", clock);

    // Стартовый метод запуска
    public void run() {
        System.out.println("Добро пожаловать в банковскую систему!");
        System.out.println("Стартовая дата симуляции: " + clock.today());
        boolean running = true;

        // Работа в основном меню в цикле
        while (running) {
            printMenu();
            int choice = readInt("Ваш выбор: ");
            try {
                running = handle(choice);   // Обработка выбора
            } catch (BankException e) {
                System.out.println("Ошибка операции: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Некорректный ввод: " + e.getMessage());
            }
        }
        System.out.println("Выход.");
    }

    // Вывод основного меню
    private void printMenu() {
        System.out.println();
        System.out.println("=== Меню (" + clock.today() + ") ===");
        System.out.println("1. Создать банк");
        System.out.println("2. Создать клиента");
        System.out.println("3. Дополнить данные клиента (адрес/паспорт/телефон)");
        System.out.println("4. Открыть счёт");
        System.out.println("5. Пополнить счёт");
        System.out.println("6. Снять со счёта");
        System.out.println("7. Перевод в одном банке");
        System.out.println("8. Межбанковский перевод");
        System.out.println("9. Отменить транзакцию");
        System.out.println("10. Подписать клиента на уведомления");
        System.out.println("11. Изменить условия банка");
        System.out.println("12. Перемотать время");
        System.out.println("13. Показать состояние системы");
        System.out.println("0. Выход");
    }

    // Обработчик ввода для основного меню
    private boolean handle(int choice) throws BankException {
        switch (choice) {
            case 1 -> createBank();
            case 2 -> createClient();
            case 3 -> updateClient();
            case 4 -> openAccount();
            case 5 -> deposit();
            case 6 -> withdraw();
            case 7 -> transferSameBank();
            case 8 -> transferBetweenBanks();
            case 9 -> undoTransaction();
            case 10 -> subscribeClient();
            case 11 -> changeBankConditions();
            case 12 -> advanceTime();
            case 13 -> showState();
            case 0 -> { return false; }   // 0 означает выход из меню
            default -> System.out.println("Неизвестный пункт меню.");
        }
        return true;
    }

    // Обработчики

    // Создать банк: нужно имя
    private void createBank() {
        String name = readString("Название банка: ");
        Bank bank = new Bank(name, clock);
        centralBank.registerBank(bank);
        System.out.println("Банк создан: " + bank);
    }

    // Создать клиента: заполнение информации
    private void createClient() {
        Bank bank = pickBank();   // выбор банка
        if (bank == null) return;

        String firstName = readString("Имя: ");
        String lastName = readString("Фамилия: ");

        String address = readOptionalString("Адрес");
        String passport = readOptionalString("Номер паспорта");
        String phone = readOptionalString("Телефон");

        // Сборщик клиента с паттерном Factory
        Client client = new ClientBuilder(firstName, lastName)
                .withAddress(address)
                .withPassport(passport)
                .withPhone(phone)
                .build();

        bank.registerClient(client);
        System.out.println("Клиент создан: " + client);
        if (client.isDoubtful()) {   // предупреждение по сомнительному клиенту
            System.out.println("(клиент сомнительный — лимит операций "
                    + bank.getDoubtfulOperationLimit() + ")");
        }
    }

    // Обновление данных по клиенту
    private void updateClient() {
        Bank bank = pickBank();   // выбор банка
        if (bank == null) return;
        Client client = pickClient(bank);   // выбор клиента
        if (client == null) return;

        System.out.println("Что дополнить?");
        System.out.println("  1. Адрес");
        System.out.println("  2. Паспорт");
        System.out.println("  3. Телефон");
        int choice = readInt("Ваш выбор: ");
        // Выбор дополнения информации
        switch (choice) {
            case 1 -> client.updateAddress(readString("Новый адрес: "));
            case 2 -> client.updatePassportNumber(readString("Новый паспорт: "));
            case 3 -> client.updatePhoneNumber(readString("Новый телефон: "));
            default -> System.out.println("Неизвестный пункт.");
        }
        System.out.println("Клиент обновлён: " + client);
    }

    // Открыть счёт: тип счёта
    private void openAccount() {
        Bank bank = pickBank();   // выбор банка
        if (bank == null) return;
        Client client = pickClient(bank);   // выбор клиента
        if (client == null) return;

        System.out.println("Тип счёта:");
        System.out.println("  1. Дебетовый");
        System.out.println("  2. Депозит");
        System.out.println("  3. Кредитный");
        int type = readInt("Ваш выбор: ");
        BigDecimal amount = readAmount("Начальная сумма: ");

        UUID id;
        // Выбор типа счёта и создание соответствующего
        switch (type) {
            case 1 -> id = bank.openDebitAccount(client, amount);
            case 2 -> id = bank.openDepositAccount(client, amount);
            case 3 -> id = bank.openCreditAccount(client, amount);
            default -> { System.out.println("Неизвестный тип."); return; }
        }
        System.out.println("Счёт открыт: " + id);
    }

    // Пополнить счёт
    private void deposit() throws BankException {
        Bank bank = pickBank();
        if (bank == null) return;
        Account account = pickAccount(bank);
        if (account == null) return;
        BigDecimal amount = readAmount("Сумма пополнения: ");
        bank.deposit(account.getId(), amount);
        System.out.println("Готово. Баланс: " + account.getBalance());
    }

    // снять со счёта
    private void withdraw() throws BankException {
        Bank bank = pickBank();
        if (bank == null) return;
        Account account = pickAccount(bank);
        if (account == null) return;
        BigDecimal amount = readAmount("Сумма снятия: ");
        bank.withdraw(account.getId(), amount);
        System.out.println("Готово. Баланс: " + account.getBalance());
    }

    // Перевести между счетами в банке
    private void transferSameBank() throws BankException {
        Bank bank = pickBank();
        if (bank == null) return;
        if (bank.getAccounts().size() < 2) {
            System.out.println("Нужно минимум два счёта в банке.");
            return;
        }
        System.out.println("Исходный счёт:");
        Account source = pickAccount(bank);
        if (source == null) return;
        System.out.println("Целевой счёт:");
        Account destination = pickAccount(bank);
        if (destination == null) return;
        BigDecimal amount = readAmount("Сумма перевода: ");
        bank.transfer(source.getId(), destination.getId(), amount);
        System.out.println("Перевод выполнен.");
    }

    // Перевести между счетами разных банков
    private void transferBetweenBanks() throws BankException {
        if (centralBank.getBanks().size() < 2) {
            System.out.println("Нужно минимум два банка.");
            return;
        }
        System.out.println("Исходный банк:");
        Bank sourceBank = pickBank();
        if (sourceBank == null) return;
        Account source = pickAccount(sourceBank);
        if (source == null) return;

        System.out.println("Целевой банк:");
        Bank destBank = pickBank();
        if (destBank == null) return;
        Account destination = pickAccount(destBank);
        if (destination == null) return;

        BigDecimal amount = readAmount("Сумма перевода: ");
        centralBank.transferBetweenBanks(source.getId(), destination.getId(), amount);
        System.out.println("Межбанковский перевод выполнен.");
    }

    // Отменить транзакцию
    private void undoTransaction() throws BankException {
        List<TxEntry> entries = collectTransactions();
        if (entries.isEmpty()) {
            System.out.println("Нет выполненных транзакций.");
            return;
        }
        System.out.println("Транзакции:");
        for (int i = 0; i < entries.size(); i++) {
            TxEntry entry = entries.get(i);
            String scope = entry.bank() == null ? "central" : entry.bank().getName();
            System.out.println("  " + (i + 1) + ". [" + scope + "] " + entry.transaction());
        }
        int idx = readInt("Номер транзакции для отмены (0 — отмена): ") - 1;
        if (idx < 0 || idx >= entries.size()) {
            System.out.println("Отменено пользователем.");
            return;
        }
        TxEntry selected = entries.get(idx);
        if (selected.bank() == null) {
            centralBank.undoTransaction(selected.transaction().getId());
        } else {
            selected.bank().undoTransaction(selected.transaction().getId());
        }
        System.out.println("Транзакция отменена.");
    }

    // Сделать клиента подписчиком
    private void subscribeClient() {
        Bank bank = pickBank();
        if (bank == null) return;
        Client client = pickClient(bank);
        if (client == null) return;

        System.out.println("Канал уведомлений:");
        System.out.println("  1. Email");
        System.out.println("  2. SMS");
        System.out.println("  3. Push");
        int choice = readInt("Ваш выбор: ");
        NotificationChannel channel;
        // Выбор типа подписки
        switch (choice) {
            case 1 -> channel = new EmailNotifier(readString("Email: "));
            case 2 -> channel = new SmsNotifier(readString("Номер телефона: "));
            case 3 -> channel = new PushNotifier(readString("ID устройства: "));
            default -> { System.out.println("Неизвестный канал."); return; }
        }
        client.subscribeChannel(channel);   // Добавление канала клиенту
        bank.subscribe(client);   // добавление клиента в подписчики (для банка)
        System.out.println("Клиент подписан на уведомления банка " + bank.getName());
    }

    // Изменить параметры банка
    private void changeBankConditions() {
        Bank bank = pickBank();
        if (bank == null) return;
        System.out.println("Что изменить?");
        System.out.println("  1. Годовая ставка по дебету (" + bank.getDebitAnnualRate() + ")");
        System.out.println("  2. Кредитный лимит (" + bank.getCreditLimit() + ")");
        System.out.println("  3. Лимит для сомнительных клиентов ("
                + bank.getDoubtfulOperationLimit() + ")");
        int choice = readInt("Ваш выбор: ");
        // Выбор типа изменений
        switch (choice) {
            case 1 -> bank.updateDebitAnnualRate(readAmount("Новая ставка (например, 0.04): "));
            case 2 -> bank.updateCreditLimit(readAmount("Новый лимит: "));
            case 3 -> bank.updateDoubtfulOperationLimit(readAmount("Новый лимит: "));
            default -> System.out.println("Неизвестный пункт.");
        }
    }

    // Симуляция времени: перемотка
    private void advanceTime() {
        System.out.println("Перемотка времени:");
        System.out.println("  1. 1 день");
        System.out.println("  2. 30 дней");
        System.out.println("  3. 365 дней");
        System.out.println("  4. Своё число дней");
        int choice = readInt("Ваш выбор: ");
        int days;
        // Выбор срока перемотки
        switch (choice) {
            case 1 -> days = 1;
            case 2 -> days = 30;
            case 3 -> days = 365;
            case 4 -> days = readInt("Число дней: ");
            default -> { System.out.println("Неизвестный пункт."); return; }
        }
        centralBank.advanceTime(days);
        System.out.println("Новая дата: " + clock.today());
    }

    // Вывод сводной информации
    private void showState() {
        System.out.println("Центральный банк: " + centralBank);
        if (centralBank.getBanks().isEmpty()) {
            System.out.println("Банков пока нет.");
            return;
        }
        for (Bank bank : centralBank.getBanks()) {
            System.out.println();
            System.out.println("Банк " + bank.getName()
                    + " (ставка по дебету " + bank.getDebitAnnualRate()
                    + ", кредитный лимит " + bank.getCreditLimit() + ")");
            if (bank.getClients().isEmpty()) {
                System.out.println("  Клиентов нет.");
            } else {
                System.out.println("  Клиенты:");
                for (Client client : bank.getClients().values()) {
                    System.out.println("    " + client);
                }
            }
            if (bank.getAccounts().isEmpty()) {
                System.out.println("  Счетов нет.");
            } else {
                System.out.println("  Счета:");
                for (Account account : bank.getAccounts().values()) {
                    System.out.println("    " + account);
                }
            }
        }
    }

    // Вспомогательные методы

    // Поиск банка среди доступных
    private Bank pickBank() {
        List<Bank> banks = centralBank.getBanks();
        if (banks.isEmpty()) {
            System.out.println("Нет зарегистрированных банков.");
            return null;
        }
        System.out.println("Доступные банки:");
        for (int i = 0; i < banks.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + banks.get(i).getName());
        }
        int idx = readInt("Номер банка (0 — отмена): ") - 1;
        if (idx < 0 || idx >= banks.size()) {
            return null;
        }
        return banks.get(idx);
    }

    // Поиск клиента в банке среди доступных
    private Client pickClient(Bank bank) {
        List<Client> clients = new ArrayList<>(bank.getClients().values());
        if (clients.isEmpty()) {
            System.out.println("В банке нет клиентов.");
            return null;
        }
        System.out.println("Клиенты банка " + bank.getName() + ":");
        for (int i = 0; i < clients.size(); i++) {
            Client c = clients.get(i);
            System.out.println("  " + (i + 1) + ". "
                    + c.getFirstName() + " " + c.getLastName()
                    + " (" + c.getStatus() + ")");
        }
        int idx = readInt("Номер клиента (0 — отмена): ") - 1;
        if (idx < 0 || idx >= clients.size()) {
            return null;
        }
        return clients.get(idx);
    }

    // Поиск счёта в банке среди доступных
    private Account pickAccount(Bank bank) {
        List<Account> accounts = new ArrayList<>(bank.getAccounts().values());
        if (accounts.isEmpty()) {
            System.out.println("В банке нет счетов.");
            return null;
        }
        System.out.println("Счета банка " + bank.getName() + ":");
        for (int i = 0; i < accounts.size(); i++) {
            Account a = accounts.get(i);
            String shortId = a.getId().toString().substring(0, 8);
            System.out.println("  " + (i + 1) + ". " + a.getType()
                    + " [" + shortId + "] "
                    + a.getOwner().getFirstName() + " " + a.getOwner().getLastName()
                    + " — " + a.getBalance());
        }
        int idx = readInt("Номер счёта (0 — отмена): ") - 1;
        if (idx < 0 || idx >= accounts.size()) {
            return null;
        }
        return accounts.get(idx);
    }

    // Сводный массив транзакций
    private List<TxEntry> collectTransactions() {
        List<TxEntry> entries = new ArrayList<>();
        for (Transaction tx : centralBank.getHistory().getAll()) {
            entries.add(new TxEntry(tx, null));
        }
        for (Bank bank : centralBank.getBanks()) {
            for (Transaction tx : bank.getHistory().getAll()) {
                entries.add(new TxEntry(tx, bank));
            }
        }
        return entries;
    }

    // Считать число int
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число.");
            }
        }
    }

    // Прочитать дробное число
    private BigDecimal readAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().replace(',', '.');
            try {
                return new BigDecimal(line).setScale(2, RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                System.out.println("Введите число.");
            }
        }
    }

    // Прочитать обязательную строку
    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    // Прочитать опциональную строку
    private String readOptionalString(String prompt) {
        System.out.print(prompt + " (Enter — пропустить): ");
        String value = scanner.nextLine().trim();
        return value.isEmpty() ? null : value;
    }

    /** Элемент объединённого списка транзакций: транзакция + её владелец */
    private record TxEntry(Transaction transaction, Bank bank) {
    }
}