package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Tokens;
import pl.lodz.p.edu.genericshopdesktopfrontend.SceneManager;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.Services;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.auth.AuthService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpService;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.ERROR;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.INFO;
import static pl.lodz.p.edu.genericshopdesktopfrontend.model.pattern.DataPatterns.LOGIN_PATTERN;
import static pl.lodz.p.edu.genericshopdesktopfrontend.model.pattern.DataPatterns.PASSWORD_PATTERN;

class AuthSceneController implements Controller, Initializable {

    private final AnimationService animationService;
    private final SceneManager sceneManager;
    private final HttpService httpService;
    private final AuthService authService;


    AuthSceneController(SceneManager sceneManager, Services services) {
        requireNonNull(services);
        this.sceneManager = requireNonNull(sceneManager);
        this.animationService = requireNonNull(services.animation());
        this.httpService = requireNonNull(services.http());
        this.authService = requireNonNull(services.auth());
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
        textFieldLogin.focusedProperty()
            .addListener((observableValue, oldValue, newValue) -> {
                if (!newValue && !isFieldValid(LOGIN_PATTERN, textFieldLogin)) {
                    animationService.shake(textFieldLogin);
                    textLoginError.setVisible(true);
                } else {
                    textLoginError.setVisible(false);
                }
            });

        passwordFieldPassword.focusedProperty()
            .addListener((observableValue, oldValue, newValue) -> {
                if (!newValue && !isFieldValid(PASSWORD_PATTERN, passwordFieldPassword)) {
                    animationService.shake(passwordFieldPassword);
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
            authService.authenticate(tokens);

            clearForm();

            Consumer<Node> animation = node -> animationService.fade(node, Duration.seconds(1), 0.3, 1);
            sceneManager.switchToMainScene(animation);

            Dialog.builder()
                .type(INFO)
                .title(bundle.getString("success"))
                .text(bundle.getString("authentication.was.successful"))
                .display();

        } catch (ApplicationException e) {
            e.printStackTrace();

            Dialog.builder()
                .type(ERROR)
                .title(bundle.getString("error"))
                .text(bundle.getString("authentication.failed"))
                .display();
        }
    }


    private void clearForm() {
        textFieldLogin.clear();
        passwordFieldPassword.clear();
    }
}
