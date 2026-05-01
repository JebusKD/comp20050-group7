package userinterface;

import javafx.css.Styleable;

public interface SimpleStyleable extends Styleable {
	default void addStyleClass(String e) {
		this.getStyleClass().add(e);
	}
	
	default void removeStyleClass(String e) {
		this.getStyleClass().remove(e);
	}
	
	default void addAllStyleClasses(String... elements) {
		this.getStyleClass().addAll(elements);
	}
	
	default void removeAllStyleClasses(String... elements) {
		this.getStyleClass().removeAll(elements);
	}
}
