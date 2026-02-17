package quax.ui;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;

public class Octagon extends Polygon{
    private double sideLength;
    private double diameter;

    public Octagon(double diameter, Paint fill) {

        //In Intellij, super has to be called first in constructor, so made a helper to fix that cos it wouldn't compile
        super(calculatePoints(diameter));
        this.sideLength = sideLength;
        this.diameter = diameter;

        this.setFill(fill);

        this.setStrokeWidth(0);

        this.setOnMouseClicked(event -> {
            octagonClicked();

        });

    }

    private void octagonClicked(){

        if(BoardMaking.Turn == 1){
            if(this.getFill().equals(Color.THISTLE)){
                this.setFill(Color.BLACK);
                BoardMaking.Turn = 0;
            }
        }

        else if(BoardMaking.Turn == 0){
            if(this.getFill().equals(Color.THISTLE)){
                this.setFill(Color.WHITE);
                BoardMaking.Turn = 1;
            }

        }

    }

    private static double[] calculatePoints(double diameter){
        double sideLength = diameter / (1 + (2 / Math.sqrt(2)));
        double halfSide = sideLength / 2;
        double radius = diameter / 2;

        return new double[]{
                -halfSide, radius,
                halfSide, radius,
                radius, halfSide,
                radius, -halfSide,
                halfSide, -radius,
                -halfSide, -radius,
                -radius, -halfSide,
                -radius, halfSide

        };

    }

    public double getSideLength() {
        return this.sideLength;
    }

    public static double calculateSideLength(double diameter) {
        return diameter / (1 + (2 / Math.sqrt(2)));
    }
}
