package notification;

/**
 * Канал доставки по электронной почте, сообщение выводится в консоль
 */
public class EmailNotifier implements NotificationChannel {

    private final String email;

    // Инициализация по email
    public EmailNotifier(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email обязателен");
        }
        this.email = email;
    }

    @Override
    public String getType() {
        return "email";
    }

    @Override
    public String getDestination() {
        return email;
    }

    @Override
    public void send(String message) {
        // Здесь в реальном сервисе должна быть своя логика
        System.out.println("[email → " + email + "] " + message);
    }
}