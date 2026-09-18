package sort;

/**
 * Сортировка слиянием. Возвращает новый отсортированный массив исходный не изменяется
 */
public final class MergeSort {

    // Закрытый конструктор обеспечивает запрет на создание экземпляров
    private MergeSort() {
    }

    /** Возвращает новый отсортированный по возрастанию массив */
    public static int[] sort(int[] a) {
        if (a == null) {
            throw new NullPointerException("array must not be null");
        }
        int[] result = a.clone();
        if (result.length < 2) {
            return result;
        }
        mergeSort(result, 0, result.length - 1);
        return result;
    }

    // Рекурсивный алгоритм
    private static void mergeSort(int[] a, int left, int right) {
        // Ожидаем схождения указателей
        if (left >= right) {
            return;
        }
        // Рекурсия для двух половин массива
        int mid = left + (right - left) / 2;
        mergeSort(a, left, mid);
        mergeSort(a, mid + 1, right);
        // Слияние отсортированных частей
        merge(a, left, mid, right);
    }

    // Слияние отсортированных частей массива
    private static void merge(int[] a, int left, int mid, int right) {
        int[] buffer = new int[right - left + 1];
        int i = left;
        int j = mid + 1;
        int k = 0;

        // Сперва добавляем меньшие элементы из одной из двух частей
        while (i <= mid && j <= right) {
            buffer[k++] = (a[i] <= a[j]) ? a[i++] : a[j++];
        }
        // Подгоняем одну из оставшихся границ: либо левую
        while (i <= mid) {
            buffer[k++] = a[i++];
        }
        // Либо правую
        while (j <= right) {
            buffer[k++] = a[j++];
        }
        // Запись в массив a тех же элементов отсортированными
        System.arraycopy(buffer, 0, a, left, buffer.length);
    }
}