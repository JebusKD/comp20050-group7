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
		case 0 -> StrategyValue.IGNORE;
		case 1 -> StrategyValue.VERY_LOW;
		case 2 -> StrategyValue.LOW;
		case 3 -> StrategyValue.BLOCKING;
		case 4 -> StrategyValue.PROGRESS;
		case 5 -> StrategyValue.KEY;
		case 6 -> StrategyValue.OPPONENT_WINNING;
		case 7 -> StrategyValue.WINNING;
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
