package queue;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CustomQueueTest {

    // Сохранение порядка элементов очереди
    @Test
    void fifoOrder() {
        CustomQueue<String> queue = new CustomQueue<>();
        queue.enqueue("a");
        queue.enqueue("b");
        queue.enqueue("c");

        assertEquals(3, queue.size());
        assertEquals("a", queue.dequeue());
        assertEquals("b", queue.dequeue());
        assertEquals("c", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    // Расширяемость
    @Test
    void growsBeyondDefaultCapacity() {
        CustomQueue<Integer> queue = new CustomQueue<>();
        for (int i = 0; i < 100; i++) {
            queue.enqueue(i);
        }
        assertEquals(100, queue.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(i, queue.dequeue());
        }
        assertTrue(queue.isEmpty());
    }

    // Сохранение структуры кольца при расширении
    @Test
    void circularWrapKeepsOrder() {
        CustomQueue<Integer> queue = new CustomQueue<>();
        // заполняем до предела
        for (int i = 0; i < 10; i++) {
            queue.enqueue(i);
        }
        // освобождаем часть головы — head уезжает вперёд
        assertEquals(0, queue.dequeue());
        assertEquals(1, queue.dequeue());
        // снова заполняем: tail должен завернуться через границу массива
        queue.enqueue(100);
        queue.enqueue(101);

        assertEquals(10, queue.size());
        assertEquals(2, queue.dequeue());
        assertEquals(3, queue.dequeue());
        assertEquals(4, queue.dequeue());
        // в хвосте — добавленные после разворота
        for (int i = 5; i < 10; i++) {
            assertEquals(i, queue.dequeue());
        }
        assertEquals(100, queue.dequeue());
        assertEquals(101, queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    // Очистка стека и исключения при работе с пустым стеком
    @Test
    void clearResetsAndQueueIsReusable() {
        CustomQueue<String> queue = new CustomQueue<>();
        queue.enqueue("a");
        queue.enqueue("b");

        queue.clear();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertThrows(NoSuchElementException.class, queue::dequeue);
        assertThrows(NoSuchElementException.class, queue::peek);

        queue.enqueue("c");
        assertEquals("c", queue.dequeue());
    }

    // Допустимость null в стеке
    @Test
    void nullIsAllowed() {
        CustomQueue<String> queue = new CustomQueue<>();
        queue.enqueue(null);
        queue.enqueue("a");

        assertNull(queue.dequeue());
        assertEquals("a", queue.dequeue());
        assertTrue(queue.isEmpty());
    }
}