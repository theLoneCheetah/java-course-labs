package stack;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Стек (LIFO) на массиве
 */
public class CustomStack<T> {

    private static final int DEFAULT_CAPACITY = 10;   // вместимость по умолчанию

    private Object[] elements;   // просто объекты
    private int size;

    // Инициализация пустым с вместимостью по умолчанию
    public CustomStack() {
        this.elements = new Object[DEFAULT_CAPACITY];
    }

    /** Кладёт элемент на вершину */
    public void push(T value) {
        ensureCapacity();
        elements[size++] = value;
    }

    /** Снимает и возвращает верхний элемент */
    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        T value = (T) elements[--size];
        elements[size] = null;   // пустая ячейка становится null
        return value;
    }

    /** Возвращает верхний элемент без снятия */
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return (T) elements[size - 1];
    }

    /** Текущее количество элементов */
    public int size() {
        return size;
    }

    /** Пуст ли стек */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Очищает стек */
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
}