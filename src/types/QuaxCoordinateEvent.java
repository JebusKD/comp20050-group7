package types;

import javafx.event.*;


public class QuaxCoordinateEvent extends Event {

    public static final EventType<QuaxCoordinateEvent> TILE_CLICKED_EVENT = new EventType<>("tileClickedEvent");

    private final QuaxCoordinate coordinate;


    public QuaxCoordinateEvent(EventType<QuaxCoordinateEvent> t, QuaxCoordinate q) {
        super(t);
        this.coordinate = q;
    }

    public QuaxCoordinate coordinate() {
        return this.coordinate;
    }
}