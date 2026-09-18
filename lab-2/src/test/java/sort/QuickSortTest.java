package sort;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class QuickSortTest {

    // Успешная сортировка
    @Test
    void sortsRandomArrayWithDuplicates() {
        int[] a = {5, 3, 8, 3, 1, 9, 5, 2};
        assertArrayEquals(new int[]{1, 2, 3, 3, 5, 5, 8, 9}, QuickSort.sort(a));
    }

    // Уже отсортированный массив
    @Test
    void alreadySortedArrayStaysSorted() {
        int[] a = {1, 2, 3, 4, 5};
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, QuickSort.sort(a));
    }

    // Один элемент
    @Test
    void singleElement() {
        assertArrayEquals(new int[]{42}, QuickSort.sort(new int[]{42}));
    }

    // Пустой массив - не падает
    @Test
    void emptyArray() {
        assertArrayEquals(new int[0], QuickSort.sort(new int[0]));
    }

    // null
    @Test
    void nullThrows() {
        assertThrows(NullPointerException.class, () -> QuickSort.sort(null));
    }

    // Массив одинаковых элементов
    @Test
    void allEqualElements() {
        int[] a = {7, 7, 7, 7, 7};
        assertArrayEquals(new int[]{7, 7, 7, 7, 7}, QuickSort.sort(a));
    }

    // Проверка на большом массиве
    @Test
    void matchesArraysSortOnRandomBigArray() {
        Random rnd = new Random(1);
        int[] a = new int[1000];
        for (int i = 0; i < a.length; i++) {
            a[i] = rnd.nextInt(2001) - 1000;
        }
        int[] expected = a.clone();
        Arrays.sort(expected);
        assertArrayEquals(expected, QuickSort.sort(a));
    }
}