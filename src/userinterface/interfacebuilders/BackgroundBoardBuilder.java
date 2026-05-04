package userinterface.interfacebuilders;

import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import static types.QuaxCoordinate.*;
import static model.QuaxBoard.*;
import static userinterface.interfacebuilders.UserInterfaceBoard.*;


class BackgroundBoardBuilder {

    static final double OCTAGON_WIDTH = 40;

    private static final double OCTAGON_GRID_GAP = 1;
    private static final double FRONT_HOURGLASS_GAP = 5.7;
    private static final double BACK_HOURGLASS_GAP = 11.4;

    private GridBuilder gridBuilder;


    StackPane initialiseBoard() {
        return new StackPane(
                createGradientBackground(),
                createWhiteCoordinateHourglassBehind(),
                createBlackCoordinateHourglass(),
                createGridBackground(),
                createBoardCoordinates(),
                createGrid());
    }


    private static Rectangle createGradientBackground() {
        double size = OCTAGON_WIDTH * (NUM_OCTAGONS + 1)
                + (NUM_RHOMBUSES * OCTAGON_GRID_GAP);

        Stop[] stops = new Stop[] {
                new Stop(0, Color.NAVY),
                new Stop(1, Color.BLUEVIOLET),
        };

        LinearGradient lgl = new LinearGradient(1,0,1,1,true, CycleMethod.NO_CYCLE,stops);
        Rectangle background = new Rectangle(size, size); //the multicoloured border around the board
        background.setFill(lgl);

        return background;
    }

    private static Rectangle createWhiteCoordinateHourglassBehind() {

        double size = (OCTAGON_WIDTH * BACK_HOURGLASS_GAP)
                + ((BACK_HOURGLASS_GAP - (NUM_OCTAGONS - NUM_RHOMBUSES)*2) * OCTAGON_GRID_GAP)
                + (OCTAGON_WIDTH / 2);

        Rectangle background = new Rectangle(size, size);
        background.setFill(Color.WHITE);

        return background;
    }


    private static Polygon createBlackCoordinateHourglass() {
        double distance = (FRONT_HOURGLASS_GAP * OCTAGON_WIDTH)
                + ((FRONT_HOURGLASS_GAP - (NUM_OCTAGONS - NUM_RHOMBUSES)) * OCTAGON_GRID_GAP)
                + (OCTAGON_WIDTH / 4);

        Polygon hourglass = new Polygon(-distance,distance,
                distance,distance,
                -distance,-distance,
                distance,-distance);
        hourglass.setFill(Color.BLACK);

        return hourglass;
    }

    private static Rectangle createGridBackground() {
        double size = ((NUM_OCTAGONS - 1) * OCTAGON_WIDTH)
                + OctagonBase.calculateSideLength(OCTAGON_WIDTH)
                + (NUM_RHOMBUSES * OCTAGON_GRID_GAP);

        Rectangle background = new Rectangle(size, size);
        background.setFill(Color.OLDLACE);

        return background;
    }


    private static GridPane createBoardCoordinates() {
        CoordinateBuilder coordBuild = new CoordinateBuilder();
        return coordBuild.createCoordinateGrid();
    }

    private static class CoordinateBuilder {

        private static final double GRIDPANE_PADDING = OCTAGON_WIDTH / (NUM_OCTAGONS + 1);

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
        	assert coordinateGrid != null;
        	
            this.coordinateGrid.setAlignment(Pos.CENTER);

            this.coordinateGrid.getColumnConstraints().add(new ColumnConstraints());
            this.coordinateGrid.getColumnConstraints().add(new ColumnConstraints
                                            ((NUM_OCTAGONS*OCTAGON_WIDTH)+(NUM_RHOMBUSES*OCTAGON_GRID_GAP)));
            this.coordinateGrid.getColumnConstraints().add(new ColumnConstraints());

            this.coordinateGrid.getRowConstraints().add(new RowConstraints());
            this.coordinateGrid.getRowConstraints().add(new RowConstraints
                                            ((NUM_OCTAGONS*OCTAGON_WIDTH)+(NUM_RHOMBUSES*OCTAGON_GRID_GAP)));
            this.coordinateGrid.getRowConstraints().add(new RowConstraints());
        }


        private void setTopBottomCoordinateGrid() {
        	assert coordinateGrid != null;
        	
            GridPane top = new GridPane();
            GridPane bottom = new GridPane();

            top.setPadding(new Insets(0, 0, GRIDPANE_PADDING, 0));
            bottom.setPadding(new Insets(GRIDPANE_PADDING, 0, 0, 0));

            top.setHgap(OCTAGON_GRID_GAP);
            top.setAlignment(Pos.CENTER);
            bottom.setHgap(OCTAGON_GRID_GAP);
            bottom.setAlignment(Pos.CENTER);

            for (int i = 0; i < NUM_OCTAGONS; i++) {
                top.getColumnConstraints().add(new ColumnConstraints(OCTAGON_WIDTH));
                bottom.getColumnConstraints().add(new ColumnConstraints(OCTAGON_WIDTH));

                top.add(addLetterCoordinateLabel(i), i, 0);
                bottom.add(addLetterCoordinateLabel(i), i, 0);
            }

            this.coordinateGrid.add(top, 1, 0);
            this.coordinateGrid.add(bottom, 1, 2);
        }

        private static StackPane addLetterCoordinateLabel(int columnNumber) {
        	assert columnNumber >= 0 && columnNumber < NUM_OCTAGONS;
        	
            Label letterCoordinateLabel = new Label(String.valueOf((char) ('A' + columnNumber)));

            letterCoordinateLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
            letterCoordinateLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);
            letterCoordinateLabel.getStyleClass().add("coordinate-letter-style");
            letterCoordinateLabel.setAlignment(Pos.CENTER);

            return new StackPane(letterCoordinateLabel);
        }


        private void setLeftRightCoordinateGrid() {
        	assert coordinateGrid != null;
        	
            GridPane left = new GridPane();
            GridPane right = new GridPane();

            left.setPadding(new Insets(0, GRIDPANE_PADDING, 0, 0));
            right.setPadding(new Insets(0, 0, 0, GRIDPANE_PADDING));

            left.setVgap(OCTAGON_GRID_GAP);
            left.setAlignment(Pos.CENTER);
            right.setVgap(OCTAGON_GRID_GAP);
            right.setAlignment(Pos.CENTER);

            for (int j = 0 ; j < NUM_OCTAGONS; j++) {
                left.getRowConstraints().add(new RowConstraints(OCTAGON_WIDTH));
                right.getRowConstraints().add(new RowConstraints(OCTAGON_WIDTH));

                left.add(addNumberCoordinateLabel(j), 0, j);
                right.add(addNumberCoordinateLabel(j), 0, j);
            }

            this.coordinateGrid.add(left, 0, 1);
            this.coordinateGrid.add(right, 2, 1);
        }

        private static StackPane addNumberCoordinateLabel(int rowNumber) {
        	assert rowNumber >= 0 && rowNumber < NUM_OCTAGONS;
        	
            Label numberCoordinateLabel = new Label(String.valueOf(11 - rowNumber));

            numberCoordinateLabel.getStyleClass().add("coordinate-number-style");
            numberCoordinateLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);
            numberCoordinateLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);

            return new StackPane(numberCoordinateLabel);
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
        	assert buildingOctagonGrid != null && octagonGrid != null;
        	
            buildingOctagonGrid = new OctagonTile[NUM_OCTAGONS][NUM_OCTAGONS];
            octagonGrid = new GridPane();
            positionBoardTileGrid(octagonGrid);

            initialiseOctagonGridRowColumns();
            initialiseOctagonGridCells();

            return octagonGrid;
        }

        private void initialiseOctagonGridRowColumns() {
        	assert octagonGrid != null;
        	
            for (int i = 0; i < NUM_OCTAGONS ; i++) {
                ColumnConstraints column = new ColumnConstraints(OCTAGON_WIDTH);
                octagonGrid.getColumnConstraints().add(column);

                RowConstraints row = new RowConstraints(OCTAGON_WIDTH);
                octagonGrid.getRowConstraints().add(row);
            }
        }

        private void initialiseOctagonGridCells() {
        	assert buildingOctagonGrid != null && octagonGrid != null;
        	
            for (int i = 0; i < NUM_OCTAGONS ; i++) {
                for (int j = 0; j < NUM_OCTAGONS ; j++) {
                    OctagonTile newTile = new OctagonTile(newOctagonCoordinate(i, j));
                    newTile.setId("octagon" + i + "-" + j);

                    buildingOctagonGrid[i][j] = newTile;
                    octagonGrid.add(newTile, i, j);
                }
            }
        }


        private static void positionBoardTileGrid(GridPane boardTileGrid) {
        	assert boardTileGrid != null;
        	
            boardTileGrid.setAlignment(Pos.TOP_LEFT);
            boardTileGrid.setVgap(OCTAGON_GRID_GAP);
            boardTileGrid.setHgap(OCTAGON_GRID_GAP);
            boardTileGrid.setPickOnBounds(false);
        }


        private GridPane createRhombusGrid() {
            buildingRhombusGrid = new RhombusTile[NUM_RHOMBUSES][NUM_RHOMBUSES];
            rhombusGrid = new GridPane();
            positionBoardTileGrid(rhombusGrid);

            rhombusGrid.setPadding(new Insets(calculateRhombusGridGap(), 0, 0, calculateRhombusGridGap()));

            initialiseRhombusGridRowColumns();
            initialiseRhombusGridCells();

            return rhombusGrid;
        }

        private static double calculateRhombusGridGap() {
            double rhombusSideLength = OctagonBase.calculateSideLength(OCTAGON_WIDTH);
            double rhombusDiagonalHeight = (OCTAGON_WIDTH - rhombusSideLength) / 2;
            return rhombusSideLength + rhombusDiagonalHeight + (OCTAGON_GRID_GAP/2);
        }

        private void initialiseRhombusGridRowColumns() {
        	assert rhombusGrid != null;
        	
            for (int i = 0; i < NUM_RHOMBUSES; i++) {
                ColumnConstraints column = new ColumnConstraints(OCTAGON_WIDTH);
                rhombusGrid.getColumnConstraints().add(column);

                RowConstraints row = new RowConstraints(OCTAGON_WIDTH);
                row.setValignment(VPos.TOP);
                rhombusGrid.getRowConstraints().add(row);
            }
        }

        private void initialiseRhombusGridCells() {
        	assert buildingRhombusGrid != null && rhombusGrid != null;
        	
            for (int i = 0; i < NUM_RHOMBUSES; i++) {
                for (int j = 0; j < NUM_RHOMBUSES; j++) {
                    RhombusTile newTile = new RhombusTile(newRhombusCoordinate(i, j));
                    newTile.setId("rhombus" + i + "-" + j);

                    buildingRhombusGrid[i][j] = newTile;
                    rhombusGrid.add(newTile, i, j);
                }
            }
        }


        private OctagonTile[][] getBuiltOctagonGrid() {
        	assert buildingOctagonGrid != null;
            return buildingOctagonGrid;
        }

        private RhombusTile[][] getBuiltRhombusGrid() {
        	assert buildingRhombusGrid != null;
            return buildingRhombusGrid;
        }
    }


    public OctagonTile[][] getOctagonTileGrid() {
    	assert gridBuilder != null;
        return gridBuilder.getBuiltOctagonGrid();
    }

    public RhombusTile[][] getRhombusTileGrid() {
    	assert gridBuilder != null;
        return gridBuilder.getBuiltRhombusGrid();
    }
}
