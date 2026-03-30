package types;

import javafx.event.Event;
import javafx.event.EventType;

public class ButtonClickEvent extends Event {

	private static final long serialVersionUID = 1L;
	
	public ButtonClickEvent(EventType<ButtonClickEvent> t) {
		super(t);
	}
	
}
	