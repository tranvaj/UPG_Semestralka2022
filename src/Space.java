import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Space that contains list of space objects, a gravitational constant and custom time.
 */
public class Space {
    private List<SpaceObj> spaceObjs;
    private double gravConst;
    private double stepTime;

    private long simStartTime;
    private double simulationTime;
    /**
     * Given simulation time since the last execution of updateSystem
     */
    private double timeSinceLastUpdateAbsolute;


    private boolean simPaused = false;
    private long pauseStart = 0;
    private long pausedTime = 0;
    private long pausedTimeTotal = 0;

    /**
     *
     * @param spaceObjs List containing instances of SpaceObj
     * @param G Gravitational constant
     * @param t Time step
     */
    public Space(List<SpaceObj> spaceObjs, double G, double t){
        this.spaceObjs = spaceObjs;
        this.gravConst = G;
        this.stepTime = t;
        this.timeSinceLastUpdateAbsolute = 0;
        this.simStartTime = 0;
    }

    /**
     * Gets collection that contains instances of SpaceObj.
     * @return List containing instances of SpaceObjs
     */
    public List<SpaceObj> getSpaceObjs() {
        return spaceObjs;
    }

    public double getGravConst() {
        return gravConst;
    }

    public double getStepTime() {
        return stepTime;
    }

    /**
     * Sets the simulation starting time in milliseconds.
     * @param simStartTime Time in milliseconds
     */
    public void setSimStartTime(long simStartTime) {
        this.simStartTime = simStartTime;
    }

    private double relativeTimeSinceLastUpdate() {
        //This gives us time elapsed since  the last execution of the updateSystem method in seconds
        return ((getSimulationTime()  -  timeSinceLastUpdateAbsolute));
    }

    /**
     * Gets the simulation time in seconds.
     * @return The simulation time in seconds
     */
    public double getSimulationTime() {
        simulationTime = (getCurrentTime()/1000.0)*stepTime;
        return simulationTime;
    }

    private long getCurrentTime(){
        if(simPaused) {
            //updates pauseTime to correctly subtract from current time while paused.
            updatePauseTime();
        }
        return (System.currentTimeMillis() - simStartTime) - pausedTime;
    }

    /**
     * Pauses the simulation.
     */
    public void startPause() {
        this.simPaused = true;
        //This exists to keep track of time elapsed since the
        // start of the pause (so we can later subtract it from current time to "stop" time)
        this.pauseStart = System.currentTimeMillis();
        updatePauseTime();
    }

    private void updatePauseTime() {
        //Calculates time to decrease from the current time to effectively "stop" time.
        //pausedTimeTotal is the sum of all paused times, needed to be able to pause time more than once.
        pausedTime = (System.currentTimeMillis() - pauseStart) + pausedTimeTotal;
    }

    /**
     * Unpauses the simulation
     */
    public void stopPause(){
        this.simPaused = false;
        //pausedTimeTotal keeps the "total pause time"
        //pausedTime already has the sum of all paused times in it (method updatePauseTime() is doing it)
        //,so no need to sum pausedTimeTotal with it here.
        pausedTimeTotal = pausedTime;
    }

    /**
     * Determines whether the simulation is paused or not.
     * @return true or false
     */
    public boolean isSimPaused() {
        return simPaused;
    }

    /**
     * Updates the position and velocity of all SpaceObjs according to the simulation time
     */
    public void updateSystem(){
        //Saving the time elapsed since last execution of this method into variable t
        double t = relativeTimeSinceLastUpdate();

        //The minimum change in time
        double dt_min = stepTime/2000;

        while(t > 0){
            double dt = Math.min(t, dt_min);

            //Calculate all SpaceObj accelerations and save them into a list
            List<Coord2D> accelerationList = new ArrayList<>();
            for(int i = 0; i < spaceObjs.size(); i++){
                Coord2D acceleration = getAcceleration(i);
                accelerationList.add(i,acceleration);
            }

            //Update velocity and position for each planet
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

            }
            t = t-dt;
        }
        //Saves the current simulation time into timeSinceLastUpdateAbsolute
        timeSinceLastUpdateAbsolute = getSimulationTime();
    }

    /**
     * Calculate acceleration of SpaceObj associated with given index.
     * @param index Index of the SpaceObj in the collection spaceObjs
     * @return Acceleration represented by an instance of Coord2D.
     */
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
        return new Coord2D( gravConst * a_i_x , gravConst * a_i_y );
    }
}
