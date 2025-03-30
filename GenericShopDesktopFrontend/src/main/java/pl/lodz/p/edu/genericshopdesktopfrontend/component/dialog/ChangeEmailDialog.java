package pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.ERROR;
import static pl.lodz.p.edu.genericshopdesktopfrontend.model.pattern.DataPatterns.EMAIL_PATTERN;

public class ChangeEmailDialog extends Stage {

    private final VBox root;
    private final Label labelTitle;
    private final TextField textFieldNewEmail;
    private final Button buttonSubmit;
    private final Function<String, Boolean> onSubmit;
    private final Consumer<TextField> onErrorFieldAnimation;


    public ChangeEmailDialog(Window initWindow, ResourceBundle bundle, Function<String, Boolean> onSubmit,
                                Consumer<TextField> onErrorFieldAnimation) {

        this.onSubmit = requireNonNull(onSubmit);
        this.onErrorFieldAnimation = requireNonNull(onErrorFieldAnimation);

        initOwner(requireNonNull(initWindow));
        initModality(Modality.WINDOW_MODAL);

        this.root = new VBox();
        this.labelTitle = new Label(bundle.getString("change.email"));

        this.textFieldNewEmail = new TextField();
        this.textFieldNewEmail.setPromptText(bundle.getString("enter.new.email"));

        this.buttonSubmit = new Button(bundle.getString("submit"));

        initStyleClasses();
        setUpButtons(bundle);
        setUpFields();

        root.getChildren().addAll(labelTitle, textFieldNewEmail, buttonSubmit);

        setScene(new Scene(root));
        setResizable(false);
        setTitle(bundle.getString("change.email"));
    }


    public void addStyleSheet(String css) {
        getScene().getStylesheets().add(css);
    }


    private void initStyleClasses() {
        root.getStyleClass().add("form_container");
        this.labelTitle.getStyleClass().add("shop");
        this.textFieldNewEmail.getStyleClass().add("field");
        this.buttonSubmit.getStyleClass().add("button");
    }


    private void setUpButtons(ResourceBundle bundle) {
        BooleanBinding isFormValidBinding = Bindings.createBooleanBinding(
            this::isFormValid, textFieldNewEmail.textProperty()
        );

        var blurEffectButtonBinding = Bindings.when(buttonSubmit.disabledProperty())
            .then(new GaussianBlur(5))
            .otherwise(new GaussianBlur(0));

        buttonSubmit.disableProperty().bind(isFormValidBinding.not());
        buttonSubmit.effectProperty().bind(blurEffectButtonBinding);
        buttonSubmit.setOnAction(actionEvent -> changeEmail(bundle));
    }


    private void setUpFields() {
        textFieldNewEmail.focusedProperty()
            .addListener((observableValue, oldValue, newValue) -> {
                if (!newValue && !isFieldValid(EMAIL_PATTERN, textFieldNewEmail)) {
                    onErrorFieldAnimation.accept(textFieldNewEmail);
                }
            });

        textFieldNewEmail.setOnAction(actionEvent -> buttonSubmit.fire());
    }


    private boolean isFormValid() {
        String curPass = textFieldNewEmail.getText();
        return EMAIL_PATTERN.matcher(curPass).matches();
    }


    private boolean isFieldValid(Pattern pattern, TextInputControl control) {
        return pattern.matcher(control.getText()).matches();
    }


    private void changeEmail(ResourceBundle bundle) {
        String newEmail = textFieldNewEmail.getText();

        if (onSubmit.apply(newEmail)) {
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
