package pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextInputControl;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.field.FieldWithErrorHandling;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.field.FieldWithIcon;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.ChangePasswordDto;

import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;

import static de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.KEY;
import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.ERROR;
import static pl.lodz.p.edu.genericshopdesktopfrontend.model.pattern.DataPatterns.PASSWORD_PATTERN;

public class ChangePasswordDialog extends Stage {

    private final VBox root;
    private final Label labelTitle;
    private final FieldWithIcon passwordFieldCurrent;
    private final FieldWithIcon passwordFieldNew;
    private final Button buttonSubmit;
    private final Function<ChangePasswordDto, Boolean> onSubmit;
    private final Consumer<TextInputControl> onErrorFieldAnimation;


    public ChangePasswordDialog(Window initWindow, ResourceBundle bundle, Function<ChangePasswordDto, Boolean> onSubmit,
                                Consumer<TextInputControl> onErrorFieldAnimation) {

        this.onSubmit = requireNonNull(onSubmit);
        this.onErrorFieldAnimation = requireNonNull(onErrorFieldAnimation);

        initOwner(requireNonNull(initWindow));
        initModality(Modality.WINDOW_MODAL);

        this.root = new VBox();
        this.labelTitle = new Label(bundle.getString("change.password"));


        this.passwordFieldCurrent = new FieldWithIcon(KEY, 30, new FieldWithErrorHandling(
            bundle.getString("enter.current.password"),
            bundle.getString("password.not.valid"),
            PASSWORD_PATTERN,
            new PasswordField()
        ));
        this.passwordFieldCurrent.setAnimation(onErrorFieldAnimation);

        this.passwordFieldNew = new FieldWithIcon(KEY, 30, new FieldWithErrorHandling(
            bundle.getString("enter.new.password"),
            bundle.getString("password.not.valid"),
            PASSWORD_PATTERN,
            new PasswordField()
        ));
        this.passwordFieldNew.setAnimation(onErrorFieldAnimation);

        this.buttonSubmit = new Button(bundle.getString("submit"));

        initStyleClasses();
        setUpButtons(bundle);

        root.getChildren().addAll(labelTitle, passwordFieldCurrent, passwordFieldNew, buttonSubmit);
        setScene(new Scene(root));
        setResizable(false);
        setTitle(bundle.getString("change.password"));
        Platform.runLater(root::requestFocus);
    }


    public void addStyleSheet(String css) {
        getScene().getStylesheets().add(css);
    }


    private void initStyleClasses() {
        root.getStyleClass().add("form_container");
        this.labelTitle.getStyleClass().add("title");
        this.buttonSubmit.getStyleClass().add("button");
    }


    private void setUpButtons(ResourceBundle bundle) {
        BooleanBinding isFormValidBinding = Bindings.createBooleanBinding(
            this::isFormValid, passwordFieldCurrent.textProperty(), passwordFieldNew.textProperty()
        );

        var blurEffectButtonBinding = Bindings.when(buttonSubmit.disabledProperty())
            .then(new GaussianBlur(5))
            .otherwise(new GaussianBlur(0));

        buttonSubmit.disableProperty().bind(isFormValidBinding.not());
        buttonSubmit.effectProperty().bind(blurEffectButtonBinding);
        buttonSubmit.setOnAction(actionEvent -> changePassword(bundle));
    }


    private boolean isFormValid() {
        String curPass = passwordFieldCurrent.textProperty().get();
        String newPass = passwordFieldNew.textProperty().get();

        boolean areFieldsSame = curPass.equals(newPass);

        return passwordFieldCurrent.isValid() && passwordFieldNew.isValid() && !areFieldsSame;
    }


    private void changePassword(ResourceBundle bundle) {
        String curPass = passwordFieldCurrent.textProperty().get();
        String newPass = passwordFieldNew.textProperty().get();

        var dto = new ChangePasswordDto(curPass, newPass);

        if (onSubmit.apply(dto)) {
            close();

        } else {
            Dialog.builder()
                .title(bundle.getString("error"))
                .text(bundle.getString("error"))
                .type(ERROR)
                .display();
        }
    }
}
