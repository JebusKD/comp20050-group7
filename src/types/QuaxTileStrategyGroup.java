package types;

import java.util.Iterator;
import java.util.LinkedList;


public class QuaxTileStrategyGroup implements Iterable<QuaxTile> {

    private LinkedList<QuaxTile> strategyMembers;


    public QuaxTileStrategyGroup() {
        this.strategyMembers = new LinkedList<>();
    }


    public void addTile(QuaxTile tile) {
        tile.setTileStrategyGroup(this);
        strategyMembers.addFirst(tile);
    }

    public void removeTile(QuaxTile tile) {
        strategyMembers.remove(tile);
    }


    public int size() {
        return strategyMembers.size();
    }


    public Iterator<QuaxTile> iterator() {
        return strategyMembers.iterator();
    }
}