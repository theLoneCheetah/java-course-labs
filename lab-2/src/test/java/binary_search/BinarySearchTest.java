package binary_search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BinarySearchTest {

    private static final int[] SORTED = {1, 3, 5, 7, 9, 11, 13};

    // Поиск медианного, первого и последнего элементов

    @Test
    void findsMiddleElement() {
        assertEquals(3, BinarySearch.search(SORTED, 7));
    }

    @Test
    void findsFirstElement() {
        assertEquals(0, BinarySearch.search(SORTED, 1));
    }

    @Test
    void findsLastElement() {
        assertEquals(6, BinarySearch.search(SORTED, 13));
    }

    // Поиск несуществующего элемента
    @Test
    void missingBetweenElements() {
        assertEquals(-1, BinarySearch.search(SORTED, 6));
    }

    // Пустой массив возвращает -1
    @Test
    void emptyArray() {
        assertEquals(-1, BinarySearch.search(new int[0], 5));
    }

    // Возврат одного из подходящих индексов при дупликатах
    @Test
    void duplicatesReturnValidIndex() {
        int[] a = {1, 2, 2, 2, 2, 3};
        int idx = BinarySearch.search(a, 2);
        assertTrue(idx >= 1 && idx <= 4, "index should point to a duplicate");
        assertEquals(2, a[idx]);
    }
}