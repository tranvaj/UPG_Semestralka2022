/**
 * This class represents X,Y coordinates of an object
 */
public class Coord2D {
    private double x;
    private double y;

    /**
     * Integer X,Y coordinates of an object
     * @param x coordinates
     * @param y coordinates
     */
    public Coord2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coord2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
