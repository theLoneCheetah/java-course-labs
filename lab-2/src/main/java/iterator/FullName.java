package iterator;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Агрегат "ФИО", хранящий части имени.
 * Имеет собственный внутренний класс-итератор
 */
public class FullName {

    private final String[] parts;

    /**
     * Инициализация с помощью ФИО в порядке обхода
     */
    public FullName(String... parts) {
        Objects.requireNonNull(parts, "parts must not be null");
        for (String part : parts) {
            Objects.requireNonNull(part, "part must not be null");
        }
        // защитная копия, чтобы внешний код не мог менять внутреннее состояние
        this.parts = parts.clone();
    }

    /**
     * Возвращает новый итератор типа MyIterator
     */
    public MyIterator<String> iterator() {
        return new FullNameIterator();
    }

    /**
     * Внутренний класс-итератор для обхода ФИО
     */
    private class FullNameIterator implements MyIterator<String> {

        private int index = 0;

        // Проверка, что ещё не дошли до конца
        @Override
        public boolean hasNext() {
            return index < parts.length;
        }

        // Получение следующего элемента и пост-инкремент
        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more parts in FullName");
            }
            return parts[index++];
        }
    }
}