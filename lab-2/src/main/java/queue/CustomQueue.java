package queue;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Очередь (FIFO) на кольцевом массиве
 */
public class CustomQueue<T> {

    private static final int DEFAULT_CAPACITY = 10;   // вместимость по умолчанию

    private Object[] elements;   // просто объекты
    private int head;   // голова - откуда забираем
    private int tail;   // хвост - куда кладём
    private int size;

    // Инициализация пустым с вместимостью по умолчанию
    public CustomQueue() {
        this.elements = new Object[DEFAULT_CAPACITY];
    }

    /** Добавляет элемент в хвост */
    public void enqueue(T value) {
        ensureCapacity();
        elements[tail] = value;
        tail = (tail + 1) % elements.length;   // с учётом кольца
        size++;
    }

    /** Извлекает элемент из головы */
    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        T value = (T) elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;   // с учётом кольца
        size--;
        return value;
    }

    /** Возвращает головной элемент без извлечения */
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return (T) elements[head];
    }

    /** Текущее количество элементов */
    public int size() {
        return size;
    }

    /** Пуста ли очередь */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Очищает очередь */
    public void clear() {
        Arrays.fill(elements, null);
        head = 0;
        tail = 0;
        size = 0;
    }

    // Увеличение размера при исчерпании лимита + "выпрямление" очереди
    private void ensureCapacity() {
        if (size == elements.length) {
            int newCapacity = elements.length + (elements.length >> 1) + 1;   // примерно в 1.5 раза + 1
            Object[] grown = new Object[newCapacity];
            // Голова помещается в начало массива, очередь распрямляется
            for (int i = 0; i < size; i++) {
                grown[i] = elements[(head + i) % elements.length];
            }
            elements = grown;
            head = 0;
            tail = size;
        }
    }
}