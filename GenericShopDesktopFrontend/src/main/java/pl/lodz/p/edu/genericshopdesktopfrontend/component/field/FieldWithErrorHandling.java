package pl.lodz.p.edu.genericshopdesktopfrontend.component.field;

import javafx.beans.property.StringProperty;
import javafx.scene.Parent;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class FieldWithErrorHandling extends VBox implements Field {

    private final TextInputControl textField;
    private final Text errorText;
    private final Pattern validationPattern;
    private Consumer<TextInputControl> animation = node -> {};


    public FieldWithErrorHandling(String promptText, String errorMessage, Pattern validationPattern, TextInputControl control) {
        this.textField = requireNonNull(control);
        this.errorText = new Text(errorMessage);
        this.validationPattern = requireNonNull(validationPattern);

        textField.setPromptText(promptText);
        textField.getStyleClass().add("field");

        errorText.getStyleClass().add("field_error");
        errorText.setVisible(false);

        this.getChildren().addAll(textField, errorText);
        this.getStyleClass().add("field_with_error_handling");

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> validate());
    }


    @Override
    public void validate() {
        errorText.setVisible(!isValid());

        if (!isValid()) {
            animation.accept(textField);
        }
    }


    @Override
    public boolean isValid() {
        return validationPattern.matcher(textField.getText()).matches();
    }


    @Override
    public void setAnimation(Consumer<TextInputControl> animation) {
        if (nonNull(animation)) {
            this.animation = animation;
        }
    }


    @Override
    public StringProperty textProperty() {
        return textField.textProperty();
    }


    @Override
    public void clear() {
        textField.clear();
    }


    @Override
    public Parent asParent() {
        return this;
    }
}