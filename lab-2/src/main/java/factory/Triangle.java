package factory;

/**
 * Треугольник
 */
public class Triangle implements Shape {

    private final double a;
    private final double b;
    private final double c;

    // Инициализируется тремя сторонами, проверяется правило треугольника
    public Triangle(double a, double b, double c) {
        if (a < 0 || b < 0 || c < 0) {
            throw new IllegalArgumentException("sides must be >= 0");
        }
        if (a + b <= c || a + c <= b || b + c <= a) {
            throw new IllegalArgumentException("triangle inequality violated");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }

    // Площадь по формуле Герона
    @Override
    public double area() {
        double p = (a + b + c) / 2.0;
        return Math.sqrt(p * (p - a) * (p - b) * (p - c));
    }

    @Override
    public String name() {
        return "Triangle";
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }
}