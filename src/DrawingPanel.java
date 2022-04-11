import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

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
     * Konstruktor teto tridy
     * @param space Instance tridy Space
     */
    public DrawingPanel(Space space){
        this.space = space;
        this.setMinimumSize(new Dimension(800, 600));
        this.setPreferredSize(new Dimension(800, 600));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g;

        //Vykresleni vesmirnych objektu z vesmiru start
        AffineTransform old = g2.getTransform();

        //vypocitani scale hodnoty a offset na vycentrovani vesmiru
        double scale = getScale();
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

        //Debug obdelnik, pro testovani
        //Rectangle2D rect = new Rectangle2D.Double(0, 0,spaceWidth,spaceHeight);
        //g2.setColor(Color.GREEN);
        //g2.draw(rect);

        g2.setTransform(old);
        //konec vykreslovani vesmirnych objektu

        //vykresleni simulacniho casu
        g2.setColor(Color.BLACK);
        drawTime(g2);

        if(selectedObj != null) {
            drawSelectedInfo(g2);
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
     * @return Scale hodnota
     */
    public double getScale(){
        getMinMaxBounds();
        double spaceWidth = Math.abs(maxObj.getX() - minObj.getX());
        double spaceHeight = Math.abs(maxObj.getY() - minObj.getY());
        double scaleX = this.getWidth() / spaceWidth;
        double scaleY = this.getHeight() / spaceHeight;
        return Math.min(scaleX,scaleY);
    }

    /**
     * Tato metoda nam vypocita levy horni roh a pravy dolni roh obdelnika ktery minimalne ohranicuje vsechny nase
     * vesmirne objekty. V kalkulaci se pocita s velikostmi vesmirnych objektu.
     * Tyto souradnice jsou ulozeny do atributu minObj a maxObj.
     */
    public void getMinMaxBounds(){
        double minX,minY,maxX,maxY;
        double x_def = space.getSpaceObjs().get(0).getPos().getX();
        double y_def = space.getSpaceObjs().get(0).getPos().getY();
        minX = x_def; minY = y_def; maxX =  x_def; maxY = y_def;
        for(SpaceObj a : space.getSpaceObjs()){
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
        spaceObjShapeList = new ArrayList<>();
        space.getSpaceObjs().forEach(spaceObj -> {
            if(spaceObj.getType().equals("Planet") || spaceObj.getType().equals("Comet")){
                Double xPos = spaceObj.getPos().getX();
                Double yPos = spaceObj.getPos().getY();
                double size = spaceObj.getSize() * extraObjScale;
                //System.out.println(spaceObj.getName() + ":" + size);

                //minimalni velikost pokud (velikost planet * scale) je moc mala
                if(size*currentScale < minObjSize){
                    double temp = size*currentScale;
                    //vypocitame velikost ktera si bude skoro rovna velikosti minObjSize po vyuziti scale
                    double minScale = minObjSize /temp;
                    size = size * minScale;
                    //nas ohranicujici obdelnik je ted spatne vypocitany,
                    // potrebujem zavolat getScale, ktery vyvola v sobe getMinMaxBounds
                    spaceObj.setSize(size);
                    getScale();
                }

                //-minObj.getX a -minObj.getY aby byly nase planety v kladnych souradnicich
                //zajistujeme ze scale bude fungovat normalne
                //vykreslujeme vyplnene kruznice jehoz stredem jsou souradnice planet
                Ellipse2D el = new Ellipse2D.Double(-minObj.getX()+xPos-(size/2.0),-minObj.getY()+yPos-(size/2.0),size,size);


                //implementace vyberu/zruseni vyberu planety
                spaceObjShapeList.add(el);
                if(selectedObj != null){
                   if(spaceObj.equals(selectedObj)){
                        g2.setColor(spaceObjSelectedColor);
                   }
                }

                g2.fill(el);
                g2.setColor(spaceObjDefaultColor);
            }
        });

    }

    /**
     * Tato metoda nacte souradnice a snazi se vypocitat zda jsou tyto souradnice
     * obsazeny v nejake jedne instanci Shape, kde tato instance reprezentuje jeden vesmirny objekt
     * @param mouseCoord Souradnice
     * @return Vybrany vesmirny objekt
     */
    public SpaceObj getSelected(Coord2D mouseCoord) {
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
            }
        });
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
