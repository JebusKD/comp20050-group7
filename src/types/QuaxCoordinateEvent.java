package types;

import javafx.event.*;


public class QuaxCoordinateEvent extends Event {

	private static final long serialVersionUID = 1L;

	public static final EventType<QuaxCoordinateEvent> TILE_CLICKED_EVENT = new EventType<>("tileClickedEvent");

    private final QuaxCoordinate coordinate;


    public QuaxCoordinateEvent(EventType<QuaxCoordinateEvent> t, QuaxCoordinate q) {
    	if (t == null) {
    		throw new IllegalArgumentException("QuaxCoordinateEvent cannot be constructed with null EventType.");
    	}
    	if (q == null) {
    		throw new IllegalArgumentException("QuaxCoordinateEvent cannot be constructed with null QuaxCoordiante.");
    	}
    	
        super(t);
        this.coordinate = q;
    }

    public QuaxCoordinate coordinate() {
    	assert coordinate != null;
    	
        return this.coordinate;
    }
}