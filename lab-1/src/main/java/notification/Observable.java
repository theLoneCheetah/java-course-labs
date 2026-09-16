package notification;

/**
 * Интерфейс-источник уведомлений. Хранит подписчиков и рассылает им сообщения
 */
public interface Observable {

    void subscribe(Observer observer);

    void unsubscribe(Observer observer);

    void notifyObservers(String message);
}