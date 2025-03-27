package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Role;
import pl.lodz.p.edu.genericshopdesktopfrontend.scene.SceneManager;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.auth.AuthenticationService;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

class SettingsSceneController implements Controller, Initializable {

    private final AuthenticationService authenticationService;
    private final SceneManager sceneManager;


    SettingsSceneController(AuthenticationService authenticationService, SceneManager sceneManager) {
        this.authenticationService = requireNonNull(authenticationService);
        this.sceneManager = requireNonNull(sceneManager);
    }


    @FXML
    private VBox vboxRoles, vboxLanguages;

    @FXML
    private SplitPane splitPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpRoleButtons(resourceBundle);
        setUpLanguageButtons();
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

        Role activeRole = authenticationService.getActiveRole();

        List<RadioButton> radioButtons = authenticationService.getAccountRoles().stream()
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
                    authenticationService.setActiveRole(newRole);
                }
            });

        roleGroup.getToggles().addAll(radioButtons);
        vboxRoles.getChildren().addAll(radioButtons);
    }

    private void setUpLanguageButtons() {
        Font buttonsFont = Font.font(20);
        Color textColor = Color.BLACK;

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
                    sceneManager.setApplicationLanguage(newLanguage);
                    sceneManager.switchToMainScene();
                }
            });

        languageGroup.getToggles().addAll(radioButtons);
        vboxLanguages.getChildren().addAll(radioButtons);
    }
}
