package factory;

/**
 * Фабричный метод: создаёт фигуру по типу и набору параметров
 */
public final class ShapeFactory {

    // Нельзя создать экземпляр фабрики
    private ShapeFactory() {
    }

    // При создании передаётся тип фигуры и набор параметров
    public static Shape create(ShapeType type, double... params) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("params must not be null");
        }

        // Проверка числа параметров в зависимости от типа фигуры и возврат созданного класса
        switch (type) {
            case CIRCLE:
                requireParams(type, params, 1);
                return new Circle(params[0]);
            case RECTANGLE:
                requireParams(type, params, 2);
                return new Rectangle(params[0], params[1]);
            case TRIANGLE:
                requireParams(type, params, 3);
                return new Triangle(params[0], params[1], params[2]);
            default:
                throw new IllegalArgumentException("Unknown shape type: " + type);
        }
    }

    // Проверка числа параметров и выброс исключения при несовпадении
    private static void requireParams(ShapeType type, double[] params, int expected) {
        if (params.length != expected) {
            throw new IllegalArgumentException(
                    type + " requires " + expected + " parameter(s), got " + params.length);
        }
    }
}