package eager_singleton;

/**
 * Singleton с жадной инициализацией: экземпляр создаётся при загрузке класса
 */
public final class EagerSingleton {

    private static int instancesCreated = 0;
    // Статическое создание объекта происходит единожды - инициализация классов атомарна
    private static final EagerSingleton INSTANCE = new EagerSingleton();

    // Закрытый констурктор
    private EagerSingleton() {
        instancesCreated++;
    }

    /** Публичная точка доступа к объекту */
    public static EagerSingleton getInstance() {
        return INSTANCE;
    }

    /** Для тестов: сколько раз вызывался конструктор */
    static int getInstancesCreated() {
        return instancesCreated;
    }
}