package stack;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CustomStackTest {

    // Сохранение порядка элементов стека
    @Test
    void lifoOrder() {
        CustomStack<String> stack = new CustomStack<>();
        stack.push("a");
        stack.push("b");
        stack.push("c");

        assertEquals(3, stack.size());
        assertEquals("c", stack.pop());
        assertEquals("b", stack.pop());
        assertEquals("a", stack.pop());
        assertTrue(stack.isEmpty());
    }

    // Работа peek не изменяет стек
    @Test
    void peekDoesNotChangeSize() {
        CustomStack<Integer> stack = new CustomStack<>();
        stack.push(1);
        stack.push(2);

        assertEquals(2, stack.peek());
        assertEquals(2, stack.peek());
        assertEquals(2, stack.size());
    }

    // Расширяемость
    @Test
    void growsBeyondDefaultCapacity() {
        CustomStack<Integer> stack = new CustomStack<>();
        for (int i = 0; i < 100; i++) {
            stack.push(i);
        }
        assertEquals(100, stack.size());
        for (int i = 99; i >= 0; i--) {
            assertEquals(i, stack.pop());
        }
        assertTrue(stack.isEmpty());
    }

    // Очистка стека и исключения при работе с пустым стеком
    @Test
    void clearResetsAndStackIsReusable() {
        CustomStack<String> stack = new CustomStack<>();
        stack.push("a");
        stack.push("b");

        stack.clear();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
        assertThrows(NoSuchElementException.class, stack::pop);
        assertThrows(NoSuchElementException.class, stack::peek);

        stack.push("c");
        assertEquals("c", stack.pop());
    }

    // Допустимость null в стеке
    @Test
    void nullIsAllowed() {
        CustomStack<String> stack = new CustomStack<>();
        stack.push(null);
        stack.push("a");

        assertEquals("a", stack.pop());
        assertNull(stack.pop());
        assertTrue(stack.isEmpty());
    }
}