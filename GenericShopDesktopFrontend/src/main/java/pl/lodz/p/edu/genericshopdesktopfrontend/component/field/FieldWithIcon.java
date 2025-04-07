package pl.lodz.p.edu.genericshopdesktopfrontend.component.field;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.StringProperty;
import javafx.scene.Parent;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public class FieldWithIcon extends HBox implements Field {

    private final FontAwesomeIconView iconView;
    private final Field field;


    public FieldWithIcon(FontAwesomeIcon icon, double iconSize, Field field) {
        this.iconView = new FontAwesomeIconView(icon);
        this.field = requireNonNull(field);

        this.iconView.setGlyphSize(iconSize);

        this.getChildren().addAll(iconView, field.asParent());
        this.getStyleClass().add("field_with_icon");
    }


    @Override
    public void validate() {
        this.field.validate();
    }


    @Override
    public boolean isValid() {
        return this.field.isValid();
    }


    @Override
    public void setAnimation(Consumer<TextInputControl> animation) {
        this.field.setAnimation(animation);
    }


    @Override
    public StringProperty textProperty() {
        return this.field.textProperty();
    }


    @Override
    public void clear() {
        this.field.clear();
    }


    @Override
    public Parent asParent() {
        return this;
    }
}