package userinterface.interfacebuilders;

import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import static types.QuaxCoordinate.*;
import static model.QuaxBoard.*;
import static userinterface.QuaxUserInterface.*;
import static userinterface.interfacebuilders.UserInterfaceBoard.*;


class BackgroundBoardBuilder {

    private static final double FRONT_HOURGLASS_GAP = 5.7;
    private static final double BACK_HOURGLASS_GAP = 11.4;

    private GridBuilder gridBuilder;


    public StackPane initialiseBoard() {
        return new StackPane(
                createGradientBackground(),
                createBehindHourglass(),
                createHourglass(),
                createGridBackground(),
                createBoardCoordinates(),
                createGrid());
    }


    private Rectangle createGradientBackground() {
        double size = OCTAGON_WIDTH * (MAX_OCTAGONS + 1)
                + (MAX_RHOMBUSES * OCTAGON_GRID_GAP);

        Stop[] stops = new Stop[] {
                new Stop(0, Color.NAVY),
                new Stop(1, Color.BLUEVIOLET),
        };

        LinearGradient lgl = new LinearGradient(1,0,1,1,true, CycleMethod.NO_CYCLE,stops);
        Rectangle background = new Rectangle(size, size); //the multicoloured border around the board
        background.setFill(lgl);

        return background;
    }

    private Rectangle createBehindHourglass() {
        double size = (OCTAGON_WIDTH * BACK_HOURGLASS_GAP)
                + ((BACK_HOURGLASS_GAP - (MAX_OCTAGONS - MAX_RHOMBUSES)*2) * OCTAGON_GRID_GAP)
                + (OCTAGON_WIDTH / 2);

        Rectangle background = new Rectangle(size, size);
        background.setFill(Color.WHITE);

        return background;
    }

    private Polygon createHourglass() {
        double distance = (FRONT_HOURGLASS_GAP * OCTAGON_WIDTH)
                + ((FRONT_HOURGLASS_GAP - (MAX_OCTAGONS - MAX_RHOMBUSES)) * OCTAGON_GRID_GAP)
                + (OCTAGON_WIDTH / 4);

        Polygon hourglass = new Polygon(-distance,distance,
                distance,distance,
                -distance,-distance,
                distance,-distance);
        hourglass.setFill(Color.BLACK);

        return hourglass;
    }

    private Rectangle createGridBackground() {
        double size = ((MAX_OCTAGONS - 1) * OCTAGON_WIDTH)
                + OctagonBase.calculateSideLength(OCTAGON_WIDTH)
                + (MAX_RHOMBUSES * OCTAGON_GRID_GAP);

        Rectangle background = new Rectangle(size, size);
        background.setFill(Color.OLDLACE);

        return background;
    }


    private GridPane createBoardCoordinates() {
        CoordinateBuilder coordBuild = new CoordinateBuilder();
        return coordBuild.createCoordinateGrid();
    }

    private static class CoordinateBuilder {

        private static final double GRIDPANE_PADDING = OCTAGON_WIDTH / (MAX_OCTAGONS + 1);

        private GridPane coordinateGrid;


        private GridPane createCoordinateGrid() {
            coordinateGrid = new GridPane();
            createBoardCoordinates();
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
            this.coordinateGrid.getColumnConstraints().add(new ColumnConstraints
                                            ((MAX_OCTAGONS*OCTAGON_WIDTH)+(MAX_RHOMBUSES*OCTAGON_GRID_GAP)));
            this.coordinateGrid.getColumnConstraints().add(new ColumnConstraints());

            this.coordinateGrid.getRowConstraints().add(new RowConstraints());
            this.coordinateGrid.getRowConstraints().add(new RowConstraints
                                            ((MAX_OCTAGONS*OCTAGON_WIDTH)+(MAX_RHOMBUSES*OCTAGON_GRID_GAP)));
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

            for (int i = 0; i < MAX_OCTAGONS; i++) {
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

            for (int j = 0 ; j < MAX_OCTAGONS; j++) {
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


    private StackPane createGrid() {
        gridBuilder = new GridBuilder();
        StackPane gridStack = new StackPane(
                gridBuilder.createOctagonGrid(),
                gridBuilder.createRhombusGrid()
        );

        gridStack.setMaxHeight(Region.USE_PREF_SIZE);
        gridStack.setMaxWidth(Region.USE_PREF_SIZE);

        return gridStack;
    }

    private static class GridBuilder {

        private GridPane octagonGrid;
        private GridPane rhombusGrid;

        private OctagonTile[][] buildingOctagonGrid;
        private RhombusTile[][] buildingRhombusGrid;


        private GridPane createOctagonGrid() {
            buildingOctagonGrid = new OctagonTile[MAX_OCTAGONS][MAX_OCTAGONS];
            octagonGrid = new GridPane();
            positionBoardTileGrid(octagonGrid);

            initialiseOctagonGridRowColumns();
            initialiseOctagonGridCells();

            return octagonGrid;
        }

        private void initialiseOctagonGridRowColumns() {
            for (int i = 0; i < MAX_OCTAGONS ; i++) {
                ColumnConstraints column = new ColumnConstraints(OCTAGON_WIDTH);
                octagonGrid.getColumnConstraints().add(column);

                RowConstraints row = new RowConstraints(OCTAGON_WIDTH);
                octagonGrid.getRowConstraints().add(row);
            }
        }

        private void initialiseOctagonGridCells() {
            for (int i = 0; i < MAX_OCTAGONS ; i++) {
                for (int j = 0; j < MAX_OCTAGONS ; j++) {
                    OctagonTile newTile = new OctagonTile(newOctagonCoordinate(i, j));
                    newTile.setId("octagon" + i + "-" + j);

                    buildingOctagonGrid[i][j] = newTile;
                    octagonGrid.add(newTile, i, j);
                }
            }
        }


        private static void positionBoardTileGrid(GridPane boardTileGrid) {
            boardTileGrid.setAlignment(Pos.TOP_LEFT);
            boardTileGrid.setVgap(OCTAGON_GRID_GAP);
            boardTileGrid.setHgap(OCTAGON_GRID_GAP);
            boardTileGrid.setPickOnBounds(false);
        }


        private GridPane createRhombusGrid() {
            buildingRhombusGrid = new RhombusTile[MAX_RHOMBUSES][MAX_RHOMBUSES];
            rhombusGrid = new GridPane();
            positionBoardTileGrid(rhombusGrid);

            rhombusGrid.setPadding(new Insets(calculateRhombusGridGap(), 0, 0, calculateRhombusGridGap()));

            initialiseRhombusGridRowColumns();
            initialiseRhombusGridCells();

            return rhombusGrid;
        }

        private double calculateRhombusGridGap() {
            double rhombusSideLength = OctagonBase.calculateSideLength(OCTAGON_WIDTH);
            double rhombusDiagonalHeight = (OCTAGON_WIDTH - rhombusSideLength) / 2;
            return rhombusSideLength + rhombusDiagonalHeight + (OCTAGON_GRID_GAP/2);
        }

        private void initialiseRhombusGridRowColumns() {
            for (int i = 0; i < MAX_RHOMBUSES; i++) {
                ColumnConstraints column = new ColumnConstraints(OCTAGON_WIDTH);
                rhombusGrid.getColumnConstraints().add(column);

                RowConstraints row = new RowConstraints(OCTAGON_WIDTH);
                row.setValignment(VPos.TOP);
                rhombusGrid.getRowConstraints().add(row);
            }
        }

        private void initialiseRhombusGridCells() {
            for (int i = 0; i < MAX_RHOMBUSES; i++) {
                for (int j = 0; j < MAX_RHOMBUSES; j++) {
                    RhombusTile newTile = new RhombusTile(newRhombusCoordinate(i, j));
                    newTile.setId("rhombus" + i + "-" + j);

                    buildingRhombusGrid[i][j] = newTile;
                    rhombusGrid.add(newTile, i, j);
                }
            }
        }


        private OctagonTile[][] getBuiltOctagonGrid() {
            return buildingOctagonGrid;
        }

        private RhombusTile[][] getBuiltRhombusGrid() {
            return buildingRhombusGrid;
        }
    }


    public OctagonTile[][] getOctagonTileGrid() {
        return gridBuilder.getBuiltOctagonGrid();
    }

    public RhombusTile[][] getRhombusTileGrid() {
        return gridBuilder.getBuiltRhombusGrid();
    }
}
