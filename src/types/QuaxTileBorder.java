package types;

public enum QuaxTileBorder {
    NONE {
        public String tileBorderStyle() {
            return "tileoutline-base";
        }
    },

    BLUE {
        public String tileBorderStyle() {
            return "tileoutline-2";
        }
    },

    GREEN {
        public String tileBorderStyle() {
            return "tileoutline-3";
        }
    },

    RED {
        public String tileBorderStyle() {
            return "tileoutline-4";
        }
    },

    CYAN {
        public String tileBorderStyle() {
            return "tileoutline-5";
        }
    },

    PURPLE {
        public String tileBorderStyle() {
            return "tileoutline-6";
        }
    },

    PINK {
        public String tileBorderStyle() {
            return "tileoutline-7";
        }
    };

    public abstract String tileBorderStyle();
}