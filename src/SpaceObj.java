/**
 * Instance teto tridy predstavuje jeden vesmirny objekt.
 * Vesmirny objekt ma jmeno,
 * typ, pozici a rychlost reprezentovany instancemi tridy Coord2D a vahu.
 * @author Vaclav Tran
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

    /**
     * Metoda nastavi kolizi, pokud tento objekt prekryva souradnice stredu druheho objektu a
     * <br>pokud je tento objekt tezsi nez ten druhy.<br>
     * <br>Pokud nastane kolize, tento objekt absorbuje druhy tak, ze si prepocita rychlost pomoci
     * zakonu zachovani hybnosti, pricte si k sobe vahu druheho objektu a prepocita si svoji velikost.
     * @param other Objekt se kterym nastane kolize.
     * @return True, pokud nastala kolize, jinak False
     */
    public boolean collide(SpaceObj other){
        if(Coord2D.distanceTo(this.getPos(),other.getPos()) <= this.getSize()/2){
            double velX = (this.weight*this.vel.getX() + other.weight*other.vel.getX())/(this.weight + other.weight);
            double velY = (this.weight*this.vel.getY() + other.weight*other.vel.getY())/(this.weight + other.weight);
            if(this.weight < other.weight){
                return false;
            } else{
                setVel(new Coord2D(velX,velY));
                setWeight(this.weight + other.getWeight());
                setSize(getRadius()*2);
                return true;
            }
        }
        return false;
    }


    //GETTERY A SETTERY

    /**
     * Vraci velikost vesmirneho objektu
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
     * Vraci nazev vesmirneho objektu
     * @return Nazev vesmirneho objektu
     */
    public String getName() {
        return name;
    }

    /**
     * Vraci typ vesmirneho objektu
     * @return Typ vesmirneho objektu
     */
    public String getType() {
        return type;
    }

    /**
     * Vraci pozici vesmirneho objektu
     * @return Pozice vesmirneho objektu
     */
    public Coord2D getPos() {
        return pos;
    }

    /**
     * Vraci rychlost vesmirneho objektu
     * @return Rychlost vesmirneho objektu
     */
    public Coord2D getVel() {
        return vel;
    }

    /**
     * Vraci vahu vesmirneho objektu
     * @return Vaha vesmirneho objektu
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Nastavi vahu vesmirneho objektu
     * @param weight Vaha vesmirneho objektu
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Vraci String reprezentaci instance teto tridy
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
