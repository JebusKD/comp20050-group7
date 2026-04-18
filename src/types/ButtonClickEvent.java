package types;

import javafx.event.Event;
import javafx.event.EventType;

public class ButtonClickEvent extends Event {
	public static final EventType<ButtonClickEvent> PIE_RULE_CLICKED_EVENT = new EventType<>("pieRuleClickedEvent");	

	// TODO - Remove unused constant
	//private static final long serialVersionUID = 1L;
	
	public ButtonClickEvent(EventType<ButtonClickEvent> t) {
		super(t);
	}
}
	