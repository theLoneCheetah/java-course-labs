package notification;

/**
 * Интерфейс-подписчик на уведомления
 */
public interface Observer {

    /** Получить уведомление */
    void update(String message);
}