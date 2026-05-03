package player.bothelpers;

import java.util.Map;
import java.util.TreeMap;

import static controller.QuaxController.RNG;
import types.StrategyValue;

public class StrategyValueProbabilities {
	private StrategyValueProbabilities() {
	}
	
	/* Cumulative probability of a strategy value being played
	 	with probabilities from 0 to 1.
	 */
	private static final TreeMap<Double, StrategyValue> STRATEGY_PROBABILITY_MAP =
			new TreeMap<>(Map.ofEntries(
						Map.entry(0.01, StrategyValue.VERY_LOW),
						Map.entry(0.15, StrategyValue.LOW),
						Map.entry(0.4, StrategyValue.BLOCKING),
						Map.entry(1.0, StrategyValue.PROGRESS)
					));
	
	public static StrategyValue randomStrategyValue() {
		return STRATEGY_PROBABILITY_MAP.ceilingEntry(RNG.nextDouble()).getValue();
	}

}
