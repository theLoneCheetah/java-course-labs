package iterator;

/**
 * Интерфейс итератора, T - тип элементов
 */
public interface MyIterator<T> {

    /**
     * Возвращает true, если есть следующий элемент
     */
    boolean hasNext();

    /**
     * Возвращает следующий элемент
     */
    T next();
}