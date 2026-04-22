package types;

import javafx.event.Event;
import javafx.event.EventType;

public class QuaxCoordinateEvent extends Event {

	public static final EventType<QuaxCoordinateEvent> TILE_CLICKED_EVENT = new EventType<>("tileClickedEvent");

	private QuaxCoordinate eventCoordinate;


	public QuaxCoordinateEvent(EventType<QuaxCoordinateEvent> t, QuaxCoordinate q) {
		super(t);
		this.eventCoordinate = q;
	}
	
	public int x() {
		return eventCoordinate.x();
	}
	
	public int y() {
		return eventCoordinate.y();
	}

	public QuaxCoordinate coords() {
		return this.eventCoordinate;
	}
}
