package pl.lodz.p.edu.genericshopdesktopfrontend.config;

import static java.util.Objects.requireNonNull;

public class Resources {

    public static class CSS {
        public static final String CHANGE_FORM_DIALOG =
            requireNonNull(Resources.class.getResource("/styles/form_dialog.css")).toExternalForm();
    }


    public static class Scene {
        public static final String AUTHENTICATION = "/view/scene/authentication/authentication_scene";
        public static final String DASHBOARD = "/view/scene/main/main_scene";

        public static final String SUB_SETTINGS = "/view/scene/sub/settings/settings_scene";
        public static String SUB_ACCOUNT = "/view/scene/sub/account/account_scene";
    }
}
