package quax.ui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Paint;

public class Rhombus extends Rectangle {
    public Rhombus(double side, Paint fill) {
        super(side, side);

        this.setFill(fill);
        this.setRotate(45.0);
        this.setStrokeWidth(0);

        this.setOnMouseClicked(event -> {
            rhombusClicked();

        });
    }

    private void rhombusClicked(){
        if(this.getFill().equals(Color.MEDIUMPURPLE)){
            this.setFill(Color.BLACK);
        }
        else if(this.getFill().equals(Color.BLACK)){
            this.setFill(Color.WHITE);
        }
        else if(this.getFill().equals(Color.WHITE)){
            this.setFill(Color.BLACK);
        }

    }
}
