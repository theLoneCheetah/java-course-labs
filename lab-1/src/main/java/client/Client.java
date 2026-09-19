package client;

import notification.NotificationChannel;
import notification.Observer;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Банковский клиент, id, имя и фамилия неизменяемые
 * Адрес, номер телефона и паспорт могут быть внесены позже
 * Без адреса и паспорта клиент считается "сомнительным".
 * Реализует Observer — получает уведомления от банка и рассылает их
 */
public class Client implements Observer {

    private final UUID id;   // UUID обеспечивает неизменяемость
    private final String firstName;
    private final String lastName;

    private String address;
    private String passportNumber;
    private String phoneNumber;

    private final Set<NotificationChannel> channels = new LinkedHashSet<>();   // каналы рассылки

    // Конструктор с генерацией уникального id
    Client(String firstName, String lastName) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Геттеры

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    // Сеттеры
    
    public void updateAddress(String address) {
        this.address = address;
    }

    public void updatePassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Уведомления

    // Добавить канал
    public void subscribeChannel(NotificationChannel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Канал обязателен");
        }
        channels.add(channel);
    }

    // Удалить канал
    public void unsubscribeChannel(NotificationChannel channel) {
        channels.remove(channel);
    }

    public Set<NotificationChannel> getChannels() {
        return Collections.unmodifiableSet(channels);
    }

    // Запустить обновление
    public void update(String message) {
        if (channels.isEmpty()) {
            return;
        }
        for (NotificationChannel channel : channels) {
            channel.send(message);
        }
    }

    // Клиент сомнительный, если отсутствуют адрес или паспортные данные
    public boolean isDoubtful() {
        return isBlank(address) || isBlank(passportNumber);
    }

    // Получение статуса
    public ClientStatus getStatus() {
        return isDoubtful() ? ClientStatus.DOUBTFUL : ClientStatus.REGULAR;
    }

    @Override
    public String toString() {
        return "Client{id=" + id
                + ", name='" + firstName + " " + lastName + '\''
                + ", status=" + getStatus()
                + ", channels=" + channels.size()
                + '}';
    }

    // Проверка пустого значения (для строковых адреса/паспорта)
    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}