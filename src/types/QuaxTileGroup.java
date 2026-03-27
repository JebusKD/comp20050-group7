package types;

import java.util.Iterator;
import java.util.LinkedList;

public class QuaxTileGroup implements Iterable<QuaxTile> {

	private LinkedList<QuaxTile> groupMembers;
    // TODO rename variables
	private boolean presenceLow; // Group presence on a or 1 rank
	private boolean presenceHigh; // Group presence on k or 11 rank
	
	public QuaxTileGroup() {
		this.groupMembers = new LinkedList<>();
		this.presenceLow = false;
		this.presenceHigh = false;
	}
	
	public QuaxTileGroup(QuaxTile initialMember) {
		this();
		this.addTile(initialMember);
	}
	
	public int size() {
		return groupMembers.size();
	}
	
	public void addTile(QuaxTile tile) {
		tile.setGroup(this);
		groupMembers.addFirst(tile);
		if (tile.onLow()) presenceLow = true;
		if (tile.onHigh()) presenceHigh = true;
	}
	
	public boolean isWinningGroup() {
		return presenceLow && presenceHigh;
	}
	
	public void merge(QuaxTileGroup mergee) {
		this.presenceLow = this.presenceLow || mergee.presenceLow;
		this.presenceHigh = this.presenceHigh || mergee.presenceHigh;
		
		this.groupMembers.addAll(mergee.groupMembers);
		for (QuaxTile t : mergee.groupMembers) {
			t.setGroup(this);
		}
	}
	
	public Iterator<QuaxTile> iterator() {
		return groupMembers.iterator();
	}

}
