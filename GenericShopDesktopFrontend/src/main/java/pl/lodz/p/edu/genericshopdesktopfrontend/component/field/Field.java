package pl.lodz.p.edu.genericshopdesktopfrontend.component.field;

import javafx.beans.property.StringProperty;
import javafx.scene.Parent;
import javafx.scene.control.TextInputControl;

import java.util.function.Consumer;

public interface Field {

    void validate();

    boolean isValid();

    void setAnimation(Consumer<TextInputControl> animation);

    StringProperty textProperty();

    void clear();

    Parent asParent();
}
