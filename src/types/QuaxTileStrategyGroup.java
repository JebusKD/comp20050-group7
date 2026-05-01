package types;

import java.util.*;


// TODO Consider deleting this class, replacing instances with Linked Lists.
public class QuaxTileStrategyGroup extends LinkedList<QuaxTile> {
	// Required by list interface
	private static final long serialVersionUID = 1L;

	public QuaxTileStrategyGroup() {
        super();
    }


    public void addTile(QuaxTile tile) {
        add(tile);
    }

    public void removeTile(QuaxTile tile) {
        remove(tile);
    }
}