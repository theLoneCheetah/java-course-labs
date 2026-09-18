package lazy_singleton;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class LazySingletonTest {

    // Стандартная проверка создания единственного экземпляра
    @Test
    void returnsSameInstance() {
        assertSame(LazySingleton.getInstance(), LazySingleton.getInstance());
    }

    // Конструктор один, закрытый
    @Test
    void constructorIsPrivate() {
        Constructor<?>[] ctors = LazySingleton.class.getDeclaredConstructors();
        assertEquals(1, ctors.length);
        assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
    }

    // Синглтон создаётся ровно один
    @Test
    void onlyOneInstanceIsCreated() {
        LazySingleton.getInstance();
        assertEquals(1, LazySingleton.getInstancesCreated());
    }

    // Потокобезопасность: нет состояния гонки при создании синглтона
    @Test
    void threadSafe() throws Exception {
        int threads = 16;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            List<Future<LazySingleton>> futures = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                futures.add(pool.submit(LazySingleton::getInstance));
            }

            LazySingleton first = futures.get(0).get();
            for (Future<LazySingleton> f : futures) {
                assertSame(first, f.get());
            }
            assertEquals(1, LazySingleton.getInstancesCreated());
        } finally {
            pool.shutdown();
        }
    }
}