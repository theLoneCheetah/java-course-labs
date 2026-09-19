package map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomHashMapTest {

    /** Ключ с постоянным hashCode — для проверки коллизий */
    static class CollidingKey {
        final int id;
        CollidingKey(int id) { this.id = id; }
        @Override public int hashCode() { return 42; }   // одинаковый для всех хэш
        @Override public boolean equals(Object obj) {   // сравнение объектов
            return obj instanceof CollidingKey && ((CollidingKey) obj).id == id;
        }
    }

    // Базовая проверка элементов
    @Test
    void putAndGetBasic() {
        CustomHashMap<String, Integer> map = new CustomHashMap<>();
        assertNull(map.put("a", 1));
        assertNull(map.put("b", 2));
        assertNull(map.put("c", 3));

        assertEquals(3, map.size());
        assertEquals(1, map.get("a"));
        assertEquals(2, map.get("b"));
        assertEquals(3, map.get("c"));
    }

    // Проверка замены элемента
    @Test
    void putExistingKeyReturnsOldAndOverwrites() {
        CustomHashMap<String, Integer> map = new CustomHashMap<>();
        map.put("a", 1);

        assertEquals(1, map.put("a", 10));
        assertEquals(10, map.get("a"));
        assertEquals(1, map.size());
    }

    // Удаление элементов, в том числе отсутствующего
    @Test
    void removeExistingAndMissing() {
        CustomHashMap<String, Integer> map = new CustomHashMap<>();
        map.put("a", 1);
        map.put("b", 2);

        assertEquals(1, map.remove("a"));
        assertNull(map.get("a"));
        assertEquals(1, map.size());

        assertNull(map.remove("nope"));
        assertEquals(1, map.size());
    }

    // Корректность работы с коллизиями
    @Test
    void collisionsAreHandled() {
        CustomHashMap<CollidingKey, String> map = new CustomHashMap<>();
        CollidingKey k1 = new CollidingKey(1);
        CollidingKey k2 = new CollidingKey(2);
        CollidingKey k3 = new CollidingKey(3);

        map.put(k1, "one");
        map.put(k2, "two");
        map.put(k3, "three");

        assertEquals(3, map.size());
        assertEquals("one", map.get(k1));
        assertEquals("two", map.get(k2));
        assertEquals("three", map.get(k3));

        assertEquals("two", map.remove(k2));
        assertNull(map.get(k2));
        assertEquals("one", map.get(k1));
        assertEquals("three", map.get(k3));
    }

    // Сохранение при расширении
    @Test
    void resizeKeepsAllEntries() {
        CustomHashMap<Integer, String> map = new CustomHashMap<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, "v" + i);
        }
        assertEquals(100, map.size());
        for (int i = 0; i < 100; i++) {
            assertEquals("v" + i, map.get(i));
        }
    }
}