package map;

import java.util.Objects;

/**
 * Хеш-таблица с разрешением коллизий методом цепочек
 */
public class CustomHashMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;   // "золотое сечение"

    private Node<K, V>[] table;
    private int size;

    // Инициализируется пустым массивом нод
    @SuppressWarnings("unchecked")
    public CustomHashMap() {
        this.table = new Node[DEFAULT_CAPACITY];
    }

    /** Кладёт пару, возвращает предыдущее значение или null */
    public V put(K key, V value) {
        if (size >= table.length * LOAD_FACTOR) {
            resize();   // расширить при достижении границы
        }
        int index = indexFor(key);
        // Проходим по бакету с одинаковым хешем, если нашли такой же ключ - заменяем и возвращаем старое значение
        for (Node<K, V> n = table[index]; n != null; n = n.next) {
            if (Objects.equals(n.key, key)) {
                V old = n.value;
                n.value = value;
                return old;
            }
        }
        // Если совпадений нет, создаём ноду и ставим в начало бакета
        Node<K, V> node = new Node<>(key, value);
        node.next = table[index];
        table[index] = node;
        size++;
        return null;
    }

    /** Возвращает значение по ключу или null */
    public V get(K key) {
        int index = indexFor(key);
        for (Node<K, V> n = table[index]; n != null; n = n.next) {
            if (Objects.equals(n.key, key)) {
                return n.value;
            }
        }
        return null;
    }

    /** Удаляет пару, возвращает значение или null */
    public V remove(K key) {
        int index = indexFor(key);
        // Предыдущий элемент запоминается, и остаток бакета записывается в next к нему
        Node<K, V> prev = null;
        for (Node<K, V> n = table[index]; n != null; n = n.next) {
            if (Objects.equals(n.key, key)) {
                if (prev == null) {   // если удаляется первый элемент
                    table[index] = n.next;
                } else {
                    prev.next = n.next;
                }
                size--;
                return n.value;
            }
            prev = n;
        }
        return null;
    }

    /** Количество пар в таблице */
    public int size() {
        return size;
    }

    /** Пуста ли таблица */
    public boolean isEmpty() {
        return size == 0;
    }

    // Получение индекса через хэш
    private int indexFor(K key) {
        // null в 0 индекс, отрицательные числа инвертируются
        int h = (key == null) ? 0 : (key.hashCode() & 0x7fffffff);
        return h % table.length;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] old = table;
        table = new Node[old.length * 2];   // увеличение размера в 2 раза
        // Перебор старого словаря
        for (Node<K, V> head : old) {
            Node<K, V> n = head;
            // Перебор бакета, вставка новых элементов в новый бакет в начало
            while (n != null) {
                Node<K, V> next = n.next;
                int index = indexFor(n.key);
                n.next = table[index];
                table[index] = n;
                n = next;
            }
        }
    }

    // Внутренний класс узлов (нод) с неизменяемым ключом, значением, ссылкой на следующий элемент (для коллизий)
    private static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}