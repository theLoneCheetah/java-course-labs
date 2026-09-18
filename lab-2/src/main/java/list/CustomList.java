package list;

import java.util.Arrays;

/**
 * Динамический список на массиве (аналог ArrayList)
 */
public class CustomList<T> {

    private static final int DEFAULT_CAPACITY = 10;   // вместимость по умолчанию

    private Object[] elements;   // просто объекты
    private int size;

    // Инициализация пустым с вместимостью по умолчанию
    public CustomList() {
        this.elements = new Object[DEFAULT_CAPACITY];
    }

    /** Добавляет элемент в конец */
    public void add(T value) {
        ensureCapacity();
        elements[size++] = value;
    }

    /** Вставляет элемент по индексу со сдвигом вправо */
    public void add(int index, T value) {
        checkIndexForAdd(index);
        ensureCapacity();
        // Копирование всех элементов, начиная с index, со сдвигом вправо
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = value;   // добавление
        size++;
    }

    /** Возвращает элемент по индексу */
    @SuppressWarnings("unchecked")   // отключить предупреждение о непроверенном приведении типов
    public T get(int index) {
        checkIndex(index);
        return (T) elements[index];   // чтобы не возвращалось как Object
    }

    /** Заменяет элемент по индексу и возвращает старое значение */
    @SuppressWarnings("unchecked")
    public T set(int index, T value) {
        checkIndex(index);
        T old = (T) elements[index];
        elements[index] = value;
        return old;
    }

    /** Удаляет элемент по индексу и возвращает его */
    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index);
        T old = (T) elements[index];
        int moved = size - index - 1;   // количество смещаемых элементов
        if (moved > 0) {
            // Копирование со сдвигом влево
            System.arraycopy(elements, index + 1, elements, index, moved);
        }
        elements[--size] = null;
        return old;
    }

    /** Текущее количество элементов */
    public int size() {
        return size;
    }

    /** Пуст ли список */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Очищает список */
    public void clear() {
        Arrays.fill(elements, 0, size, null);
        size = 0;
    }

    // Увеличение размера при исчерпании лимита
    private void ensureCapacity() {
        if (size == elements.length) {
            int newCapacity = elements.length + (elements.length >> 1) + 1;   // примерно в 1.5 раза + 1
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }

    // Проверка индекса для чтения, удаления, изменения
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", size: " + size);
        }
    }

    // Проверка индекса для вставки элемента (допускается в конец)
    private void checkIndexForAdd(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", size: " + size);
        }
    }
}