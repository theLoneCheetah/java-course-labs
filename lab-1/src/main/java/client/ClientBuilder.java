package client;

/**
 * Класс-сборщик для {@link Client}
 * 1. Имя и фамилия (обязательн)
 * 2. Адрес (опционально)
 * 3. Паспорт (опционально)
 * Дополнительно: номер телефона (опционально)
 * Использует паттерн Builder: при вызовах возвращает указатель на объект
 */
public class ClientBuilder {

    private final String firstName;
    private final String lastName;

    private String address;
    private String passportNumber;
    private String phoneNumber;

    // Объявление сразу с именем и фамилией
    public ClientBuilder(String firstName, String lastName) {
        // Исключение при пустых данных
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Добавить адрес
    public ClientBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    // Добавить паспорт
    public ClientBuilder withPassport(String passportNumber) {
        this.passportNumber = passportNumber;
        return this;
    }

    // Добавить телефон
    public ClientBuilder withPhone(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    // Собрать клиента с полученными данными
    public Client build() {
        Client client = new Client(firstName, lastName);
        client.updateAddress(address);
        client.updatePassportNumber(passportNumber);
        client.updatePhoneNumber(phoneNumber);
        return client;   // возвращает объект клиента
    }
}