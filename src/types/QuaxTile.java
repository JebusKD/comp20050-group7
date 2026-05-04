package types;

import static types.QuaxTileGroup.UNASSIGNED_GROUP;


public abstract class QuaxTile {
	
	public static final QuaxTile OUT_OF_BOUNDS_TILE = new PlaceholderTile("out-of-bounds");
	public static final QuaxTile HIDDEN_TILE = new PlaceholderTile("hidden");

	private QuaxTileColour tileColour;
	private QuaxTileGroup tileGroup;

	private StrategyValue strategyValue;
	private final int xPosition;
	private final int yPosition;


	public QuaxTile(int x, int y) {
		this.tileColour = QuaxTileColour.NONE;
		this.tileGroup = UNASSIGNED_GROUP;

		this.strategyValue = StrategyValue.IGNORE;
		this.xPosition = x;
		this.yPosition = y;
	}

	public QuaxTile(QuaxTile t) {
		if (t == null) {
			throw new IllegalArgumentException("QuaxTile cannot be constructed from null copy.");
		}
		
		this.tileColour = t.tileColour;
		this.tileGroup = UNASSIGNED_GROUP; // Don't copy tile group, added in the board after object is constructed

		this.strategyValue = t.strategyValue;
		this.xPosition = t.xPosition;
		this.yPosition = t.yPosition;
	}

	/* Private constructor used exclusively to construct PlaceholderTiles. */
	private QuaxTile() {
		this.xPosition = -1;
		this.yPosition = -1;
	}


	public QuaxTileColour getTileColour() {
		return this.tileColour;
	}

	public QuaxTileGroup getTileGroup() {
		assert tileGroup != null;
		if (tileGroup == UNASSIGNED_GROUP) {
			throw new IllegalStateException("TileGroup not initialised for tile.");
		}
		
		return this.tileGroup;
	}

	public StrategyValue getStrategyValue() {
		assert strategyValue != null;
		
		return this.strategyValue;
	}

	public int getXPosition() {
		return xPosition;
	}

	public int getYPosition() {
		return yPosition;
	}


	public void setTileColour(QuaxTileColour colour) {
		if (colour == null) {
			throw new IllegalArgumentException("QuaxTile cannot be assigned null colour.");
		}
		
		this.tileColour = colour;
	}

	public void setTileGroup(QuaxTileGroup tileGroup) {
		if (tileGroup == null) {
			throw new IllegalArgumentException("QuaxTile cannot be assigned null QuaxTileGroup.");
		}
		
		this.tileGroup = tileGroup;
	}
	
	public void setStrategyValue(StrategyValue value) {
		if (value == null) {
			throw new IllegalArgumentException("QuaxTile cannot be assigned null StrategyValue.");
		}
		
		this.strategyValue = value;
	}


	public abstract QuaxCoordinate getCoordinates();


	/* Following are shorthand boolean checks for brevity */
	public boolean isFree() {
		assert getTileColour() != null;
		
		return getTileColour() == QuaxTileColour.NONE;
	}
	public boolean isOccupied() {
		return !isFree();
	}

	public boolean isBlack() {
		assert getTileColour() != null;
		
		return getTileColour() == QuaxTileColour.BLACK;
	}
	public boolean isWhite() {
		assert getTileColour() != null;
		
		return getTileColour() == QuaxTileColour.WHITE;
	}

	public boolean isSameColour(QuaxTileColour c) {
		assert this.getTileColour() != null;
		
		if (c == null) {
			throw new IllegalArgumentException("null cannot be passed as argument into isSameColour.");
		}
		
		return getTileColour() == c;

	}

	public boolean isOpponentColour(QuaxTileColour c) {
		if (c == null) {
			throw new IllegalArgumentException("null cannot be passed as argument into isOpponentColour.");
		}
		if (c == QuaxTileColour.NONE) {
			throw new IllegalArgumentException("No opponent colour exists for NONE tile colour.");
		}
		
		return getTileColour() == c.flip();
	}


	public final boolean tileExists() {
		return !(this instanceof PlaceholderTile);
	}

	public boolean isLowPriority() {
		return strategyValue.isLowPriority();
	}

	/* Placeholder tiles are used where a tile either exceeds
	 * the bounds of the board (OUT_OF_BOUNDS_TILE) or where
	 * code wishes to intentionally hide a tile from
	 * sight (HIDDEN_TILE). Both of which replace
	 * what else had to have been "null" references
	 * in code for QuaxBoard.neighbours and related methods.
	 */
	private static class PlaceholderTile extends QuaxTile {

		private final String type;


		private PlaceholderTile(String type) {
			super();
			assert type != null;
			this.type = type;
		}


		private UnsupportedOperationException placeholderUsedException() {
			assert type != null;
			return new UnsupportedOperationException("Cannot be invoked on " + type + " tile.");
		}

		@Override
		public QuaxTileColour getTileColour() {
			throw placeholderUsedException();
		}
		@Override
		public QuaxTileGroup getTileGroup() {
			throw placeholderUsedException();
		}


		@Override
		public StrategyValue getStrategyValue() {
			throw placeholderUsedException();
		}

		@Override
		public void setTileColour(QuaxTileColour colour) {
			throw placeholderUsedException();
		}

		@Override
		public void setTileGroup(QuaxTileGroup tileGroup) {
			throw placeholderUsedException();
		}

		@Override
		public void setStrategyValue(StrategyValue value) {
			throw placeholderUsedException();
		}


		@Override
		public QuaxCoordinate getCoordinates() {
			throw placeholderUsedException();
		}


		@Override
		public boolean isFree() {
			throw placeholderUsedException();
		}

		@Override
		public boolean isOccupied() {
			throw placeholderUsedException();
		}

		@Override
		public boolean isBlack() {
			throw placeholderUsedException();
		}

		@Override
		public boolean isWhite() {
			throw placeholderUsedException();
		}

		@Override
		public boolean isSameColour(QuaxTileColour colour) {
			throw placeholderUsedException();
		}

		@Override
		public boolean isOpponentColour(QuaxTileColour colour) {
			throw placeholderUsedException();
		}

		public boolean isLowPriority() {
			throw placeholderUsedException();
		}
	}
}