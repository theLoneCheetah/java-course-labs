package notification;

/**
 * Канал доставки push-уведомлений, сообщение выводится в консоль
 */
public class PushNotifier implements NotificationChannel {

    private final String deviceId;

    // Инициализация по идентификатору устройства
    public PushNotifier(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("Идентификатор устройства обязателен");
        }
        this.deviceId = deviceId;
    }

    @Override
    public String getType() {
        return "push";
    }

    @Override
    public String getDestination() {
        return deviceId;
    }

    @Override
    public void send(String message) {
        // Здесь в реальном сервисе должна быть своя логика
        System.out.println("[push → " + deviceId + "] " + message);
    }
}