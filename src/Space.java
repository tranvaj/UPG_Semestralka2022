import java.util.ArrayList;
import java.util.List;

public class Space {
    private List<SpaceObj> spaceObjs;
    private double gravConst;
    private double stepTime;

    private long simStartTime;
    private double simulationTime;
    private double timeSinceLastUpdateAbsolute;

    public Space(List<SpaceObj> spaceObjs, double G, double t){
        this.spaceObjs = spaceObjs;
        this.gravConst = G;
        this.stepTime = t;
        this.timeSinceLastUpdateAbsolute = 0;
        this.simStartTime = 0;
    }

    public List<SpaceObj> getSpaceObjs() {
        return spaceObjs;
    }

    public double getGravConst() {
        return gravConst;
    }

    public double getStepTime() {
        return stepTime;
    }

    public void setSimStartTime(long simStartTime) {
        this.simStartTime = simStartTime;
    }

    public double relativeTimeSinceLastUpdate() {
        System.out.println("curent: " + getSimulationTime() + "\nsincelastupdate: " + timeSinceLastUpdateAbsolute);
        return ((getSimulationTime()  -  timeSinceLastUpdateAbsolute));
    }

    public double getSimulationTime() {
        simulationTime = (getCurrentTime()/1000.0)*stepTime;
        return simulationTime;
    }

    public long getCurrentTime(){
        return System.currentTimeMillis()-simStartTime;
    }

    /**
     * Aktualizuje pozici prvku SpaceObj v spaceObjs pomoci simulace N-objektu
     * s
     */
    public void updateSystem(){
        //ubehnuta doba
        //timeElapsedSinceUpdate = time - timeElapsedSinceUpdate;
        double t = relativeTimeSinceLastUpdate();
        //System.out.println("time" + t);
        //ubehnuta doba
        //Ziskame zmenu v case, pokud ubehnuta doba je vetsi nez dt_min, tak zmena v case je dt_min
        double dt_min = stepTime/3000;

        while(t > 0){
            double dt = Math.min(t, dt_min);

            //Vypocitame zrychleni vsech spaceObj a ulozime do kolekce
            List<Coord2D> accelerationList = new ArrayList<>();
            for(int i = 0; i < spaceObjs.size(); i++){
                Coord2D acceleration = getAcceleration(i);
                accelerationList.add(i,acceleration);
            }

            for(int i = 0; i < spaceObjs.size(); i++){
                SpaceObj spaceObj = getSpaceObjs().get(i);
                double speedX = spaceObj.getVel().getX() + 0.5 * dt * accelerationList.get(i).getX();
                double speedY = spaceObj.getVel().getY() + 0.5 * dt * accelerationList.get(i).getY();
                double posX = spaceObj.getPos().getX() + dt * speedX;
                double posY = spaceObj.getPos().getY() + dt * speedY;
                speedX += 0.5 * dt * accelerationList.get(i).getX();
                speedY += 0.5 * dt * accelerationList.get(i).getY();
                spaceObj.setVel(new Coord2D(speedX,speedY));
                spaceObj.setPos(new Coord2D(posX,posY));

                double xd = accelerationList.get(i).getX();
                double yd = accelerationList.get(i).getY();
                //System.out.println(Math.sqrt(xd*xd + yd*yd));
            }

            t = t-dt;
            //System.out.println("working... - " + t);
        }
        timeSinceLastUpdateAbsolute = getSimulationTime();
    }

    private Coord2D getAcceleration(int index){
        SpaceObj obj_i = spaceObjs.get(index);
        double a_i_x = 0;
        double a_i_y = 0;
        for(int j = 0; j < spaceObjs.size(); j++){
            if(j == index) continue;
            SpaceObj obj_j = spaceObjs.get(j);
            double dx =  obj_j.getPos().getX() - obj_i.getPos().getX();
            double dy =  obj_j.getPos().getY() - obj_i.getPos().getY();
            double dist = Math.sqrt(dx*dx + dy*dy);
            a_i_x += obj_j.getWeight() * (dx/Math.pow(dist,3));
            a_i_y += obj_j.getWeight() * dy/Math.pow(dist,3);


        }
        //System.out.println(gravConst * a_i_x + " " + gravConst * a_i_y);
        return new Coord2D( gravConst * a_i_x , gravConst * a_i_y );
    }

}
