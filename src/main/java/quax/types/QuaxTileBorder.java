package quax.types;

public enum QuaxTileBorder {
    NONE{
        public String tileborderStyle() {
            return "tileoutline-base";
        }
    },
    RED{
        public String tileborderStyle() {
            return "tileoutline-0";
        }
    },
    GREEN{
        public String tileborderStyle() {
            return "tileoutline-2";
        }
    },
    PURPLE{
        public String tileborderStyle() {
            return "tileoutline-3";
        }
    },
    BLUE{
        public String tileborderStyle() {
            return "tileoutline-1";
        }
    };
    public abstract String tileborderStyle();
}

