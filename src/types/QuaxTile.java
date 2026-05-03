package types;


public abstract class QuaxTile {
	
	public static final QuaxTile OUT_OF_BOUNDS_TILE = new PlaceholderTile("out-of-bounds");
	public static final QuaxTile HIDDEN_TILE = new PlaceholderTile("hidden");

	private QuaxTileColour tileColour;
	private QuaxTileGroup tileGroup;

	private StrategyValue strategyValue;
	// TODO should these be private and accessed downstream via a getXPosition()/YPosition()?
	protected final int xPosition;
	protected final int yPosition;


	public QuaxTile(int x, int y) {
		this.tileColour = QuaxTileColour.NONE;
		this.tileGroup = null;

		this.strategyValue = StrategyValue.IGNORE;
		this.xPosition = x;
		this.yPosition = y;
	}

	public QuaxTile(QuaxTile t) {
		this.tileColour = t.tileColour;
		this.tileGroup = null; // Don't copy tile group, added in the board after object is constructed

		this.strategyValue = t.strategyValue;
		this.xPosition = t.xPosition;
		this.yPosition = t.yPosition;
	}

	/* Private constructor used exclusively to construct OutOfBoundsTile. */
	private QuaxTile() {
		this.xPosition = -1;
		this.yPosition = -1;
	}


	public QuaxTileColour getTileColour() {
		return this.tileColour;
	}

	public QuaxTileGroup getTileGroup() {
		return this.tileGroup;
	}

	public StrategyValue getStrategyValue() {
		return this.strategyValue;
	}


	public void setTileColour(QuaxTileColour colour) {
		assert colour != null;
		this.tileColour = colour;
	}

	public void setTileGroup(QuaxTileGroup tileGroup) {
		this.tileGroup = tileGroup;
	}
	
	public void setStrategyValue(StrategyValue value) {
		this.strategyValue = value;
	}


	public abstract QuaxCoordinate getCoordinates();


	/*
	 * Shorthand boolean checks for brevity
	 */
	public boolean isFree() {
		return getTileColour() == QuaxTileColour.NONE;
	}
	public boolean isOccupied() {
		return !isFree();
	}

	public boolean isBlack() {
		return getTileColour() == QuaxTileColour.BLACK;
	}
	public boolean isWhite() {
		return getTileColour() == QuaxTileColour.WHITE;
	}

	public boolean isSameColour(QuaxTileColour colour) {
		assert colour == QuaxTileColour.BLACK || colour == QuaxTileColour.WHITE;
		return getTileColour() == colour;
	}

	public boolean isOpponentColour(QuaxTileColour colour) {
		assert colour != QuaxTileColour.NONE;
		return getTileColour() == colour.flip();
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
			this.type = type;
		}


		private UnsupportedOperationException placeholderUsedException() {
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