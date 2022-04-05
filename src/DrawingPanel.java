import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DrawingPanel extends JPanel {
    private Coord2D minObj;
    private Coord2D maxObj;
    private double simulatedTime;
    private Space space;
    //scaleA reprezentuje
    private double scaleA = 1;
    //min velikost objektu
    private double minSize = 5;
    //min
    java.util.List<Shape> spaceObjShapeList;
    SpaceObj selectedObj;


    public DrawingPanel(Space space){
        this.space = space;
        //this.startTime = System.nanoTime();
        //simulatedTime = ((System.nanoTime()-startTime)/1000000000.0)*space.getStepTime();
        this.setPreferredSize(new Dimension(800, 600));
    }

    private double scaleTest = 0;
    private double offsetXtest = 0;
    private double offsetYtest = 0;
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g;


        /*
        DRAWING OBJECTS IN SPACE
         */
        AffineTransform old = g2.getTransform();

        double scale = getScale();
        scaleTest = scale;
        double spaceWidth = Math.abs(maxObj.getX() - minObj.getX());
        double spaceHeight = Math.abs(maxObj.getY() - minObj.getY());

        double offsetX = (this.getWidth() - spaceWidth*scale) / 2;
        double offsetY = (this.getHeight() - spaceHeight*scale) / 2;
        offsetXtest = offsetX;
        offsetYtest = offsetY;

        //System.out.println(offsetX+","+offsetY);

        //debug
        space.getSpaceObjs().forEach(spaceObj -> {
            //System.out.println(spaceObj);
            //System.out.println(minObj);
            //System.out.println(maxObj);
        });

        g2.translate(offsetX,offsetY);
        g2.scale(scale,scale);
        drawPlanets(g2);

        //debug rect
        Rectangle2D rect = new Rectangle2D.Double(0, 0,spaceWidth,spaceHeight);
        g2.setColor(Color.BLUE);
        //g2.draw(rect);

        //space.updateSystem(simulatedTime);

        g2.setTransform(old);

        //draw time
        g2.setColor(Color.BLACK);
        drawTime(g2);
    }

    public void drawTime(Graphics2D g2){
        //((System.nanoTime()-startTime)/1000000000.0)*space.getStepTime();
        simulatedTime = space.getSimulationTime();
        //Double roundedSimTime = Math.ro
        //DecimalFormat df = new DecimalFormat("#.#");
        //String str = "Current time: " + (df.format(simulatedTime)) + "s";
        String str = "Current time: " + simulatedTime + "s";
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
            double size = a.getSize() * scaleA;
            double x1 = a.getPos().getX() - size/2;
            double y1 = a.getPos().getY() - size/2;
            double x2 = a.getPos().getX() + size/2;
            double y2 = a.getPos().getY() +  size/2;
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
        g2.setColor(Color.RED);
        spaceObjShapeList = new ArrayList<>();
        space.getSpaceObjs().forEach(spaceObj -> {
            if(spaceObj.getType().equals("Planet")){
                //Double r = calculateR(spaceObj);
                Double xPos = spaceObj.getPos().getX();
                Double yPos = spaceObj.getPos().getY();
                double size = spaceObj.getSize()*scaleA;
                //-minObj.get(x) abychom posunuli objekt na kladne souradnice -> aby fungoval scale normalne
                if(size*getScale() < minSize){
                    double temp = size*getScale();
                    double minScale = minSize/temp;
                    size = size * minScale;
                }
                Ellipse2D el = new Ellipse2D.Double(-minObj.getX()+xPos-(size/2),-minObj.getY()+yPos-(size/2),size,size);
                spaceObjShapeList.add(el);
                if(selectedObj != null){
                   if(spaceObj.equals(selectedObj)){
                        g2.setColor(Color.YELLOW);
                   }
                }
                g2.fill(el);
                g2.setColor(Color.RED);
            }
        });

    }

    public void getSelected(Coord2D mouseCoord) {

        spaceObjShapeList.forEach(shape -> {
            double scale = scaleTest;
            double spaceWidth = Math.abs(maxObj.getX() - minObj.getX());
            double spaceHeight = Math.abs(maxObj.getY() - minObj.getY());

            double offsetX = (this.getWidth() - spaceWidth*scale) / 2;
            double offsetY = (this.getHeight() - spaceHeight*scale) / 2;
            double mouseX = ((mouseCoord.getX()/scale) - offsetX/scale);
            double mouseY = ((mouseCoord.getY()/scale) - offsetY/scale);
            if(shape.contains(mouseX ,mouseY)){
                System.out.println("HRLLOsadaa\n\nadsad");
                if(selectedObj != null && selectedObj.equals(space.getSpaceObjs().get(spaceObjShapeList.indexOf(shape)))){
                    selectedObj = null;
                } else{
                    selectedObj = space.getSpaceObjs().get(spaceObjShapeList.indexOf(shape));
                }
            }
        });
    }
}
