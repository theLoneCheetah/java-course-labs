package client;

import java.util.UUID;

/**
 * Банковский клиент, id, имя и фамилия неизменяемые
 * Адрес, номер телефона и паспорт могут быть внесены позже
 * Без адреса и паспорта клиент считается "сомнительным"
 */
public class Client {

    private final UUID id;   // UUID обеспечивает неизменяемость
    private final String firstName;
    private final String lastName;

    private String address;
    private String passportNumber;
    private String phoneNumber;

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
                + '}';
    }

    // Проверка пустого значения (для строковых адреса/паспорта)
    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}