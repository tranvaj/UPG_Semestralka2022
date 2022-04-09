import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DrawingPanel extends JPanel {
    private Coord2D minObj;
    private Coord2D maxObj;
    private Space space;

    Color spaceObjDefaultColor = Color.BLUE;
    Color spaceObjSelectedColor = Color.RED;
    //extraScale zvetsi vsechny objekty o extraScale-krat
    private double extraObjScale = 1;
    //Minimalni velikost objektu
    private double minObjSize = 5;

    //Kolekce vsech vykreslenych objektu
    java.util.List<Shape> spaceObjShapeList;
    //Prave zvoleny objekt
    SpaceObj selectedObj;

    private double currentScale;
    private double currentOffsetX;
    private double currentOffsetY;


    public DrawingPanel(Space space){
        this.space = space;
        this.setMinimumSize(new Dimension(800, 600));
        this.setPreferredSize(new Dimension(800, 600));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g;

        //Drawing objects in space start
        AffineTransform old = g2.getTransform();

        double scale = getScale();
        double spaceWidth = Math.abs(maxObj.getX() - minObj.getX());
        double spaceHeight = Math.abs(maxObj.getY() - minObj.getY());
        double offsetX = (this.getWidth() - spaceWidth*scale) / 2;
        double offsetY = (this.getHeight() - spaceHeight*scale) / 2;

        this.currentScale = scale;
        this.currentOffsetX = offsetX;
        this.currentOffsetY = offsetY;

        g2.translate(offsetX,offsetY);
        g2.scale(scale,scale);
        drawPlanets(g2);

        //Debug rect
        //Rectangle2D rect = new Rectangle2D.Double(0, 0,spaceWidth,spaceHeight);
        //g2.setColor(Color.GREEN);
        //g2.draw(rect);

        g2.setTransform(old);
        //Drawing objects in space end

        //draw time
        g2.setColor(Color.BLACK);
        drawTime(g2);

        if(selectedObj != null) {
            drawSelectedInfo(g2);
        }
    }


    public void drawTime(Graphics2D g2){
        //((System.nanoTime()-startTime)/1000000000.0)*space.getStepTime();
        Double simulatedTime = space.getSimulationTime();

        simulatedTime = Math.floor(simulatedTime*10000);
        simulatedTime = simulatedTime / 10000;


        //Double roundedSimTime = Math.ro
        //DecimalFormat df = new DecimalFormat("#.#");
        //String str = "Current time: " + (df.format(simulatedTime)) + "s";
        String str = "Simulated time: " + simulatedTime + "s";
        //str = String.format("Current time: %f s", df.format(simulatedTime));
        Font font = new Font("Arial",Font.BOLD, 14);
        g2.setFont(font);
        g2.drawString(str,this.getWidth() - g2.getFontMetrics().stringWidth(str),g2.getFontMetrics().getHeight());
    }


    public double getScale(){
        getMinMaxBounds();
        double spaceWidth = Math.abs(maxObj.getX() - minObj.getX());
        double spaceHeight = Math.abs(maxObj.getY() - minObj.getY());
        //System.out.println(Math.abs(maxObj.getY() - minObj.getY()));
        //double spaceSize = Math.max(spaceWidth,spaceHeight);
        //double scale = Math.min(this.getWidth(),this.getHeight())/spaceSize;
        double scaleX = this.getWidth() / spaceWidth;
        double scaleY = this.getHeight() / spaceHeight;
        return Math.min(scaleX,scaleY);
    }
    public void getMinMaxBounds(){
        double minX,minY,maxX,maxY;
        double x_def = space.getSpaceObjs().get(0).getPos().getX();
        double y_def = space.getSpaceObjs().get(0).getPos().getY();
        minX = x_def; minY = y_def; maxX =  x_def; maxY = y_def;
        for(SpaceObj a : space.getSpaceObjs()){
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
        //System.out.println("MINX: " + (maxX));
        minObj = new Coord2D(minX,minY);
        maxObj = new Coord2D(maxX,maxY);
    }


    public void drawPlanets(Graphics2D g2){
        g2.setColor(spaceObjDefaultColor);
        spaceObjShapeList = new ArrayList<>();
        space.getSpaceObjs().forEach(spaceObj -> {
            if(spaceObj.getType().equals("Planet")){
                Double xPos = spaceObj.getPos().getX();
                Double yPos = spaceObj.getPos().getY();
                double size = spaceObj.getSize() * extraObjScale;

                if(size*currentScale < minObjSize){
                    double temp = size*currentScale;
                    double minScale = minObjSize /temp;
                    size = size * minScale;
                    spaceObj.setSize(size);
                    getScale();
                }

                //-minObj.get(x) abychom posunuli objekt na kladne souradnice -> aby fungoval scale normalne
                Ellipse2D el = new Ellipse2D.Double(-minObj.getX()+xPos-(size/2.0),-minObj.getY()+yPos-(size/2.0),size,size);

                //System.out.println("-min :" + -minObj.getX() + " + xPos: " + xPos + "=  " + (-minObj.getX()+xPos-(size/2.0)));

                //selection
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

    public SpaceObj getSelected(Coord2D mouseCoord) {
        spaceObjShapeList.forEach(shape -> {
            double scale = currentScale;
            double offsetX = currentOffsetX;
            double offsetY = currentOffsetY;

            double mouseX = ((mouseCoord.getX()/scale) - offsetX/scale);
            double mouseY = ((mouseCoord.getY()/scale) - offsetY/scale);
            if(shape.contains(mouseX ,mouseY)){
                //System.out.println("Clicked!");
                if(selectedObj != null && selectedObj.equals(space.getSpaceObjs().get(spaceObjShapeList.indexOf(shape)))){
                    selectedObj = null;
                } else{
                    selectedObj = space.getSpaceObjs().get(spaceObjShapeList.indexOf(shape));
                }
            }
        });
        return selectedObj;
    }

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
