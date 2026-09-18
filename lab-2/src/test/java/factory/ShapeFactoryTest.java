package factory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShapeFactoryTest {

    private static final double EPS = 1e-9;   // допустимая при тестах погрешность

    // Круг: стандартное поведение
    @Test
    void createsCircle() {
        Shape s = ShapeFactory.create(ShapeType.CIRCLE, 2.0);
        assertInstanceOf(Circle.class, s);
        assertEquals("Circle", s.name());
        assertEquals(Math.PI * 4, s.area(), EPS);
    }

    // Прямоугольник: стандартное поведение
    @Test
    void createsRectangle() {
        Shape s = ShapeFactory.create(ShapeType.RECTANGLE, 3.0, 4.0);
        assertInstanceOf(Rectangle.class, s);
        assertEquals("Rectangle", s.name());
        assertEquals(12.0, s.area(), EPS);
    }

    // Греугольник: стандартное поведение
    @Test
    void createsTriangle() {
        Shape s = ShapeFactory.create(ShapeType.TRIANGLE, 3.0, 4.0, 5.0);
        assertInstanceOf(Triangle.class, s);
        assertEquals("Triangle", s.name());
        assertEquals(6.0, s.area(), EPS);
    }

    // Исключения при неверном числе параметров
    @Test
    void wrongNumberOfParamsThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ShapeFactory.create(ShapeType.CIRCLE));
        assertThrows(IllegalArgumentException.class,
                () -> ShapeFactory.create(ShapeType.CIRCLE, 1.0, 2.0));
        assertThrows(IllegalArgumentException.class,
                () -> ShapeFactory.create(ShapeType.RECTANGLE, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> ShapeFactory.create(ShapeType.TRIANGLE, 1.0, 2.0));
    }

    // Исключение при передаче null типа
    @Test
    void nullTypeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ShapeFactory.create(null, 1.0));
    }

    // Неверные параметры, включая нарушение правила треугольника
    @Test
    void negativeSizesThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> ShapeFactory.create(ShapeType.CIRCLE, -1.0));
        assertThrows(IllegalArgumentException.class,
                () -> ShapeFactory.create(ShapeType.RECTANGLE, -1.0, 2.0));
        assertThrows(IllegalArgumentException.class,
                () -> ShapeFactory.create(ShapeType.TRIANGLE, -3.0, 4.0, 5.0));
        assertThrows(IllegalArgumentException.class,
                () -> ShapeFactory.create(ShapeType.TRIANGLE, 1.0, 2.0, 10.0));
    }
}