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
	
	public boolean matchesOrIsNull(QuaxTileColour other) {
		return other == null || other == this;
	}
	
	public boolean matchesOrIsNull(QuaxTile other) {
		return other == null || matchesOrIsNull(other.getColour());
	}
	
	public boolean matchesFlipped(QuaxTileColour other) {
		return other != null && other == this.flip();
	}
	
	public boolean matchesFlipped(QuaxTile other) {
		return other != null && matchesFlipped(other.getColour());
	}
}
