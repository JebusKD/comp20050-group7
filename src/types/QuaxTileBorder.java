package types;


public enum QuaxTileBorder {
    NONE {
        public String tileBorderStyle() {
            return "tileoutline-base";
        }
    },

    RED {
        public String tileBorderStyle() {
            return "tileoutline-0";
        }
    },

    BLUE {
        public String tileBorderStyle() {
            return "tileoutline-1";
        }
    },

    GREEN {
        public String tileBorderStyle() {
            return "tileoutline-2";
        }
    },

    PURPLE {
        public String tileBorderStyle() {
            return "tileoutline-3";
        }
    },

    PINK {
        public String tileBorderStyle() {
            return "tileoutline-4";
        }
    },
    // TODO - Reorder based on SV correlation (SV1,2,3,4,5,6,7)
    CYAN {
    	public String tileBorderStyle() {
    		return "tileoutline-5";
    	}
    };

    public abstract String tileBorderStyle();
}