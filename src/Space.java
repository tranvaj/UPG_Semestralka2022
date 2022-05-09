import java.util.*;

/**
 * Tato trida reprezentuje vesmir, ktery obsahuje vesmirne objekty.
 * Vesmir ma urcenou vlastni gravitacni konstantu a bezi v simulacnim casu
 * @author Vaclav Tran
 */
public class Space {
    private List<SpaceObj> spaceObjs;
    private double gravConst;
    private double stepTime;

    private long simStartTime;
    private double simulationTime;
    /**
     * Cas od posledniho spusteni metody updateSystem
     */
    private double timeSinceLastUpdateAbsolute;



    private boolean simPaused = false;
    private long pauseStart = 0;
    private long pausedTime = 0;
    private long pausedTimeTotal = 0;

    /**
     *
     * @param spaceObjs Seznam obsahujici vesmirne objekty
     * @param G Gravitacni konstanta
     * @param t Krok v case (1s v realnem zivote = t sekund v tomto vesmiru)
     */
    public Space(List<SpaceObj> spaceObjs, double G, double t){
        this.spaceObjs = spaceObjs;
        this.gravConst = G;
        this.stepTime = t;
        this.timeSinceLastUpdateAbsolute = 0;
        this.simStartTime = 0;

        this.timeVelLinkList = new LinkedList<>();
        this.trackTimeStart = Double.NaN;
    }

    /**
     * Vraci se seznam vsech vesmirnych objektu ktery existuji v tomto vesmiru
     * @return Seznam vsech instanci tridy SpaceObj
     */
    public List<SpaceObj> getSpaceObjs() {
        return spaceObjs;
    }

    /**
     * Vraci gravitacni konstantu vesmiru
     * @return Gravitacni konstanta tohoto vesmiru
     */
    public double getGravConst() {
        return gravConst;
    }

    /**
     * Vraci krok v case vesmiru
     * @return Krok v case tohoto vesmiru
     */
    public double getStepTime() {
        return stepTime;
    }

    /**
     * Tato metoda nastavuje pocatecni simulacni cas
     * @param simStartTime Momentalni cas v milisekundach
     */
    public void setSimStartTime(long simStartTime) {
        this.simStartTime = simStartTime;
    }

    private double relativeTimeSinceLastUpdate() {
        //odecte se simulacni cas od casu kdy se na posledy spustila metoda updateSystem
        //ziskame takto ubehnutou dobu od posledniho spusteni metody updateSystem
        return ((getSimulationTime()  -  timeSinceLastUpdateAbsolute));
    }

    /**
     * Tato metoda vraci simulacni cas v sekundach
     * @return Simulacni cas v sekundach
     */
    public double getSimulationTime() {
        simulationTime = (getCurrentTime()/1000.0)*stepTime;
        return simulationTime;
    }


    private Double trackTimeStart;
    private List<Link<Double,Double,SpaceObj>> timeVelLinkList;

    /**
     * Metoda zaznamena rychlost a momentalni cas objektu
     * @param planet Objekt, ktery ma byt zaznamenavan
     */
    private void trackPlanetVel(SpaceObj planet) {
        if(planet == null) return;
        double currTime = ((System.currentTimeMillis() - simStartTime)/1000.0);
        Link<Double,Double,SpaceObj> correctLink = null;

        if(Double.isNaN(trackTimeStart)){
            trackTimeStart = currTime;
        }

        double trackTimeElapsed = currTime - trackTimeStart;

        for(int i = 0; i < timeVelLinkList.size(); i++){
            if(timeVelLinkList.get(i) != null){
                if(timeVelLinkList.get(i).getLink() == null){
                    continue;
                }
                if(timeVelLinkList.get(i).getLink().equals(planet)){
                    correctLink = timeVelLinkList.get(i);
                    break;
                }
            }
            if(i == timeVelLinkList.size() - 1){
                correctLink = new Link<>(planet);
                timeVelLinkList.add(correctLink);
            }
        }

        if(timeVelLinkList.size() == 0){
            correctLink = new Link<>(planet);
            timeVelLinkList.add(correctLink);
        }

        if(correctLink == null) return;

        //System.out.println((getCurrentTime()/1000.0)+ " - " + trackTimeStart + " = " +  trackTimeElapsed);
        correctLink.addToItemX(trackTimeElapsed);
        correctLink.addToItemY(correctLink.getLink().getVel().size());

        if(trackTimeElapsed > 30){
            double lastTime = correctLink.getItemX().get(correctLink.getItemX().size()-1);
            double firstTime = correctLink.getItemX().get(0);
            while(lastTime-firstTime > 30){
                if(correctLink.getItemX().size() <= 2 || correctLink.getItemY().size() <= 2){
                    //kontrola pro jistotu
                    break;
                }
                correctLink.removeFirstFromItemX();
                correctLink.removeFirstFromItemY();
                lastTime = correctLink.getItemX().get(correctLink.getItemX().size()-1);
                firstTime = correctLink.getItemX().get(0);
            }
        }

    }

    private double trackTimeStep = 0.05;
    private double trackTimeLastUpdate;

    /**
     * Zaznamena vsechny rychlosti a casy vsech vesmirnych objektu
     */
    public void trackPlanetVelAll(){
        double currTime = ((System.currentTimeMillis() - simStartTime)/1000.0);

        double trackTimeElapsed = currTime - trackTimeStart;

        if(trackTimeElapsed-trackTimeLastUpdate < trackTimeStep){
            return;
        }

        for(SpaceObj s : spaceObjs){
            trackPlanetVel(s);
        }

        trackTimeLastUpdate = trackTimeElapsed;
        //System.out.println(timeVelLinkList.size());
    }

    /**
     * Metoda se snazi najit strukturu s rychlosti a casem, ktery patri danemu objektu z parametru
     * @param planet Objekt, kteremu patri tato struktura
     * @return Strukturu, ktera v sobe uchovava cas, rychlost v case a vesmirny objekt kteremu toto patri.
     */
    public Link<Double,Double,SpaceObj> getVelTimeLink(SpaceObj planet){
        for(Link<Double,Double,SpaceObj> x : timeVelLinkList){
            if(x.getLink().equals(planet)){
                //System.out.println("yay");
                return x;
            }
        }
        return null;
    }

    /**
     * Vraci ubehnuty cas v milisekundach, ktery neni ovlivnen krokem v casu
     * @return Ubehnuty cas v milisekundach, ktery neni ovlivnen krokem v casu
     */
    public long getCurrentTime(){
        if(simPaused) {
            //jelikoz getSimulationTime vyvolava tento cas
            //updatneme ubehnutou dobu od zacatku pauzy
            updatePauseTime();
        }
        return (System.currentTimeMillis() - simStartTime) - pausedTime;
    }

    /**
     * Zastavi simulaci.
     */
    public void startPause() {
        this.simPaused = true;
        //Ulozime si cas od zacatku pauzy abychom mohli napocitat ubehnutou dobu od pauzy
        this.pauseStart = System.currentTimeMillis();
        updatePauseTime();
    }

    private void updatePauseTime() {
        //Abychom zastavili cas, musime odecitat ubehnutou dobu od zacatku pauzy z momentalniho casu.
        //Takto jakoby "pozastavime" cas
        //Aby fungovala pauza vicekrat, musime jeste odecist sumu vsech dob z predchozich pauz
        pausedTime = (System.currentTimeMillis() - pauseStart) + pausedTimeTotal;
    }

    /**
     * Obnovi simulaci.
     */
    public void stopPause(){
        this.simPaused = false;
        //pri obnoveni simulace se ulozi ubehnuta doba od zacatku pauzy sem (v pausedTime je ulozena i ubehnuta doba vsech predchozich pauz)
        //az zacne dalsi pauza, tento cas bude ulozeny do pausedTime, viz metoda updatePauseTime
        pausedTimeTotal = pausedTime;
    }

    /**
     * Tato metoda vraci, zda je simulace pozastavena nebo ne
     * @return Vraci, zda je simulace pozastavena nebo ne
     */
    public boolean isSimPaused() {
        return simPaused;
    }

    /**
     * Metoda kontroluje jestli nenastala kolize objektu,
     * pokud ano, vesmirne objekty se spoji.
     */
    public void checkCollision(){
        for(SpaceObj i : spaceObjs){
            for(SpaceObj j : spaceObjs){
                if(i == null || j == null || i.equals(j)){
                    continue;
                }
                if(i.collide(j)){
                    if(j.getName().equals("Planet55")){
                        System.out.println();
                    }
                    //prvek se jakoby "smaze", predtim jsem pouzival remove, ale hodne to blblo
                    spaceObjs.set(spaceObjs.indexOf(j),null);
                }
            }
        }
    }


    /**
     * Vypocita a ulozi nove pozice a rychlosti vsech vesmirnych objektu v simulacnim case
     */
    public void updateSystem(){

        //Ubehnuta doba od posledniho spusteni teto metody se ulozi do promenne t
        double t = relativeTimeSinceLastUpdate();

        //Minimalni zmena v case
        double dt_min = stepTime/2000;

        while(t > 0){
            double dt = Math.min(t, dt_min);

            //Vypocitame zrychleni a ulozime do seznamu
            List<Coord2D> accelerationList = new ArrayList<>();
            for(int i = 0; i < spaceObjs.size(); i++){
                Coord2D acceleration = getAcceleration(i);
                accelerationList.add(i,acceleration);
            }

            //Vypocitani a ulozeni novych pozic a rychlosti vesmirnych objektu
            for(int i = 0; i < spaceObjs.size(); i++){
                SpaceObj spaceObj = spaceObjs.get(i);
                if(spaceObj == null) continue;
                if(accelerationList.get(i) == null) continue;
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
        //Ulozime cas od posledniho spusteni teto metody
        timeSinceLastUpdateAbsolute = getSimulationTime();
    }

    /**
     * Tato metoda vypocita zrychleni vesmirneho objektu v seznamu spaceObjs na indexu predany parametrem
     * @param index Index vesmirneho objektu obsazeny v seznamu spaceObjs
     * @return Zrychleni reprezentovany instanci tridy Coord2D
     */
    private Coord2D getAcceleration(int index){
        SpaceObj obj_i = spaceObjs.get(index);
        if(obj_i == null) return null;
        double a_i_x = 0;
        double a_i_y = 0;
        for(int j = 0; j < spaceObjs.size(); j++){
            if(j == index) continue;
            SpaceObj obj_j = spaceObjs.get(j);
            if(obj_j == null) continue;

            double dx =  obj_j.getPos().getX() - obj_i.getPos().getX();
            double dy =  obj_j.getPos().getY() - obj_i.getPos().getY();
            double dist = Math.sqrt(dx*dx + dy*dy);
            a_i_x += obj_j.getWeight() * (dx/Math.pow(dist,3));
            a_i_y += obj_j.getWeight() * dy/Math.pow(dist,3);
        }
        return new Coord2D( gravConst * a_i_x , gravConst * a_i_y );
    }
}
