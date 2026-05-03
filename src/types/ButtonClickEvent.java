package types;

import javafx.event.*;


public class ButtonClickEvent extends Event {

	private static final long serialVersionUID = 1L;

	public static final EventType<ButtonClickEvent> PIE_RULE_CLICKED_EVENT = new EventType<>("pieRuleClickedEvent");
    public static final EventType<ButtonClickEvent> SHOW_STRATEGY_CLICKED_EVENT = new EventType<>("showStrategyClickedEvent");
    public static final EventType<ButtonClickEvent> HIDE_STRATEGY_CLICKED_EVENT = new EventType<>("hideStrategyClickedEvent");

    public ButtonClickEvent(EventType<ButtonClickEvent> t) {
    	if (t == null) {
    		throw new IllegalArgumentException("ButtonClickEvent cannot be constructed with null EventType.");
    	}
    	
        super(t);
    }
}