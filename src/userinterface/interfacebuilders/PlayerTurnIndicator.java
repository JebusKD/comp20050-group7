package userinterface.interfacebuilders;

import javafx.css.Styleable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import types.QuaxTileColour;
import static userinterface.interfacebuilders.BackgroundBoardBuilder.OCTAGON_WIDTH;


public class PlayerTurnIndicator {

    private static final double HBOX_SPACING = 5;

    private final HBox turnTrackerBox;

    private OctagonTurnIndicator octagonIndicator;
    private RhombusTurnIndicator rhombusIndicator;
    private TurnText turnText;


    public PlayerTurnIndicator() {
        this.turnTrackerBox = initialiseTurnTracker();
        this.setIndicatorColour(QuaxTileColour.BLACK);
    }


    private HBox initialiseTurnTracker() {
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
        if (colour != QuaxTileColour.BLACK && colour != QuaxTileColour.WHITE) {
            throw new IllegalStateException("Indicator can only be BLACK or WHITE");
        }

        this.octagonIndicator.setTurnTileColour(colour);
        this.rhombusIndicator.setTurnTileColour(colour);
        this.turnText.setTurnColour(colour);
    }


    public HBox getTurnTrackerBox() {
        return this.turnTrackerBox;
    }

    public void hideTurnTrackerBox() {
        this.turnTrackerBox.setVisible(false);
    }


    private interface TurnIndicatorShape extends Styleable {

        default void setTurnTileColour(QuaxTileColour colour) {
            assert colour.isPlayerColour();

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

            if (width <= 0) {
                throw new IllegalArgumentException("Invalid Octagon Turn Indicator length");
            }

            this.setId("Octagon-Turn-Indicator");
            this.getStyleClass().add("turn-indicator-shape");
            this.setTurnTileColour(QuaxTileColour.BLACK);
        }
    }


    private static class RhombusTurnIndicator extends RhombusBase implements TurnIndicatorShape {

        public RhombusTurnIndicator() {
            super();
            this.setId("Rhombus-Turn-Indicator");
            this.getStyleClass().add("turn-indicator-shape");
            this.setTurnTileColour(QuaxTileColour.BLACK);
        }
    }


    private static class TurnText extends Label implements Styleable {
        public TurnText() {
            super();
            this.getStyleClass().add("turn-label");
            this.setId("Turn-text");
        }

        public void setTurnColour(QuaxTileColour colour) {
            assert colour.isPlayerColour();
            this.setText(colour + " to play");
        }
    }
}
