/**
 * Instance teto tridy predstavuje jeden vesmirny objekt.
 * Vesmirny objekt ma jmeno,
 * typ, pozici a rychlost reprezentovany instancemi tridy Coord2D a vahu.
 */
public class SpaceObj {
    /**
     * Nazev vesmirneho objektu
     */
    private String name;
    /**
     * Typ vesmirneho objektu
     */
    private String type;
    /**
     * Pozice vesmirneho objektu reprezentovany instanci tridy Coord2D
     */
    private Coord2D pos;
    /**
     * Rychlost vesmirneho objektu reprezentovany instanci tridy Coord2D
     */
    private Coord2D vel;
    /**
     * Vaha vesmirneho objektu (kg)
     */
    private double weight;
    /**
     * Velikost vesmirneho objektu (m)
     */
    private double size;

    /**
     * Konstruktor pro vesmirny objekt
     * @param name Nazev
     * @param type Typ
     * @param pos Pozice vesmirneho objektu reprezentovany instanci tridy Coord2D
     * @param vel Rychlost vesmirneho objektu reprezentovany instanci tridy Coord2D
     * @param weight Vaha vesmirneho objektu (kg)
     */
    public SpaceObj(String name, String type, Coord2D pos, Coord2D vel, double weight){
        this.name = name;
        this.type = type;
        this.pos = pos;
        this.vel = vel;
        this.weight = weight;
        this.size = getRadius() * 2;
    }

    /**
     * Vraci se polomer tohoto vesmirneho objektu. Predpoklada se jednotkova hustota a vyuziva se objem koule
     * @return Polomer tohoto objektu
     */
    public double getRadius(){
        Double r = Math.cbrt(3*weight/4*Math.PI);
        return r;
    }

    //GETTERY A SETTERY

    /**
     * @return Velikost vesmirneho objektu
     */
    public double getSize(){
        return size;
    }

    /**
     * Nastavi velikost vesmirneho objektu
     * @param size Velikost
     */
    public void setSize(double size) {
        this.size = size;
    }

    /**
     * Nastavi pozici vesmirneho objektu
     * @param pos Instance tridy Coord2D
     */
    public void setPos(Coord2D pos) {
        this.pos = pos;
    }

    /**
     * Nastavi rychlost vesmirneho objektu
     * @param vel Instance tridy Coord2D
     */
    public void setVel(Coord2D vel) {
        this.vel = vel;
    }

    /**
     * @return Nazev vesmirneho objektu
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return Typ vesmirneho objektu
     */
    public String getType() {
        return type;
    }

    /**
     * @return Pozice vesmirneho objektu
     */
    public Coord2D getPos() {
        return pos;
    }

    /**
     * @return Rychlost vesmirneho objektu
     */
    public Coord2D getVel() {
        return vel;
    }

    /**
     * @return Vaha vesmirneho objektu
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @return String ktery reprezentuje tuto instanci
     */
    @Override
    public String toString() {
        return "SpaceObj{" +
                "name='" + name + '\'' +
                ", pos=" + pos +
                ", vel=" + vel +
                ", weight=" + weight +
                '}';
    }

}
