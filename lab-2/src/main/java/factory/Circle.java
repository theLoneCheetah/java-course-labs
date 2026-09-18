package factory;

/**
 * Круг
 */
public class Circle implements Shape {

    private final double radius;

    // Инициализируется радиусом
    public Circle(double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("radius must be >= 0");
        }
        this.radius = radius;
    }

    // S = pi * r^2
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    @Override
    public String name() {
        return "Circle";
    }

    public double getRadius() {
        return radius;
    }
}