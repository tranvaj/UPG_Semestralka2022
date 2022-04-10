/**
 * Tato trida reprezentuje X,Y souradnice. Vyuziva se navrhovy vzor Prepravka.
 */
public class Coord2D {
    private double x;
    private double y;

    /**
     * Konstruktor, ktery si ulozi predane X-ove a Y-ove souradnice do svych atributu
     * @param x X-ove souradnice
     * @param y Y-ove souradnice
     */
    public Coord2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    /**
     * Vraci x-ove souradnice
     * @return X-ove souradnice vraceny jako double
     */
    public double getX() {
        return x;
    }

    /**
     * Vraci y-ove souradnice
     * @return Y-souradnice vraceny jako double
     */
    public double getY() {
        return y;
    }

    /**
     * @return String reprezentance teto instance tridy
     */
    @Override
    public String toString() {
        return "Coord2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
