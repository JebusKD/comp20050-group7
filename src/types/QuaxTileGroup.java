package types;

import java.util.Iterator;
import java.util.LinkedList;

public class QuaxTileGroup implements Iterable<QuaxTile> {

	private LinkedList<QuaxTile> groupMembers;
	private boolean onColumnA;
	private boolean onColumnK;
	private boolean onRow1;
	private boolean onRow11;

	public QuaxTileGroup() {
		this.groupMembers = new LinkedList<>();
		this.onColumnA = false;
		this.onColumnK = false;
		this.onRow1 = false;
		this.onRow11 = false;
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

		updateRows(tile);
		updateColumns(tile);
	}

	private void updateRows(QuaxTile t) {
		if (t.onLow()) {
			onRow1 = true;
		}
		if (t.onHigh()) {
			onRow11 = true;
		}
	}

	private void updateColumns(QuaxTile t) {
		if (t.onLeft()) {
			onColumnA = true;
		}
		if (t.onRight()) {
			onColumnK = true;
		}
	}

	public boolean isWinningGroup() {
		return (onRow1 && onRow11) && isBlackGroup() ||
				(onColumnA && onColumnK) && isWhiteGroup();
	}

	// TODO - LoD?
	private boolean isWhiteGroup() {
		return groupMembers.getFirst().isWhite();
	}

	private boolean isBlackGroup() {
		return groupMembers.getFirst().isBlack();
	}
	
	public void merge(QuaxTileGroup mergee) {
		mergeLocations(mergee);

		this.groupMembers.addAll(mergee.groupMembers);
		for (QuaxTile t : mergee.groupMembers) {
			t.setGroup(this);
		}
	}

	private void mergeLocations(QuaxTileGroup mergee) {
		this.onColumnA = this.onColumnA || mergee.onColumnA;
		this.onColumnK = this.onColumnK || mergee.onColumnK;
		this.onRow1 = this.onRow1 || mergee.onRow1;
		this.onRow11 = this.onRow11 || mergee.onRow11;
	}
	
	public Iterator<QuaxTile> iterator() {
		return groupMembers.iterator();
	}
}
