package userinterface.interfacebuilders;

import javafx.css.Styleable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import types.QuaxTileColour;

import static userinterface.QuaxUserInterface.OCTAGON_WIDTH;


public class PlayerTurnIndicator {

    private static final double HBOX_SPACING = 5;

    private HBox turnTrackerBox;

    private OctagonTurnIndicator octagonIndicator;
    private RhombusTurnIndicator rhombusIndicator;
    private TurnText turnText;


    public PlayerTurnIndicator() {
        this.turnTrackerBox = createTurnTracker();
        this.setIndicatorColour(QuaxTileColour.BLACK);
    }


    private HBox createTurnTracker() {
        HBox box = new HBox(HBOX_SPACING);
        createComponents();
        box.getStyleClass().add("hbox-custom");
        box.getChildren().addAll(this.turnText, this.octagonIndicator, this.rhombusIndicator);
        return box;
    }

    private void createComponents() {
        this.octagonIndicator = new OctagonTurnIndicator();
        this.rhombusIndicator = new RhombusTurnIndicator();
        this.turnText = new TurnText();
    }

    public void setIndicatorColour(QuaxTileColour colour) {
        this.octagonIndicator.setTurnTileColour(colour);
        this.rhombusIndicator.setTurnTileColour(colour);
        this.turnText.setTurnColour(colour);
    }

    public HBox getTurnTrackerBox() {
        return this.turnTrackerBox;
    }

    public void hideTurnTrackerBox() {
        turnTrackerBox.setVisible(false);
    }

    private interface TurnIndicatorShape extends Styleable {
        default void setTurnTileColour(QuaxTileColour colour) {
            assert colour == QuaxTileColour.BLACK || colour == QuaxTileColour.WHITE;

            this.getStyleClass().removeAll(QuaxTileColour.BLACK.tilecolourStyle(),
                                            QuaxTileColour.WHITE.tilecolourStyle());
            this.getStyleClass().add(colour.tilecolourStyle());
        }
    }


    private static class OctagonTurnIndicator extends OctagonBase implements TurnIndicatorShape {

        public OctagonTurnIndicator() {
            this(OCTAGON_WIDTH);
        }

        public OctagonTurnIndicator(double width) {
            super(width);
            this.setId("Octagon-object"); // TODO Change this ID - also needs to be done in UI test
            this.getStyleClass().add("turn-indicator-shape");
            this.setTurnTileColour(QuaxTileColour.BLACK);
        }
    }


    private static class RhombusTurnIndicator extends RhombusBase implements TurnIndicatorShape {

        public RhombusTurnIndicator() {
            super();
            this.setId("Rhombus-object"); // TODO Change this ID - also needs to be done in UI test
            this.getStyleClass().add("turn-indicator-shape");
        }
    }


    private static class TurnText extends Label {
        public TurnText() {
            super();
            this.getStyleClass().add("turn-label");
            this.setId("Turn-text");
        }

        public void setTurnColour(QuaxTileColour colour) {
            assert colour == QuaxTileColour.BLACK || colour == QuaxTileColour.WHITE;
            this.setText(colour + " to play");
        }
    }
}
