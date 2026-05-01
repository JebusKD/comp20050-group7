package userinterface.interfacebuilders;

import javafx.scene.shape.Polygon;

import static userinterface.QuaxUserInterface.OCTAGON_WIDTH;


abstract class OctagonBase extends Polygon {

    public static final double SIDELENGTH = calculateSideLength(OCTAGON_WIDTH);
    private static final double[] POINTS = generatePolygonPoints(OCTAGON_WIDTH);


    public OctagonBase() {
        super(POINTS);
    }

    public OctagonBase(double width) {
        super(generatePolygonPoints(width));
    }


    public static double calculateSideLength(double width) {
        return width / (1 + (2 / Math.sqrt(2)));
    }

    private static double[] generatePolygonPoints(double width) {
        double sideLength = calculateSideLength(width);
        double halfSide = sideLength / 2;
        double radius = width / 2;

        return new double[] { -halfSide, radius,
                halfSide, radius,
                radius, halfSide,
                radius, -halfSide,
                halfSide, -radius,
                -halfSide, -radius,
                -radius, -halfSide,
                -radius, halfSide
        };
    }
}
