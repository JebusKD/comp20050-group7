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
    };

    public abstract String tileBorderStyle();
}