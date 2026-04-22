package types;

import java.util.Iterator;
import java.util.LinkedList;

public class QuaxTileStrategyGroup implements Iterable<QuaxTile> {
    private LinkedList<QuaxTile> members;

    public QuaxTileStrategyGroup(){
        this.members = new LinkedList<QuaxTile>();
    }

    public void addTile(QuaxTile tile) {
        tile.setStrategyGroup(this);
        members.addFirst(tile);
    }

    public void removeTile(QuaxTile tile) {
        members.remove(tile);
    }

    public int size(){
        return members.size();
    }

    public QuaxTileStrategyGroup(QuaxTile initialMember) {
        this();
        this.addTile(initialMember);
    }


    public Iterator<QuaxTile> iterator() {
        return members.iterator();
    }
}