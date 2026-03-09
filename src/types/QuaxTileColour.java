package types;

public enum QuaxTileColour {
	NONE {
		@Override
		public QuaxTileColour flip() {
			return NONE;
		}
		
		@Override
		public String tilecolourStyle() {
			return "tilecolour-none";
		}
	},
	BLACK {
		@Override
		public QuaxTileColour flip() {
			return WHITE;
		}
		
		@Override
		public String tilecolourStyle() {
			return "tilecolour-black";
		}
	},
	WHITE {
		@Override
		public QuaxTileColour flip() {
			return BLACK;
		}
		
		@Override
		public String tilecolourStyle() {
			return "tilecolour-white";
		}
	};
	public abstract QuaxTileColour flip();
	public abstract String tilecolourStyle();
}
