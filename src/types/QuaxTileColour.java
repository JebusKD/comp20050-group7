package types;


public enum QuaxTileColour {
	NONE {
		public QuaxTileColour flip() {
			return NONE;
		}

		public String tilecolourStyle() {
			return "tilecolour-none";
		}
	},

	BLACK {
		public QuaxTileColour flip() {
			return WHITE;
		}

		public String tilecolourStyle() {
			return "tilecolour-black";
		}
	},

	WHITE {
		public QuaxTileColour flip() {
			return BLACK;
		}

		public String tilecolourStyle() {
			return "tilecolour-white";
		}
	};

	public abstract QuaxTileColour flip();
	public abstract String tilecolourStyle();

    public boolean isPlayerColour() {
        return this == BLACK || this == WHITE;
    }
}
