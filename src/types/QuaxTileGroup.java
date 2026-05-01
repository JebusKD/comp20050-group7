package types;

import java.util.Iterator;
import java.util.LinkedList;


public class QuaxTileGroup implements Iterable<QuaxTile> {

	private final LinkedList<QuaxTile> groupMembers;

	public QuaxTileGroup() {
		this.groupMembers = new LinkedList<>();
	}
	
	public QuaxTileGroup(QuaxTile initialMember) {
		this();
		this.addTile(initialMember);
	}


	public int size() {
		return groupMembers.size();
	}

	public boolean isWinningGroup() {
		return distanceToWalls() == 0;
	}


	public void addTile(QuaxTile tile) {
		tile.setTileGroup(this);
		groupMembers.addFirst(tile);
	}

	public void merge(QuaxTileGroup mergee) {
		this.groupMembers.addAll(mergee.groupMembers);
		for (QuaxTile t : mergee.groupMembers) {
			t.setTileGroup(this);
		}
	}

	public int distanceToWalls() {
		return distanceToLowWall() + distanceToHighWall();
	}

	/* //TODO - How about this?
	 * Calculate the minimum distance of the given tile group to  the edges of the board,
	 * 	so the bot may find a more optimal tile to progress
	 */
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
