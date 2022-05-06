import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Instance teto tridy reprezentuje platno, na ktery se bude vykreslovat vesmir.
 * @author Vaclav Tran
 */
public class DrawingPanel extends JPanel {
    /**
     * Levy horni roh obdelniku ktery obsahuje vsechny vesmirne objekty pri minimalni velikosti
     */
    private Coord2D minObj;
    /**
     * Pravy dolni roh obdelniku ktery obsahuje vsechny vesmirne objekty pri minimalni velikosti
     */
    private Coord2D maxObj;
    /**
     * Instance tridy Space, obsahuje seznam vsech vesmirnych objektu
     */
    private Space space;
    /**
     * Vychozi barva vesmirneho objektu
     */
    Color spaceObjDefaultColor = Color.BLUE;
    /**
     * Vychozi barva vybraneho vesmirneho objektu
     */
    Color spaceObjSelectedColor = Color.RED;
    /**
     * Zvetseni velikosti vsech objektu danou hodnotou
     * Vyuzivany k testingu
     */
    private double extraObjScale = 1;
    /**
     * Minimalni velikost vesmirneho objektu v pixelech
     */
    private double minObjSize = 5;

    /**
     * Seznam ktery obsahuje vsechny instance tridy Shape, kde kazda jedna instance reprezentuje jednu instanci
     * v seznamu spaceObj
     */
    java.util.List<Shape> spaceObjShapeList;
    /**
     * Reference na momentalne vybrany vesmirny objekt
     */
    SpaceObj selectedObj;

    /**
     * Scale hodnota
     */
    private double currentScale;
    /**
     * X-ova hodnota kterou potrebujem k vycentrovani naseho vesmiru v platne
     */
    private double currentOffsetX;
    /**
     * Y-ova hodnota kterou potrebujem k vycentrovani naseho vesmiru v platne
     */
    private double currentOffsetY;

    /**
     * Cas pri prvnim spusteni drawTrail
     */
    private double trailTimeStart;
    /**
     * Pozice vsech objektu za posledni 1 sekundu realneho casu
     */
    private Queue<List<Coord2D>> trails;

    /**
     * Konstruktor teto tridy
     * @param space Instance tridy Space
     */
    public DrawingPanel(Space space){
        this.space = space;
        this.setMinimumSize(new Dimension(800, 600));
        this.setPreferredSize(new Dimension(800, 600));
    }

    /**
     * Metoda vykresluje na graficky kontext
     * @param g Graficky kontext
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g;

        //Vykresleni vesmirnych objektu z vesmiru start
        AffineTransform old = g2.getTransform();

        //vypocitani scale hodnoty a offset na vycentrovani vesmiru
        double scale = getScale(space.getSpaceObjs());
        double spaceWidth = Math.abs(maxObj.getX() - minObj.getX());
        double spaceHeight = Math.abs(maxObj.getY() - minObj.getY());
        double offsetX = (this.getWidth() - spaceWidth*scale) / 2;
        double offsetY = (this.getHeight() - spaceHeight*scale) / 2;

        this.currentScale = scale;
        this.currentOffsetX = offsetX;
        this.currentOffsetY = offsetY;

        //vycentrovani vesmiru
        g2.translate(offsetX,offsetY);
        //aby vsechny vesmirne objekty byly viditelne v kazdym case a vyplnovaly cely volny prostor tohoto platna
        g2.scale(scale,scale);

        drawPlanets(g2);
        if(spaceObjShapeList != null) drawTrails(g2);
        //prekreslime trajektorie
        drawPlanets(g2);



        //Debug obdelnik, pro testovani
        //Rectangle2D rect = new Rectangle2D.Double(0, 0,spaceWidth,spaceHeight);
        //g2.setColor(Color.GREEN);
        //g2.draw(rect);

        g2.setTransform(old);
        //konec vykreslovani vesmirnych objektu

        //vykresleni simulacniho casu
        g2.setColor(Color.BLACK);
        drawTime(g2);

        if(selectedObj != null && space.getSpaceObjs().contains(selectedObj)) {
            drawSelectedInfo(g2);
        } else if(!space.getSpaceObjs().contains(selectedObj)){
            selectedObj = null;
        }
    }

    /**
     * Vykresli simulacni cas na platno
     * @param g2 Graficky kontext
     */
    public void drawTime(Graphics2D g2){
        Double simulatedTime = space.getSimulationTime();

        //Zaokrouhleni simulacniho casu
        simulatedTime = Math.floor(simulatedTime*10000);
        simulatedTime = simulatedTime / 10000;

        //Vykresleni simulacniho casu
        //String str = "Simulated time: " + simulatedTime + "s";
        String str = String.format("Simulated time: %.3f s", simulatedTime);
        Font font = new Font("Arial",Font.BOLD, 14);
        g2.setFont(font);
        g2.drawString(str,this.getWidth() - g2.getFontMetrics().stringWidth(str),g2.getFontMetrics().getHeight());
    }

    /**
     * Vraci scale hodnotu ktera nam zvetsi/zmensi vesmir tak, aby vyplnoval cely prostor naseho platna a
     * aby byly videt vsechny vesmirny objekty v kazdym case
     * @param spaceObjs List vesmirnych objektu
     * @return Scale hodnota
     */
    public double getScale(java.util.List<SpaceObj> spaceObjs){
        getMinMaxBounds(spaceObjs);
        double spaceWidth = Math.abs(maxObj.getX() - minObj.getX());
        double spaceHeight = Math.abs(maxObj.getY() - minObj.getY());
        double scaleX = this.getWidth() / spaceWidth;
        double scaleY = this.getHeight() / spaceHeight;
        return Math.min(scaleX,scaleY);
    }


    /**
     * Metoda vykresli trajektorii vesmirnych objektu,
     * ktery zobrazuje pohyb provedeny za jednu sekundu realneho casu
     * @param g2 Graficky kontext
     */
    public void drawTrails(Graphics2D g2){
        if(trailTimeStart < 0.1) {
            trailTimeStart = space.getCurrentTime()/1000.0;
            trails = new LinkedList<>();
        }
        double currentTime = (space.getCurrentTime()/1000.0) - trailTimeStart;

        if(!space.isSimPaused()){
            trails.add(space.getSpaceObjs().stream().map(SpaceObj::getPos).toList());
            if (currentTime > 1) {
                trails.poll();
            }
        }

        g2.setColor(Color.LIGHT_GRAY);
        //velikosti objektu
        List<Double> sizes = spaceObjShapeList.stream().map(shape -> shape.getBounds2D().getWidth()).toList();
        //queue na arraylist
        List<List<Coord2D>> temp = trails.stream().toList();
        List<Path2D> paths = new LinkedList<>();


        //z ulozenych pozic ziskame jednotlive cesty vsech objektu
        for(int k = 0; k < temp.get(0).size(); k++){
            paths.add(new Path2D.Double());
        }
        for(int i = 0; i < temp.size(); i++){
            for(int j = 0; j < temp.get(i).size(); j++){
                Path2D path = paths.get(j);
                Coord2D pos = temp.get(i).get(j);
                if(i == 0) {
                    path.moveTo(-minObj.getX()+ pos.getX(),  -minObj.getY()+ pos.getY());
                } else{
                    path.lineTo(-minObj.getX() + pos.getX(), -minObj.getY()+ pos.getY());
                }
            }
        }

        //pokud nastane kolize, resetujeme frontu
        if(paths.size() != sizes.size()){
            trailTimeStart = space.getCurrentTime()/1000.0;
            trails = new LinkedList<>();
            return;
        }

        for(int i = 0; i < sizes.size(); i++){
            double stroke = (sizes.get(i));
            PathIterator it = paths.get(i).getPathIterator(null);

            //pocet bodu v ceste
            int count = 0;
            while(!it.isDone()){
                count++;
                it.next();
            }

            //ziskame vsechny body v ceste
            it = paths.get(i).getPathIterator(null);
            double increment = stroke/count;
            count = 0;
            while(!it.isDone()){
                //zde je ulozeny 1 bod cesty
                double[] coord = new double[6];
                it.currentSegment(coord);
                double size = increment*count;
                //vykreslime elipsu v bode cesty
                Ellipse2D el = new Ellipse2D.Double(coord[0]-size/2.0,coord[1]-size/2.0,size,size);
                g2.fill(el);
                //jdeme na dalsi bod v ceste
                it.next();
                count++;
            }
        }
    }

    /**
     * Tato metoda nam vypocita levy horni roh a pravy dolni roh obdelnika ktery minimalne ohranicuje vsechny nase
     * vesmirne objekty. V kalkulaci se pocita s velikostmi vesmirnych objektu.
     * Tyto souradnice jsou ulozeny do atributu minObj a maxObj.
     * @param spaceObjs List vesmirnych objektu
     */
    public void getMinMaxBounds(java.util.List<SpaceObj> spaceObjs){
        double minX,minY,maxX,maxY;
        double x_def = spaceObjs.get(0).getPos().getX();
        double y_def = spaceObjs.get(0).getPos().getY();
        minX = x_def; minY = y_def; maxX =  x_def; maxY = y_def;
        for(SpaceObj a : spaceObjs){
            //vypocitani rohu naseho ohranicujiciho obdelnika
            //velikosti jsou brany v potaz pri pocitani
            double size = a.getSize() * extraObjScale;
            double x1 = a.getPos().getX() - size/2;
            double y1 = a.getPos().getY() - size/2;
            double x2 = a.getPos().getX() + size/2;
            double y2 = a.getPos().getY() +  size/2;
            //System.out.println(a.getName() + ": " + x1 + "," +x2);
            if(x1 < minX) minX = x1;
            if(x2 > maxX) maxX = x2;
            if(y1 < minY) minY = y1;
            if(y2 > maxY) maxY = y2;
        }
        //ulozeni pozic rohu do techto dvou atributu
        minObj = new Coord2D(minX,minY);
        maxObj = new Coord2D(maxX,maxY);
    }


    /**
     * Tato metoda vykresli vsechny planety v nasem vesmiru na nas graficky kontext
     * @param g2 Graficky kontext
     */
    public void drawPlanets(Graphics2D g2){
        g2.setColor(spaceObjDefaultColor);
        spaceObjShapeList = new LinkedList<>();
        try{
        for (SpaceObj spaceObj : space.getSpaceObjs()) {
            if (spaceObj.getType().equals("Planet") || spaceObj.getType().equals("Comet")) {
                Double xPos = spaceObj.getPos().getX();
                Double yPos = spaceObj.getPos().getY();
                double size = spaceObj.getSize() * extraObjScale;

                //minimalni velikost pokud (velikost planet * scale) je moc mala
                if (size * currentScale < minObjSize) {
                    double temp = size * currentScale;
                    //vypocitame velikost ktera si bude skoro rovna velikosti minObjSize po vyuziti scale
                    double minScale = minObjSize / temp;
                    size = size * minScale;
                    //nas ohranicujici obdelnik je ted spatne vypocitany,
                    // potrebujem zavolat getScale, ktery vyvola v sobe getMinMaxBounds
                    //spaceObj.setSize(size);
                    getScale(space.getSpaceObjs());
                }

                //-minObj.getX a -minObj.getY aby byly nase planety v kladnych souradnicich
                //zajistujeme ze scale bude fungovat normalne
                //vykreslujeme vyplnene kruznice jehoz stredem jsou souradnice planet
                Ellipse2D el = new Ellipse2D.Double(-minObj.getX() + xPos - (size / 2.0), -minObj.getY() + yPos - (size / 2.0), size, size);

                //implementace vyberu/zruseni vyberu planety
                spaceObjShapeList.add(el);
                if (selectedObj != null) {
                    if (spaceObj.equals(selectedObj)) {
                        g2.setColor(spaceObjSelectedColor);
                    }
                }

                g2.fill(el);
                g2.setColor(spaceObjDefaultColor);
            }
        }
        } catch (Exception e){
            //nekdy nastane concurrentmodexception, nekdy ne, nevim jak to osetrit, problem s kolizi apod.
            //System.out.println("Error, skipping drawing frame: " + e.getMessage());
            //nastesti lidske oko preskoceny vykres nezaznamena
        }
    }

    /**
     * Tato metoda nacte souradnice a snazi se vypocitat zda jsou tyto souradnice
     * obsazeny v nejake jedne instanci Shape, kde tato instance reprezentuje jeden vesmirny objekt
     * @param mouseCoord Souradnice
     * @return Vybrany vesmirny objekt
     */
    public SpaceObj getSelected(Coord2D mouseCoord) {
        AtomicBoolean found = new AtomicBoolean(false);
        spaceObjShapeList.forEach(shape -> {
            double scale = currentScale;
            double offsetX = currentOffsetX;
            double offsetY = currentOffsetY;
            //ziskame spravne post transform souradnice
            double mouseX = ((mouseCoord.getX()/scale) - offsetX/scale);
            double mouseY = ((mouseCoord.getY()/scale) - offsetY/scale);
            if(shape.contains(mouseX ,mouseY)){
                //System.out.println("Clicked!");
                int index = spaceObjShapeList.indexOf(shape);
                if(selectedObj != null && selectedObj.equals(space.getSpaceObjs().get(index))){
                    selectedObj = null;
                } else{
                    selectedObj = space.getSpaceObjs().get(index);
                }
                found.set(true);
            }
        });
        if(found.get()){
            return selectedObj;
        } else{
            return null;
        }
    }

    /**
     * Vraci vybrany vesmirny objekt
     * @return Vybrany vesmirny objekt
     */
    public SpaceObj getSelectedObj() {
        return selectedObj;
    }

    /**
     * Vykresli pozici, rychlost a nazev vybraneho vesmirneho objektu
     * v dolni stredni casti platna na graficky kontext
     * @param g2 Graficky kontext
     */
    public void drawSelectedInfo(Graphics2D g2){
        // objSpeed = Math.sqrt(selectedObj.getVel().getX() * selectedObj.getVel().getX() + selectedObj.getVel().getY() * selectedObj.getVel().getY());
        String strx = String.format("Name: %s | Position: [X=%.2f | Y=%.2f] " +
                        "| Velocity: [X=%.2f | Y=%.2f]",
            selectedObj.getName(),
            selectedObj.getPos().getX(),
            selectedObj.getPos().getY(),
            selectedObj.getVel().getX(),
            selectedObj.getVel().getY()
        );
        Font font = new Font("Arial",Font.BOLD, 14);
        g2.setFont(font);
        g2.drawString(strx,this.getWidth()/2 - g2.getFontMetrics().stringWidth(strx)/2, this.getHeight() - g2.getFontMetrics().getHeight());
    }

}
