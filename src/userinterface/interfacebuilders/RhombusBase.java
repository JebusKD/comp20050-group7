package userinterface.interfacebuilders;

import javafx.scene.shape.Polygon;
import userinterface.SimpleStyleable;

import static userinterface.QuaxUserInterface.OCTAGON_WIDTH;


abstract class RhombusBase extends Polygon implements SimpleStyleable {

    public RhombusBase() {
        this((OCTAGON_WIDTH - OctagonBase.SIDELENGTH) / 2);
    }

    public RhombusBase(double radius) {
        super(-radius, 0,
                0, radius,
                radius, 0,
                0, -radius);
    }
}
