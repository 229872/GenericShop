package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.text.Text;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.model.pattern.DataPatterns.LOGIN_PATTERN;
import static pl.lodz.p.edu.genericshopdesktopfrontend.model.pattern.DataPatterns.PASSWORD_PATTERN;

class AuthenticationController implements Controller, Initializable {

    private final AnimationService animationService;

    @FXML
    private TextField textFieldLogin;

    @FXML
    private PasswordField passwordFieldPassword;

    @FXML
    private Text textLoginError, textPasswordError;

    @FXML
    private Button buttonSignIn;

    AuthenticationController(AnimationService animationService) {
        this.animationService = requireNonNull(animationService);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setUpButtonBindings();
        setUpInputs();
    }

    private void setUpButtonBindings() {
        BooleanBinding isFormValidBinding = Bindings.createBooleanBinding(
            this::isFormValid, textFieldLogin.textProperty(), passwordFieldPassword.textProperty());

        var blurEffectButtonBinding = Bindings.when(buttonSignIn.disabledProperty())
            .then(new GaussianBlur(5))
            .otherwise(new GaussianBlur(0));

        buttonSignIn.disableProperty().bind(isFormValidBinding.not());
        buttonSignIn.effectProperty().bind(blurEffectButtonBinding);
    }

    private void setUpInputs() {

        textFieldLogin.focusedProperty().addListener((observableValue, oldValue, newValue) -> {

            if (!newValue && !isFieldValid(LOGIN_PATTERN, textFieldLogin)) {
                animationService.shakeField(textFieldLogin);
                textLoginError.setVisible(true);
            } else {
                textLoginError.setVisible(false);
            }
        });

        passwordFieldPassword.focusedProperty().addListener((observableValue, oldValue, newValue) -> {

            if (!newValue && !isFieldValid(PASSWORD_PATTERN, passwordFieldPassword)) {
                animationService.shakeField(passwordFieldPassword);
                textPasswordError.setVisible(true);
            } else {
                textPasswordError.setVisible(false);
            }
        });
    }

    private boolean isFormValid() {
        boolean isLoginValid = LOGIN_PATTERN.matcher(textFieldLogin.getText()).matches();
        boolean isPasswordValid = PASSWORD_PATTERN.matcher(passwordFieldPassword.getText()).matches();

        return isLoginValid && isPasswordValid;
    }

    private boolean isFieldValid(Pattern pattern, TextInputControl control) {
        return pattern.matcher(control.getText()).matches();
    }


}
