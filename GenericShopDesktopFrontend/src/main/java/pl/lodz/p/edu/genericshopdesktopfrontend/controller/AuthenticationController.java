package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.alert.ErrorDialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.model.pattern.DataPatterns.LOGIN_PATTERN;
import static pl.lodz.p.edu.genericshopdesktopfrontend.model.pattern.DataPatterns.PASSWORD_PATTERN;

class AuthenticationController implements Controller, Initializable {

    private final AnimationService animationService;

    private final SceneManager sceneManager;

    AuthenticationController(AnimationService animationService, SceneManager sceneManager) {
        this.animationService = requireNonNull(animationService);
        this.sceneManager = requireNonNull(sceneManager);
    }

    @FXML
    private BorderPane root;

    @FXML
    private TextField textFieldLogin;

    @FXML
    private PasswordField passwordFieldPassword;

    @FXML
    private ComboBox<Locale> comboBoxLanguage;

    @FXML
    private Text textLoginError, textPasswordError;

    @FXML
    private Button buttonSignIn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setUpButtons();
        setUpInputs();
        setUpLanguageChoiceBox();
        Platform.runLater(() -> root.requestFocus());
    }

    private void setUpButtons() {
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

    private void setUpLanguageChoiceBox() {
        ObservableList<Locale> languages = FXCollections.observableArrayList(
            Locale.forLanguageTag("en"),
            Locale.forLanguageTag("pl")
        );
        comboBoxLanguage.setItems(languages);
        comboBoxLanguage.setValue(Locale.forLanguageTag(Locale.getDefault().getLanguage()));
        comboBoxLanguage.getSelectionModel()
            .selectedItemProperty()
            .addListener((observableValue, oldLocale, newLocale) -> {
                try {
                    Locale.setDefault(newLocale);
                    sceneManager.setApplicationLanguage(newLocale);
                    sceneManager.switchToAuthenticationScene();

                } catch (ApplicationException e) {
                    e.printStackTrace();

                    Alert alert = new ErrorDialog("Couldn't change language.", "");
                    alert.show();
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
