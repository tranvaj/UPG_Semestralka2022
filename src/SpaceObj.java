/**
 * An instance of this class represents a single space object
 * A space object has a name; type; X,Y pos; X,Y velocity and weight
 */
public class SpaceObj {
    //obj name
    private String name;
    //obj type
    private String type;
    //X,Y coord of obj position
    private Coord2D pos;
    //X,Y coord of obj velocity
    private Coord2D vel;
    //obj weight
    private double weight;
    //size of obj
    //private double size;

    public SpaceObj(String name, String type, Coord2D pos, Coord2D vel, double weight){
        this.name = name;
        this.type = type;
        this.pos = pos;
        this.vel = vel;
        this.weight = weight;
    }

    //velikost ctverce ktery reprezentuje nas objekt
    //velikost je vypocitana za predpokladu, ze vsechny objekty maji jednotkovou hustotu
    public double getSize(){
        Double r = Math.cbrt(3*weight/4*Math.PI);
        //melo by
        return r;
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
        return "Planet{" +
                "name='" + name + '\'' +
                ", pos=" + pos +
                ", vel=" + vel +
                ", weight=" + weight +
                '}';
    }

}
