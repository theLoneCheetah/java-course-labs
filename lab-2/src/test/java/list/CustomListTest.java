package list;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomListTest {

    // Стандартное добавление элементов и порядок
    @Test
    void addAndGetInOrder() {
        CustomList<String> list = new CustomList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        assertEquals(3, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    // Проверка расширяемости
    @Test
    void growsBeyondDefaultCapacity() {
        CustomList<Integer> list = new CustomList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        assertEquals(100, list.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(i, list.get(i));
        }
    }

    // Замена элемента
    @Test
    void setReturnsOldAndReplaces() {
        CustomList<String> list = new CustomList<>();
        list.add("a");
        list.add("b");

        assertEquals("a", list.set(0, "x"));
        assertEquals("x", list.get(0));
        assertEquals("b", list.get(1));
    }

    // Проверка выброса исключения выхода за границы
    @Test
    void indexOutOfBoundsThrows() {
        CustomList<String> list = new CustomList<>();
        list.add("a");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(1, "x"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(2, "x"));
    }

    // Исключение при работе с пустым списком
    @Test
    void emptyListBehavior() {
        CustomList<String> list = new CustomList<>();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
    }

    // Работа списка после полной очистки
    @Test
    void clearResetsAndListIsReusable() {
        CustomList<String> list = new CustomList<>();
        list.add("a");
        list.add("b");

        list.clear();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());

        list.add("c");
        assertEquals(1, list.size());
        assertEquals("c", list.get(0));
    }
}