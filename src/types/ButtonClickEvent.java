package types;

import javafx.event.Event;
import javafx.event.EventType;

public class ButtonClickEvent extends Event {
	public static final EventType<ButtonClickEvent> PIE_RULE_CLICKED_EVENT = new EventType<>("pieRuleClickedEvent");	

	public ButtonClickEvent(EventType<ButtonClickEvent> t) {
		super(t);
	}
}
	