package types;

import java.util.Iterator;
import java.util.LinkedList;
import model.QuaxBoard;

public class QuaxTileGroup implements Iterable<QuaxTile> {

	private LinkedList<QuaxTile> groupMembers;
	private boolean onColumnA_Row1;
	private boolean onColumnK_Row11;


	public QuaxTileGroup() {
		this.groupMembers = new LinkedList<>();
		this.onColumnA_Row1 = false;
		this.onColumnK_Row11 = false;
	}
	
	public QuaxTileGroup(QuaxTile initialMember) {
		this();
		this.addTile(initialMember);
	}


	public int size() {
		return groupMembers.size();
	}

	public boolean isWinningGroup() {
		return onColumnA_Row1 && onColumnK_Row11;
	}


	public void addTile(QuaxTile tile) {
		tile.setTileGroup(this);
		groupMembers.addFirst(tile);

		if (tile.onLow()) {
            onColumnA_Row1 = true;
        }
		if (tile.onHigh()) {
            onColumnK_Row11 = true;
        }
	}

	public void merge(QuaxTileGroup mergee) {
		this.onColumnA_Row1 = this.onColumnA_Row1 || mergee.onColumnA_Row1;
		this.onColumnK_Row11 = this.onColumnK_Row11 || mergee.onColumnK_Row11;
		
		this.groupMembers.addAll(mergee.groupMembers);
		for (QuaxTile t : mergee.groupMembers) {
			t.setTileGroup(this);
		}
	}



	public Iterator<QuaxTile> iterator() {
		return groupMembers.iterator();
	}
}
