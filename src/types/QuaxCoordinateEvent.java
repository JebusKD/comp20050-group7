package types;

import javafx.event.*;


public class QuaxCoordinateEvent extends Event {

	private static final long serialVersionUID = 1L;

	public static final EventType<QuaxCoordinateEvent> TILE_CLICKED_EVENT = new EventType<>("tileClickedEvent");

    private final QuaxCoordinate coordinate;


    public QuaxCoordinateEvent(EventType<QuaxCoordinateEvent> clickedTile, QuaxCoordinate coord) {
    	if (clickedTile == null) {
    		throw new IllegalArgumentException("QuaxCoordinateEvent cannot be constructed with null EventType.");
    	}
    	if (coord == null) {
    		throw new IllegalArgumentException("QuaxCoordinateEvent cannot be constructed with null QuaxCoordiante.");
    	}

        super(clickedTile);
        this.coordinate = coord;
    }

    public QuaxCoordinate coordinate() {
    	assert coordinate != null;

        return this.coordinate;
    }
}