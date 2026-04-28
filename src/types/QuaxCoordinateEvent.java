package types;

import javafx.event.Event;
import javafx.event.EventType;


public class QuaxCoordinateEvent extends Event {
    public static final EventType<QuaxCoordinateEvent> TILE_CLICKED_EVENT = new EventType<>("tileClickedEvent");

    private QuaxCoordinate coordinate;


    public QuaxCoordinateEvent(EventType<QuaxCoordinateEvent> t, QuaxCoordinate q) {
        super(t);
        this.coordinate = q;
    }


    // TODO - rename methods?
    public int x() {
        return coordinate.x();
    }

    public int y() {
        return coordinate.y();
    }

    public boolean octagon() {
        return coordinate.isOctagon();
    }

    public boolean rhombus() {
        return coordinate.isRhombus();
    }

    public QuaxCoordinate coordinate() {
        return this.coordinate;
    }
}