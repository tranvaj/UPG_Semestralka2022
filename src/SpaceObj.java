/**
 * An instance of this class represents a single space object.
 * A space object has a name, type, X Y pos, X Y velocity and weight.
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
     * Space object weight (kg)
     */
    private double weight;
    /**
     * Size of space object (m)
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

    //GETTERS AND SETTERS

    /**
     * @return The size of the object
     */
    public double getSize(){
        return size;
    }

    /**
     * Sets the size of the object.
     * @param size Size
     */
    public void setSize(double size) {
        this.size = size;
    }

    /**
     * Sets the position coordinates of the object
     * @param pos Instance of Coord2D
     */
    public void setPos(Coord2D pos) {
        this.pos = pos;
    }

    /**
     * Sets the velocity coordinates of the object
     * @param vel Instance of Coord2D
     */
    public void setVel(Coord2D vel) {
        this.vel = vel;
    }

    /**
     * @return Name of the object.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return Type of the object.
     */
    public String getType() {
        return type;
    }

    /**
     * @return Instance of Coord2D representing position.
     */
    public Coord2D getPos() {
        return pos;
    }

    /**
     * @return Instance of Coord2D representing velocity
     */
    public Coord2D getVel() {
        return vel;
    }

    /**
     * @return Weight of the object
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @return Instance of String that represents this object.
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
