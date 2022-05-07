import java.util.LinkedList;
import java.util.List;

/**
 * Pomocna trida, ktera propojuje 2 listy s jednim objektem
 * @param <T> Datovy typ listu
 * @param <Y> Datovy typ listu
 * @param <Z> Objekt v relaci s datovymi typy
 */
public class Link<T,Y,Z> {
    private Z link;
    private List<T> itemX;
    private List<Y> itemY;

    /**
     * Konstruktor teto tridy
     * @param link Objekt, ktery je v relaci se spojovyma seznamama
     */
    public Link(Z link){
        this.link = link;
        this.itemX = new LinkedList<>();
        this.itemY = new LinkedList<>();
    }

    /**
     * Prida prvek do listu itemX.
     * @param value Hodnota prvku
     */
    public void addToItemX(T value){
        itemX.add(value);
    }

    /**
     * Vymaze z listu itemX prvni prvek, pokud je to mozne.
     */
    public void removeFirstFromItemX(){
        if(itemX.stream().findFirst().isPresent()){
            itemX.remove(itemX.stream().findFirst().get());
        }
    }

    /**
     * Prida prvek do listu itemY.
     * @param value Hodnota prvku
     */
    public void addToItemY(Y value){
        itemY.add(value);
    }

    /**
     * Vymaze z listu itemY prvni prvek, pokud je to mozne.
     */
    public void removeFirstFromItemY(){
        if(itemY.stream().findFirst().isPresent()){
            itemY.remove(itemY.stream().findFirst().get());
        }
    }

    /**
     * Vraci seznam itemX.
     * @return List itemX
     */
    public List<T> getItemX() {
        return itemX;
    }

    /**
     * Vraci seznam itemY.
     * @return List itemY
     */
    public List<Y> getItemY() {
        return itemY;
    }

    /**
     * Vraci objekt ktery je v relaci s listem itemX a itemY.
     * @return Objekt, ktery je v relaci s listem itemX a itemY.
     */
    public Z getLink() {
        return link;
    }
}
