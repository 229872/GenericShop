package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import org.controlsfx.control.Notifications;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Tokens;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.auth.AuthenticationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpService;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.model.pattern.DataPatterns.LOGIN_PATTERN;
import static pl.lodz.p.edu.genericshopdesktopfrontend.model.pattern.DataPatterns.PASSWORD_PATTERN;

class AuthenticationSceneController implements Controller, Initializable {

    private final AnimationService animationService;

    private final SceneManager sceneManager;

    private final HttpService httpService;

    private final AuthenticationService authenticationService;

    AuthenticationSceneController(AnimationService animationService, SceneManager sceneManager,
                                  HttpService httpService, AuthenticationService authenticationService) {
        this.animationService = requireNonNull(animationService);
        this.sceneManager = requireNonNull(sceneManager);
        this.httpService = requireNonNull(httpService);
        this.authenticationService = requireNonNull(authenticationService);
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

        setUpButtons(resourceBundle);
        setUpInputs();
        setUpLanguageChoiceBox(resourceBundle);
        Platform.runLater(() -> root.requestFocus());
    }

    private void setUpButtons(ResourceBundle bundle) {
        BooleanBinding isFormValidBinding = Bindings.createBooleanBinding(
            this::isFormValid, textFieldLogin.textProperty(), passwordFieldPassword.textProperty());

        var blurEffectButtonBinding = Bindings.when(buttonSignIn.disabledProperty())
            .then(new GaussianBlur(5))
            .otherwise(new GaussianBlur(0));

        buttonSignIn.disableProperty().bind(isFormValidBinding.not());
        buttonSignIn.effectProperty().bind(blurEffectButtonBinding);

        buttonSignIn.setOnAction(actionEvent -> authenticate(bundle));
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

        passwordFieldPassword.setOnAction(actionEvent -> buttonSignIn.fire());
    }

    private void setUpLanguageChoiceBox(ResourceBundle languageBundle) {
        ObservableList<Locale> languages = FXCollections.observableArrayList(
            Locale.forLanguageTag("en"),
            Locale.forLanguageTag("pl")
        );
        comboBoxLanguage.setItems(languages);
        comboBoxLanguage.setValue(Locale.forLanguageTag(languageBundle.getLocale().getLanguage()));
        comboBoxLanguage.getSelectionModel()
            .selectedItemProperty()
            .addListener((observableValue, oldLocale, newLocale) -> {
                Locale.setDefault(newLocale);
                sceneManager.setApplicationLanguage(newLocale);
                sceneManager.switchToAuthenticationScene();
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

    private void authenticate(ResourceBundle bundle) {
        try {
            String login = textFieldLogin.getText();
            String password = passwordFieldPassword.getText();

            Tokens tokens = httpService.sendAuthenticationRequest(login, password);
            authenticationService.authenticate(tokens);

            clearForm();

            sceneManager.switchToMainScene();

            Notifications.create()
                .position(Pos.TOP_RIGHT)
                .title(bundle.getString("authentication.success.title"))
                .text(bundle.getString("authentication.success.text"))
                .showInformation();


        } catch (ApplicationException e) {
            e.printStackTrace();

            Notifications.create()
                .position(Pos.TOP_RIGHT)
                .title(bundle.getString("authentication.error.title"))
                .text(bundle.getString("authentication.error.text"))
                .showError();
        }
    }

    private void clearForm() {
        textFieldLogin.clear();
        passwordFieldPassword.clear();
    }
}
