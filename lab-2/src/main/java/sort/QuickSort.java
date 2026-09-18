package sort;

/**
 * Быстрая сортировка (схема Хоара, опорный — средний элемент)
 */
public final class QuickSort {

    // Закрытый конструктор обеспечивает запрет на создание экземпляров
    private QuickSort() {
    }

    /** Сортирует массив целых чисел по возрастанию in-place и возвращает его же */
    public static int[] sort(int[] a) {
        if (a == null) {
            throw new NullPointerException("array must not be null");
        }
        // Изначально левый и правый указатели по краям массива
        quickSort(a, 0, a.length - 1);
        return a;
    }

    // Рекурсивный алгоритм
    private static void quickSort(int[] a, int left, int right) {
        // Ожидаем схождения указателей
        if (left >= right) {
            return;
        }
        int i = left;
        int j = right;
        int pivot = a[left + (right - left) / 2];   // опорный элемент

        // Для всех элементов между указателями
        while (i <= j) {
            while (a[i] < pivot) i++;
            while (a[j] > pivot) j--;
            if (i <= j) {
                // Если левый больше правого, меняем местами
                int tmp = a[i];
                a[i] = a[j];
                a[j] = tmp;
                i++;
                j--;
            }
        }

        // Рекурсия для оставшихся частей
        if (left < j) quickSort(a, left, j);
        if (i < right) quickSort(a, i, right);
    }
}