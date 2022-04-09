/**
 * This class represents X,Y coordinates of an object
 */
public class Coord2D {
    private double x;
    private double y;

    /**
     * Double representation of X,Y coordinates of an object
     * @param x coordinates
     * @param y coordinates
     */
    public Coord2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the X-coordinate in double value
     * @return X-coordinate in double value
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the Y-coordinate in double value
     * @return Y-coordinate in double value
     */
    public double getY() {
        return y;
    }

    /**
     * @return String representation of this instance
     */
    @Override
    public String toString() {
        return "Coord2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
