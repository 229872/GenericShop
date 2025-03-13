package pl.lodz.p.edu.genericshopdesktopfrontend.model.pattern;

import java.util.regex.Pattern;

public class DataPatterns {
    public static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]+$");
    public static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[!@#$%^&+=]).*$");
}
