package userinterface.interfacebuilders;

import javafx.scene.shape.Polygon;
import static userinterface.interfacebuilders.BackgroundBoardBuilder.OCTAGON_WIDTH;


abstract class RhombusBase extends Polygon {

    public RhombusBase() {
        this((OCTAGON_WIDTH - OctagonBase.SIDELENGTH) / 2);
    }

    public RhombusBase(double radius) {
        super(-radius, 0,
                0, radius,
                radius, 0,
                0, -radius);

        if (radius <= 0) {
            throw new IllegalArgumentException("Invalid polygon length received");
        }
    }
}
