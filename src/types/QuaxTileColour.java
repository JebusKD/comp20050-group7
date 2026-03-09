package types;

public enum QuaxTileColour {
	NONE {
		@Override
		public QuaxTileColour flip() {
			return NONE;
		}
	},
	BLACK {
		@Override
		public QuaxTileColour flip() {
			return WHITE;
		}
	},
	WHITE {
		@Override
		public QuaxTileColour flip() {
			return BLACK;
		}
	};
	public abstract QuaxTileColour flip();
}
