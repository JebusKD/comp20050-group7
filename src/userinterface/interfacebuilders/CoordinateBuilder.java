package userinterface.interfacebuilders;

import javafx.geometry.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import static model.QuaxBoard.*;
import static userinterface.QuaxUserInterface.*;


public class CoordinateBuilder {
    private static final double GRIDPANE_PADDING = OCTAGON_WIDTH / (MAX_OCTAGONS + 1);

    private GridPane coordinateGrid;


    public CoordinateBuilder() {
        coordinateGrid = new GridPane();
        createBoardCoordinates();
    }


    public GridPane getCoordinateGrid() {
        return coordinateGrid;
    }


    private void createBoardCoordinates() {
        setCoordinateGridRowsColumns();
        setTopBottomCoordinateGrid();
        setLeftRightCoordinateGrid();
    }

    private void setCoordinateGridRowsColumns() {
        this.coordinateGrid.setAlignment(Pos.CENTER);

        this.coordinateGrid.getColumnConstraints().add(new ColumnConstraints());
        this.coordinateGrid.getColumnConstraints().add(new ColumnConstraints((MAX_OCTAGONS*OCTAGON_WIDTH)+(MAX_RHOMBUSES*OCTAGON_GRID_GAP)));
        this.coordinateGrid.getColumnConstraints().add(new ColumnConstraints());

        this.coordinateGrid.getRowConstraints().add(new RowConstraints());
        this.coordinateGrid.getRowConstraints().add(new RowConstraints((MAX_OCTAGONS*OCTAGON_WIDTH)+(MAX_RHOMBUSES*OCTAGON_GRID_GAP)));
        this.coordinateGrid.getRowConstraints().add(new RowConstraints());
    }


    private void setTopBottomCoordinateGrid() {
        GridPane top = new GridPane();
        GridPane bottom = new GridPane();

        top.setPadding(new Insets(0, 0, GRIDPANE_PADDING, 0));
        bottom.setPadding(new Insets(GRIDPANE_PADDING, 0, 0, 0));

        top.setHgap(OCTAGON_GRID_GAP);
        top.setAlignment(Pos.CENTER);
        bottom.setHgap(OCTAGON_GRID_GAP);
        bottom.setAlignment(Pos.CENTER);

        for (int i = 0; i < MAX_OCTAGONS; i++){
            top.getColumnConstraints().add(new ColumnConstraints(OCTAGON_WIDTH));
            bottom.getColumnConstraints().add(new ColumnConstraints(OCTAGON_WIDTH));

            top.add(addLetterCoordinateLabel(i), i, 0);
            bottom.add(addLetterCoordinateLabel(i), i, 0);
        }

        this.coordinateGrid.add(top, 1, 0);
        this.coordinateGrid.add(bottom, 1, 2);
    }

    private StackPane addLetterCoordinateLabel(int i) {
        Label letterCLabel = new Label(String.valueOf((char) ('A' + i)));

        letterCLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
        letterCLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);
        letterCLabel.getStyleClass().add("coordinate-letter-style");
        letterCLabel.setAlignment(Pos.CENTER);

        return new StackPane(letterCLabel);
    }


    private void setLeftRightCoordinateGrid() {
        GridPane left = new GridPane();
        GridPane right = new GridPane();

        left.setPadding(new Insets(0, GRIDPANE_PADDING, 0, 0));
        right.setPadding(new Insets(0, 0, 0, GRIDPANE_PADDING));

        left.setVgap(OCTAGON_GRID_GAP);
        left.setAlignment(Pos.CENTER);
        right.setVgap(OCTAGON_GRID_GAP);
        right.setAlignment(Pos.CENTER);

        for (int j = 0 ; j < MAX_OCTAGONS; j++){
            left.getRowConstraints().add(new RowConstraints(OCTAGON_WIDTH));
            right.getRowConstraints().add(new RowConstraints(OCTAGON_WIDTH));

            left.add(addNumberCoordinateLabel(j), 0, j);
            right.add(addNumberCoordinateLabel(j), 0, j);
        }

        this.coordinateGrid.add(left, 0, 1);
        this.coordinateGrid.add(right, 2, 1);
    }

    private StackPane addNumberCoordinateLabel(int j) {
        Label numCLabel = new Label(String.valueOf(11 - j));

        numCLabel.getStyleClass().add("coordinate-number-style");
        numCLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);
        numCLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);

        return new StackPane(numCLabel);
    }
}
