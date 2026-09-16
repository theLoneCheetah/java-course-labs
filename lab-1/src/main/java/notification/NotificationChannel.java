package notification;

/**
 * Интерфейс-канал доставки уведомлений (email, sms, push и т. п.).
 * Хранит адрес доставки и умеет отправить по нему сообщение
 */
public interface NotificationChannel {

    /** Короткое имя канала для вывода (например, "email") */
    String getType();

    /** Адрес доставки (email, телефон, device id и т. п.) */
    String getDestination();

    /** Отправить сообщение */
    void send(String message);
}