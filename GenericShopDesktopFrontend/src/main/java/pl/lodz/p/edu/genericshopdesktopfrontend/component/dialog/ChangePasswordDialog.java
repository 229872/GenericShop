package pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.ChangePasswordDto;

import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.ERROR;
import static pl.lodz.p.edu.genericshopdesktopfrontend.model.pattern.DataPatterns.PASSWORD_PATTERN;

public class ChangePasswordDialog extends Stage {

    private final VBox root;
    private final Label labelTitle;
    private final PasswordField passwordFieldCurrent;
    private final PasswordField passwordFieldNew;
    private final Button buttonSubmit;
    private final Function<ChangePasswordDto, Boolean> onSubmit;
    private final Consumer<TextField> onErrorFieldAnimation;


    public ChangePasswordDialog(Window initWindow, ResourceBundle bundle, Function<ChangePasswordDto, Boolean> onSubmit,
                                Consumer<TextField> onErrorFieldAnimation) {

        this.onSubmit = requireNonNull(onSubmit);
        this.onErrorFieldAnimation = requireNonNull(onErrorFieldAnimation);

        initOwner(requireNonNull(initWindow));
        initModality(Modality.WINDOW_MODAL);

        this.root = new VBox();
        this.labelTitle = new Label(bundle.getString("change.password"));

        this.passwordFieldCurrent = new PasswordField();
        this.passwordFieldCurrent.setPromptText(bundle.getString("enter.current.password"));

        this.passwordFieldNew = new PasswordField();
        this.passwordFieldNew.setPromptText(bundle.getString("enter.new.password"));

        this.buttonSubmit = new Button(bundle.getString("submit"));

        initStyleClasses();
        setUpButtons(bundle);
        setUpFields();

        root.getChildren().addAll(labelTitle, passwordFieldCurrent, passwordFieldNew, buttonSubmit);
        setScene(new Scene(root));
        setResizable(false);
        setTitle(bundle.getString("change.password"));
    }


    public void addStyleSheet(String css) {
        getScene().getStylesheets().add(css);
    }


    private void initStyleClasses() {
        root.getStyleClass().add("form_container");
        this.labelTitle.getStyleClass().add("title");
        this.passwordFieldCurrent.getStyleClass().add("field");
        this.passwordFieldNew.getStyleClass().add("field");
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


    private void setUpFields() {
        passwordFieldCurrent.focusedProperty()
            .addListener((observableValue, oldValue, newValue) -> {
                if (!newValue && !isFieldValid(PASSWORD_PATTERN, passwordFieldCurrent)) {
                    onErrorFieldAnimation.accept(passwordFieldCurrent);
                }
            });

        passwordFieldNew.focusedProperty()
            .addListener((observableValue, oldValue, newValue) -> {
                if (!newValue && !isFieldValid(PASSWORD_PATTERN, passwordFieldNew)) {
                    onErrorFieldAnimation.accept(passwordFieldNew);
                }
            });

        passwordFieldNew.setOnAction(actionEvent -> buttonSubmit.fire());
    }


    private boolean isFormValid() {
        String curPass = passwordFieldCurrent.getText();
        String newPass = passwordFieldNew.getText();

        boolean isCurPassValid = PASSWORD_PATTERN.matcher(curPass).matches();
        boolean isNewPassValid = PASSWORD_PATTERN.matcher(newPass).matches();
        boolean areFieldsSame = curPass.equals(newPass);

        return isCurPassValid && isNewPassValid && !areFieldsSame;
    }


    private boolean isFieldValid(Pattern pattern, TextInputControl control) {
        return pattern.matcher(control.getText()).matches();
    }


    private void changePassword(ResourceBundle bundle) {
        String curPass = passwordFieldCurrent.getText();
        String newPass = passwordFieldNew.getText();

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
