package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Role;
import pl.lodz.p.edu.genericshopdesktopfrontend.scene.SceneManager;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.auth.AuthService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpService;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.ERROR;

class SettingsSceneController implements Controller, Initializable {

    private final AuthService authService;
    private final SceneManager sceneManager;
    private final HttpService httpService;
    private final AnimationService animationService;


    SettingsSceneController(SceneManager sceneManager, HttpService httpService, AnimationService animationService) {
        this.httpService = requireNonNull(httpService);
        this.authService = requireNonNull(httpService.getAuthService());
        this.sceneManager = requireNonNull(sceneManager);
        this.animationService = requireNonNull(animationService);
    }


    @FXML
    private VBox vboxRoles, vboxLanguages;

    @FXML
    private SplitPane splitPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpRoleButtons(resourceBundle);
        setUpLanguageButtons(resourceBundle);
        setUpDividers();
    }


    private void setUpDividers() {
        double[] dividerPositions = splitPane.getDividerPositions();
        ObservableList<SplitPane.Divider> dividers = splitPane.getDividers();

        if (dividers.isEmpty()) return;

        dividers.get(0)
            .positionProperty()
            .addListener((observableValue, oldVal, newVal) -> {
                splitPane.setDividerPositions(dividerPositions);
            });
    }


    private void setUpRoleButtons(ResourceBundle bundle) {
        Font buttonsFont = Font.font(20);
        Color textColor = Color.BLACK;

        ToggleGroup roleGroup = new ToggleGroup();

        Role activeRole = authService.getActiveRole();

        List<RadioButton> radioButtons = authService.getAccountRoles().stream()
            .map(role -> {
                String buttonName = bundle.getString(role.name().toLowerCase());
                var button = new RadioButton(buttonName);
                button.setTextFill(textColor);
                button.setFont(buttonsFont);
                button.setUserData(role);

                if (role.equals(activeRole)) {
                    button.setSelected(true);
                }

                return button;
            })
            .toList();

        roleGroup.selectedToggleProperty()
            .addListener((observableValue, oldToggle, newToggle) -> {
                if (newToggle.getUserData() instanceof Role newRole) {
                    authService.setActiveRole(newRole);
                }
            });

        roleGroup.getToggles().addAll(radioButtons);
        vboxRoles.getChildren().addAll(radioButtons);
    }

    private void setUpLanguageButtons(ResourceBundle resourceBundle) {
        Font buttonsFont = Font.font(20);
        Color textColor = Color.BLACK;
        Consumer<Node> animation = node ->
            animationService.fade(node, Duration.millis(75), 0.8, 1);

        ToggleGroup languageGroup = new ToggleGroup();

        List<RadioButton> radioButtons = Stream.of("en", "pl")
            .map(language -> {
                Locale locale = Locale.forLanguageTag(language);

                var button = new RadioButton(language);
                button.setTextFill(textColor);
                button.setFont(buttonsFont);
                button.setUserData(locale);

                if (Locale.getDefault().getLanguage().equals(language)) {
                    button.setSelected(true);
                }

                return button;
            })
            .toList();

        languageGroup.selectedToggleProperty()
            .addListener((observableValue, oldToggle, newToggle) -> {
                if (oldToggle != null && newToggle.getUserData() instanceof Locale newLanguage) {
                    Locale.setDefault(newLanguage);
                    changeLanguageOnServer(newLanguage.getLanguage(), resourceBundle);
                    sceneManager.setApplicationLanguage(newLanguage);

                    sceneManager.switchToMainScene(animation);
                }
            });

        languageGroup.getToggles().addAll(radioButtons);
        vboxLanguages.getChildren().addAll(radioButtons);
    }


    private void changeLanguageOnServer(String language, ResourceBundle bundle) {
        try {
            httpService.sendChangeAccountLanguageRequest(language);

        } catch (ApplicationException e) {
            e.printStackTrace();

            Dialog.builder()
                .type(ERROR)
                .title(bundle.getString("error.title"))
                .text(bundle.getString("error.text"))
                .display();
        }
    }
}
