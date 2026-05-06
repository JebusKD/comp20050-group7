package player.bothelpers;

import java.util.*;

import static controller.QuaxController.RNG;
import types.StrategyValue;


class StrategyValueProbabilities {
	private StrategyValueProbabilities() {
	}
	
	/* Cumulative probability of a strategy value being played
	 	with probabilities from 0 to 1.
	 */
	private static final TreeMap<Double, StrategyValue> STRATEGY_PROBABILITY_MAP =
			new TreeMap<>(Map.ofEntries(
						Map.entry(0.005, StrategyValue.VERY_LOW),
						Map.entry(0.07, StrategyValue.LOW),
						Map.entry(0.3, StrategyValue.BLOCKING),
						Map.entry(1.0, StrategyValue.PROGRESS)
					));
	
	public static StrategyValue randomStrategyValue() {
		assert STRATEGY_PROBABILITY_MAP.ceilingEntry(1.0) != null;
		
		return STRATEGY_PROBABILITY_MAP.ceilingEntry(RNG.nextDouble()).getValue();
	}
}
