package lazy_singleton;

/**
 * Singleton с ленивой инициализацией (initialization-on-demand holder).
 * Экземпляр создаётся при первом обращении к getInstance()
 */
public final class LazySingleton {

    private static int instancesCreated = 0;   // счётчик созданных экземпляров, не больше 1

    // Закрытый конструктор
    private LazySingleton() {
        instancesCreated++;
    }

    /** Публичная точка доступа */
    public static LazySingleton getInstance() {
        return Holder.INSTANCE;   // возвращает статический объект
    }

    /** Для тестов: сколько раз вызывался конструктор */
    static int getInstancesCreated() {
        return instancesCreated;
    }

    /** Вложенный класс загружается JVM только при первом обращении - "ленивость" */
    private static class Holder {
        // Холдер создаёт статический объект синглтона один раз при первом обращении
        static final LazySingleton INSTANCE = new LazySingleton();
    }
}