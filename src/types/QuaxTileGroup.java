package types;

import java.util.Iterator;
import java.util.LinkedList;


public class QuaxTileGroup implements Iterable<QuaxTile> {

	private final LinkedList<QuaxTile> groupMembers;
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


	// TODO could this be used to get rid of onColumnA_Row1?
	public int distanceToWalls() {
		return distanceToLowWall() + distanceToHighWall();
	}

	// TODO Added these for the bot, although could be used elsewhere or not at all.
	private int distanceToLowWall() {
		int minimumDistance = 10;
		for (QuaxTile t : this) {
			if (t instanceof Octagon o) {
				minimumDistance = Math.min(o.distanceToLowWall(), minimumDistance);
			}
		}
		return minimumDistance;
	}

	private int distanceToHighWall() {
		int minimumDistance = 10;
		for (QuaxTile t : this) {
			if (t instanceof Octagon o) {
				minimumDistance = Math.min(o.distanceToHighWall(), minimumDistance);
			}
		}
		return minimumDistance;
	}


	public QuaxTileColour getGroupColour() {
		assert groupMembers.size() > 0;
		return groupMembers.getFirst().getTileColour();
	}
	
	public Iterator<QuaxTile> iterator() {
		return groupMembers.iterator();
	}
}
