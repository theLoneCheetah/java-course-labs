package notification;

/**
 * Канал доставки по SMS, сообщение выводится в консоль
 */
public class SmsNotifier implements NotificationChannel {

    private final String phoneNumber;

    // Инициализация по номеру телефона
    public SmsNotifier(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Номер телефона обязателен");
        }
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getType() {
        return "sms";
    }

    @Override
    public String getDestination() {
        return phoneNumber;
    }

    @Override
    public void send(String message) {
        // Здесь в реальном сервисе должна быть своя логика
        System.out.println("[sms → " + phoneNumber + "] " + message);
    }
}