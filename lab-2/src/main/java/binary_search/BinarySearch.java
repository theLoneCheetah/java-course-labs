package binary_search;

/**
 * Бинарный поиск в отсортированном по возрастанию массиве
 */
public final class BinarySearch {

    private BinarySearch() {
    }

    /** 
     * Возвращает индекс target или -1, если не найден.
     * Массив должен быть отсортирован, это ответственность пользователя, иначе теряется O(logn) по времени
     */
    public static int search(int[] a, int target) {
        if (a == null) {
            throw new NullPointerException("array must not be null");
        }
        int lo = 0;
        int hi = a.length - 1;

        // Сдвигаем границы к центру
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;   // формула позволяет избежать переполнения int
            if (a[mid] == target) {
                return mid;   // если нашли, возвращаем первый подходящий индекс
            }
            if (a[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return -1;   // если не нашли
    }
}