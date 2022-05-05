/**
 * Tato trida reprezentuje X,Y souradnice. Vyuziva se navrhovy vzor Prepravka.
 * @author Vaclav Tran
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
     * Vraci velikost vektoru (x,y)
     * @return Velikost vektory (x,y)
     */
    public double size(){
        return Math.sqrt(x*x + y*y);
    }

    /**
     * Vypocita vzdalenost mezi bodama
     * @param a Bod 1
     * @param b Bod 2
     * @return Vzdalenost mezi danymi body
     */
    public static double distanceTo(Coord2D a, Coord2D b){
        //Math.sqrt((x1 - x2)^2 + (y1 - y2)^2)
        double x1 = a.getX();
        double x2 = b.getX();
        double y1 = a.getY();
        double y2 = b.getY();
        double dist = Math.sqrt(Math.pow((x1 - x2),2) + Math.pow((y1 - y2),2));
        return dist;
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
     * Vraci String reprezentaci instance teto tridy
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
