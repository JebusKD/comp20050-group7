package types;

import javafx.event.Event;
import javafx.event.EventType;

public class QuaxCoordinateEvent extends Event {
	public static final EventType<QuaxCoordinateEvent> TILE_CLICKED_EVENT = new EventType<>("tileClickedEvent");
	
	private static final long serialVersionUID = 1L;

	private QuaxCoordinate coord;
	
	public QuaxCoordinateEvent(EventType<QuaxCoordinateEvent> t, QuaxCoordinate q) {
		super(t);
		this.coord = q;
	}
	
	public int x() {
		return coord.x();
	}
	
	public int y() {
		return coord.y();
	}
	
	public boolean octagon() {
		return coord.isOctagonMove();
	}
	
	public boolean rhombus() {
		return coord.isRhombusMove();
	}
	
	public QuaxCoordinate coords() {
		return this.coord;
	}
}
