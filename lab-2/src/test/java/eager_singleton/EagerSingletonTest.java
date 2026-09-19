package eager_singleton;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class EagerSingletonTest {

    // Стандартная проверка создания единственного экземпляра
    @Test
    void returnsSameInstance() {
        assertSame(EagerSingleton.getInstance(), EagerSingleton.getInstance());
    }

    // Конструктор один, закрытый
    @Test
    void constructorIsPrivate() {
        Constructor<?>[] ctors = EagerSingleton.class.getDeclaredConstructors();
        assertEquals(1, ctors.length);
        assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
    }

    // Синглтон создаётся ровно один
    @Test
    void onlyOneInstanceIsCreated() {
        EagerSingleton.getInstance();
        assertEquals(1, EagerSingleton.getInstancesCreated());
    }

    // Потокобезопасность: нет состояния гонки при создании синглтона
    @Test
    void threadSafe() throws Exception {
        int threads = 16;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            List<Future<EagerSingleton>> futures = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                futures.add(pool.submit(EagerSingleton::getInstance));
            }

            EagerSingleton first = futures.get(0).get();
            for (Future<EagerSingleton> f : futures) {
                assertSame(first, f.get());
            }
            assertEquals(1, EagerSingleton.getInstancesCreated());
        } finally {
            pool.shutdown();
        }
    }
}