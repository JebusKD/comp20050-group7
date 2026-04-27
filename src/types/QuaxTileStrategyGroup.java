package types;

import java.util.Iterator;
import java.util.LinkedList;


public class QuaxTileStrategyGroup implements Iterable<QuaxTile> {

    private LinkedList<QuaxTile> strategyMembers;


    public QuaxTileStrategyGroup() {
        this.strategyMembers = new LinkedList<>();
    }
    // TODO - Remove unused constructor
    public QuaxTileStrategyGroup(QuaxTile initialMember) {
        this();
        this.addTile(initialMember);
    }


    public void addTile(QuaxTile tile) {
        tile.setTileStrategyGroup(this);
        strategyMembers.addFirst(tile);
    }

    public void removeTile(QuaxTile tile) {
        strategyMembers.remove(tile);
    }

    // TODO - rename
    public int size(){
        return strategyMembers.size();
    }


    public Iterator<QuaxTile> iterator() {
        return strategyMembers.iterator();
    }
}