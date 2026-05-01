package types;


public abstract class QuaxTile {
	
	public static final QuaxTile OUT_OF_BOUNDS_TILE = new OutOfBoundsTile();

	private QuaxTileColour tileColour;
	private QuaxTileGroup tileGroup;

	private int strategyValue;

	protected final int xPosition;
	protected final int yPosition;


	public QuaxTile(int x, int y) {
		this.tileColour = QuaxTileColour.NONE;
		this.tileGroup = null;
		this.strategyValue = 0;
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

	public int getStrategyValue() {
		return this.strategyValue;
	}


	public void setTileColour(QuaxTileColour colour) {
		assert colour != null;
		this.tileColour = colour;
	}

	public void setTileGroup(QuaxTileGroup tileGroup) {
		this.tileGroup = tileGroup;
	}

	public void setStrategyValue(int value) {
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
	public boolean isSameColour(QuaxTileColour c) {
		// TODO Any instance where NONE is used will return false, --> Added assertion
		// I'd suggest an assertion
		assert c == QuaxTileColour.BLACK || c == QuaxTileColour.WHITE;
		return getTileColour() == c;
	}

	// TODO - Will this be used?
	public boolean isOpponentColour(QuaxTileColour c) {
		assert c != QuaxTileColour.NONE;
		return getTileColour() == c.flip();
	}
	
	public final boolean isOutOfBounds() {
		return this == OUT_OF_BOUNDS_TILE;
	}
	
	public final boolean isInBounds() {
		return !isOutOfBounds();
	}
	
	private static class OutOfBoundsTile extends QuaxTile {
		private OutOfBoundsTile() {
			super();
		}

		@Override
		public QuaxTileColour getTileColour() {
			throw new UnsupportedOperationException("Cannot be invoked on out-of-bounds tile.");
		}
		@Override
		public QuaxTileGroup getTileGroup() {
			throw new UnsupportedOperationException("Cannot be invoked on out-of-bounds tile.");
		}
		@Override
		public int getStrategyValue() {
			throw new UnsupportedOperationException("Cannot be invoked on out-of-bounds tile.");
		}
		@Override
		public void setTileColour(QuaxTileColour colour) {
			throw new UnsupportedOperationException("Cannot be invoked on out-of-bounds tile.");
		}
		@Override
		public void setTileGroup(QuaxTileGroup tileGroup) {
			throw new UnsupportedOperationException("Cannot be invoked on out-of-bounds tile.");
		}
		@Override
		public void setStrategyValue(int value) {
			throw new UnsupportedOperationException("Cannot be invoked on out-of-bounds tile.");
		}
		@Override
		public QuaxCoordinate getCoordinates() {
			throw new UnsupportedOperationException("Cannot be invoked on out-of-bounds tile.");
		}
		@Override
		public boolean isFree() {
			throw new UnsupportedOperationException("Cannot be invoked on out-of-bounds tile.");
		}
		@Override
		public boolean isOccupied() {
			throw new UnsupportedOperationException("Cannot be invoked on out-of-bounds tile.");
		}
		@Override
		public boolean isBlack() {
			throw new UnsupportedOperationException("Cannot be invoked on out-of-bounds tile.");
		}
		@Override
		public boolean isWhite() {
			throw new UnsupportedOperationException("Cannot be invoked on out-of-bounds tile.");
		}
		@Override
		public boolean isSameColour(QuaxTileColour c) {
			throw new UnsupportedOperationException("Cannot be invoked on out-of-bounds tile.");
		}
		@Override
		public boolean isOpponentColour(QuaxTileColour c) {
			throw new UnsupportedOperationException("Cannot be invoked on out-of-bounds tile.");
		}
		
	}

}