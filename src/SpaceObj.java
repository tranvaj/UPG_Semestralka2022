/**
 * An instance of this class represents a single space object
 * A space object has a name; type; X,Y pos; X,Y velocity and weight
 */
public class SpaceObj {
    /**
     * Name of the space object represented by instance of String
     */
    private String name;
    /**
     * Type of the space object represented by instance of String
     */
    private String type;
    /**
     * X,Y coord of space object position
     */
    private Coord2D pos;
    /**
     * X,Y coord of space object velocity
     */
    private Coord2D vel;
    /**
     * Space object weight
     */
    private double weight;
    /**
     * Size of space object
     */
    private double size;

    /**
     * Constructor of SpaceObj
     * @param name Name
     * @param type Type
     * @param pos Position represented by instance of Coord2D
     * @param vel Velocity represented by instance of Coord2D
     * @param weight Weight of the space object
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
     * Gets the radius of the spaceObj assuming density is equal to 1 and volume is represented by a sphere.
     * @return Radius of this instance
     */
    public double getRadius(){
        Double r = Math.cbrt(3*weight/4*Math.PI);
        return r;
    }

    public double getSize(){
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setPos(Coord2D pos) {
        this.pos = pos;
    }

    public void setVel(Coord2D vel) {
        this.vel = vel;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Coord2D getPos() {
        return pos;
    }

    public Coord2D getVel() {
        return vel;
    }

    public double getWeight() {
        return weight;
    }

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
