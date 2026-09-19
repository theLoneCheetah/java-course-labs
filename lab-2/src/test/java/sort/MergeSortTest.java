package sort;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MergeSortTest {

    // Успешная сортировка
    @Test
    void sortsRandomArrayWithDuplicates() {
        int[] a = {5, 3, 8, 3, 1, 9, 5, 2};
        assertArrayEquals(new int[]{1, 2, 3, 3, 5, 5, 8, 9}, MergeSort.sort(a));
    }

    // Уже отсортированный массив
    @Test
    void alreadySortedArrayStaysSorted() {
        int[] a = {1, 2, 3, 4, 5};
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, MergeSort.sort(a));
    }

    // Один элемент
    @Test
    void singleElement() {
        assertArrayEquals(new int[]{42}, MergeSort.sort(new int[]{42}));
    }

    // Пустой массив - не падает
    @Test
    void emptyArray() {
        assertArrayEquals(new int[0], MergeSort.sort(new int[0]));
    }

    // null
    @Test
    void nullThrows() {
        assertThrows(NullPointerException.class, () -> MergeSort.sort(null));
    }

    // Массив одинаковых элементов
    @Test
    void allEqualElements() {
        int[] a = {7, 7, 7, 7, 7};
        assertArrayEquals(new int[]{7, 7, 7, 7, 7}, MergeSort.sort(a));
    }

    // При сортировке слиянием исходный массив не изменяется
    @Test
    void doesNotMutateSource() {
        int[] a = {3, 1, 2};
        int[] sorted = MergeSort.sort(a);
        assertArrayEquals(new int[]{3, 1, 2}, a);
        assertArrayEquals(new int[]{1, 2, 3}, sorted);
    }

    // Проверка на большом массиве
    @Test
    void matchesArraysSortOnRandomBigArray() {
        Random rnd = new Random(2);
        int[] a = new int[1000];
        for (int i = 0; i < a.length; i++) {
            a[i] = rnd.nextInt(2001) - 1000;
        }
        int[] expected = a.clone();
        Arrays.sort(expected);
        assertArrayEquals(expected, MergeSort.sort(a));
    }
}