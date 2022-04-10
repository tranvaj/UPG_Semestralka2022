import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Instance teto tridy nacita a zpracovava soubor ve formatu CSV.
 * V teto tride je poskytnuta metoda, ktera ze souboru po zpracovani vrati
 * instanci tridy Space
 * @author Vaclav Tran
 *
 */
public class CSVLoader {
    String fileLoc;

    public CSVLoader(String fileLoc){
        this.fileLoc = fileLoc;
    }

    public Space parseDataToSpace() {
        try(Scanner sc = new Scanner(new File(fileLoc))){
            List<SpaceObj> spaceObjs = new ArrayList<>();
            String[] firstRow = sc.nextLine().split(",");
            //prvni radek je grav. konstanta a krok v case
            double gravConst = Double.parseDouble(firstRow[0]);
            double timeStep = Double.parseDouble(firstRow[1]);

            //zpracovani zbylych radku
            while(sc.hasNextLine()){
                String[] row = sc.nextLine().split(",");
                String name = row[0];
                String type = row[1];
                Coord2D pos = new Coord2D(Double.parseDouble(row[2]),Double.parseDouble(row[3]));
                Coord2D vel = new Coord2D(Double.parseDouble(row[4]),Double.parseDouble(row[5]));
                double weight = Double.parseDouble(row[6]);
                spaceObjs.add(new SpaceObj(name,type,pos,vel,weight));
            }
            return new Space(spaceObjs,gravConst,timeStep);
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

}
