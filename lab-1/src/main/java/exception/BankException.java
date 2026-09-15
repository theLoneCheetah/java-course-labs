package exception;

/**
 * Базовое исключение для всех bank ошибок
 */
public class BankException extends Exception {

    // Обычный конструктор
    public BankException(String message) {
        super(message);
    }

    // Конструктор с сохранением первоначальной ошибки
    public BankException(String message, Throwable cause) {
        super(message, cause);
    }
}
