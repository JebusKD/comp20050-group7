package types;

import java.util.LinkedList;

public class QuaxTileGroup {

	private LinkedList<QuaxTile> members;
	private boolean presenceLow; // Group presence on a or 1 rank
	private boolean presenceHigh; // Group presence on k or 11 rank
	
	public QuaxTileGroup() {
		this.members = new LinkedList<QuaxTile>();
		this.presenceLow = false;
		this.presenceHigh = false;
	}
	
	public QuaxTileGroup(QuaxTile initialMember) {
		this();
		this.addTile(initialMember);
	}
	
	public int size() {
		return members.size();
	}
	
	public void addTile(QuaxTile tile) {
		members.addFirst(tile);
		if (tile.onLow()) presenceLow = true;
		if (tile.onHigh()) presenceHigh = true;
	}
	
	public boolean isWinningGroup() {
		return presenceLow && presenceHigh;
	}
	
	public void merge(QuaxTileGroup mergee) {
		this.members.addAll(mergee.members);
		this.presenceLow = this.presenceLow && mergee.presenceLow;
		this.presenceHigh = this.presenceHigh && mergee.presenceHigh;
	}
}
