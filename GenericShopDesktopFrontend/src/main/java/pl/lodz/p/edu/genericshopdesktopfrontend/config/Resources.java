package pl.lodz.p.edu.genericshopdesktopfrontend.config;

import static java.util.Objects.requireNonNull;

public class Resources {

    public static class CSS {
        public static final String CHANGE_FORM_DIALOG =
            requireNonNull(Resources.class.getResource("/styles/form_dialog.css")).toExternalForm();
    }

}
