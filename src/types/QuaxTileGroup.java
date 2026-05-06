package types;

import java.util.*;

/* Represent a collection of connected Tiles of the same colour */
public class QuaxTileGroup implements Iterable<QuaxTile> {

	public static final QuaxTileGroup UNASSIGNED_GROUP = new PlaceholderGroup("unassigned");
	
	private final LinkedList<QuaxTile> groupMembers;


	public QuaxTileGroup() {
		this.groupMembers = new LinkedList<>();
	}
	
	public QuaxTileGroup(QuaxTile initialMember) {
		this();
		
		if (initialMember == null) {
			throw new IllegalArgumentException("QuaxTileGroup cannot be initialised with a null member.");
		}
		if (!initialMember.tileExists()) {
			throw new IllegalArgumentException("Initial Member does not exist.");
		}
		
		this.addTile(initialMember);
	}


	public int size() {
		assert groupMembers != null;
		
		return groupMembers.size();
	}

	public boolean isWinningGroup() {
		return distanceToWalls() == 0;
	}


	public void addTile(QuaxTile tile) {
		if (tile == null) {
			throw new IllegalArgumentException("Cannot add null tile to QuaxTileGroup.");
		}
		if (!tile.tileExists()) {
			throw new IllegalArgumentException("Non-existing tile cannot be added to QuaxTileGroup.");
		}
		
		tile.setTileGroup(this);
		groupMembers.addFirst(tile);
	}

	public void merge(QuaxTileGroup mergee) {
		if (mergee == null) {
			throw new IllegalArgumentException("Cannot merge null group.");
		}
		if (!mergee.groupExists()) {
			throw new IllegalArgumentException("Cannot merge with non-existing group.");
		}
		assert this.groupMembers != null && mergee.groupMembers != null;

		this.groupMembers.addAll(mergee.groupMembers);

		for (QuaxTile t : mergee.groupMembers) {
			assert t != null && t.tileExists();
			t.setTileGroup(this);
		}
	}

	public int distanceToWalls() {
		return groupDistanceToLowWall() + groupDistanceToHighWall();
	}

	/*
	 * Calculate the minimum distance of the given tile group to the edges of the board,
	 * 	primarily so the bot may find a more optimal tile to progress
	 */
	private int groupDistanceToLowWall() {
		int minimumDistance = 10;
		for (QuaxTile t : this) {
			if (t instanceof Octagon o) {
				minimumDistance = Math.min(o.distanceToLowWall(), minimumDistance);
			}
		}
		return minimumDistance;
	}

	private int groupDistanceToHighWall() {
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
		assert groupMembers != null;
		return groupMembers.iterator();
	}

	public boolean groupExists() {
		return this != UNASSIGNED_GROUP;
	}
	
    /* Placeholder groups are used similarly to PlaceholderTiles where
     * they are used as a special object instead of null references in
     * methods that change or otherwise access a tile's group
     *
     */
	private static class PlaceholderGroup extends QuaxTileGroup {
		private String type;
		
		private PlaceholderGroup(String type) {
			super();
			assert type != null;
			this.type = type;
		}
		
		private UnsupportedOperationException placeholderUsedException() {
			assert type != null;
			return new UnsupportedOperationException("Cannot be invoked on " + type + " tilegroup.");
		}
		
		@Override
		public int size() {
			throw placeholderUsedException();
		}

		@Override
		public boolean isWinningGroup() {
			throw placeholderUsedException();
		}

		@Override
		public void addTile(QuaxTile tile) {
			throw placeholderUsedException();
		}

		@Override
		public void merge(QuaxTileGroup mergee) {
			throw placeholderUsedException();
		}

		@Override
		public int distanceToWalls() {
			throw placeholderUsedException();
		}

		@Override
		public QuaxTileColour getGroupColour() {
			throw placeholderUsedException();
		}
		
		@Override
		public Iterator<QuaxTile> iterator() {
			throw placeholderUsedException();
		}
		
	}
}
