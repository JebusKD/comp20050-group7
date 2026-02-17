package quax.ui;

import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BoardMaking {
    private GridPane octagonGrid;
    private GridPane rhombusGrid;
    private final double octDiameter = 68;
    private StackPane board;
    public static int Turn;

    public BoardMaking() {
         octagonGrid = new GridPane();
         rhombusGrid = new GridPane();
         Turn = 1;

        makeOctagonalGrid();
        makeRhombusGrid();
        Rectangle rectangle = new  Rectangle(718,780);
        rectangle.setFill(Color.BLACK);
        Rectangle rectangle2 = new Rectangle(780,780);
        rectangle2.setFill(Color.WHITE);
        Rectangle rectangle3 = new Rectangle(800,800);
        rectangle3.setFill(Color.NAVY);

        rhombusGrid.setPickOnBounds(false); // stops the rhombus GridPane blocking the clicks going down to ther octagons

        board = new StackPane(rectangle3,rectangle2,rectangle,octagonGrid, rhombusGrid);
    }

    public StackPane getBoard() {
        return board;
    }

    private void makeOctagonalGrid(){
        octagonGrid.setAlignment(Pos.CENTER);
        octagonGrid.setHgap(1);
        octagonGrid.setVgap(1);



        for(int i = 0; i < 11;i++){
            for(int j = 0; j < 11; j++){
                octagonGrid.add(new Octagon(octDiameter,Color.THISTLE),i,j);
            }
        }
    }


    private void makeRhombusGrid(){
        rhombusGrid.setAlignment(Pos.CENTER);
        final double sideLen = Octagon.calculateSideLength(octDiameter);

        rhombusGrid.setHgap(0.5 + octDiameter - sideLen);
        rhombusGrid.setVgap(0.5 + octDiameter - sideLen);

        for(int i =0;i< 10;i++){
            for(int j =0;j< 10;j++){
                rhombusGrid.add(new Rhombus(sideLen, Color.MEDIUMPURPLE),i,j);
            }
        }
    }
}
