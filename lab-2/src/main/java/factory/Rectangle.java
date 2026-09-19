package factory;

/**
 * Прямоугольник
 */
public class Rectangle implements Shape {

    private final double width;
    private final double height;

    // Инициализируется двумя сторонами
    public Rectangle(double width, double height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("sides must be >= 0");
        }
        this.width = width;
        this.height = height;
    }

    // S = a * b
    @Override
    public double area() {
        return width * height;
    }

    @Override
    public String name() {
        return "Rectangle";
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}