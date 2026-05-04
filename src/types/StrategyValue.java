package types;

public enum StrategyValue {
	IGNORE,
	VERY_LOW,
	LOW,
	BLOCKING,
	PROGRESS,
	KEY,
	OPPONENT_WINNING,
	WINNING;
	
	public static final int MAX_STRATEGIES = 7;
	
	public int toInt() {
		return this.ordinal();
	}
	
	public static StrategyValue fromInt(int value) {
		return switch(value) {
		case 0 -> IGNORE;
		case 1 -> VERY_LOW;
		case 2 -> LOW;
		case 3 -> BLOCKING;
		case 4 -> PROGRESS;
		case 5 -> KEY;
		case 6 -> OPPONENT_WINNING;
		case 7 -> WINNING;
		default -> throw new IllegalArgumentException("Value " + value + " exceeded strategy value bounds.");
		};
	}
	
	public StrategyValue downgrade(int value) {
		return fromInt(toInt() - value);
	}
	
	public StrategyValue upgrade(int value) {
		return fromInt(toInt() + value);
	}


	public StrategyValue downgradeOne() {
		if (this == IGNORE) {
			throw new IllegalStateException("downgradeOne cannot be called on IGNORE StrategyValue.");
		}
		
		return downgrade(1);
	}
	
	public StrategyValue upgradeOne() {
		return upgrade(1);
	}


	public boolean isHighPriority() {
		return this.compareTo(OPPONENT_WINNING) >= 0;
	}
	
	public boolean isLowPriority() {
		return !isHighPriority();
	}
}
