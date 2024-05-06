package pl.lodz.p.edu.shop.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.ResourceBundle;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class I18nUtil {

    static {
        Locale.setDefault(new Locale("default"));
    }

    public static final String LOCALE_PL = "pl";
    public static final String LOCALE_EN = "en";

    public static String getMessage(String messageKey, String locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("lang/messages", Locale.forLanguageTag(locale));
        return bundle.getString(messageKey);
    }

    public static class MessageKey {
        public static final String MAIL_COMPANY_NAME = "mail.companyName";
        public static final String MAIL_HELLO = "mail.hello";
        public static final String MAIL_VERIFICATION_SUBJECT = "mail.verification.subject";
        public static final String MAIL_VERIFICATION_FOOTER = "mail.verification.footer";
        public static final String MAIL_VERIFICATION_SUBTITLE = "mail.verification.subtitle";
        public static final String MAIL_VERIFICATION_CONTENT = "mail.verification.content";
        public static final String MAIL_VERIFICATION_URL_TEXT = "mail.verification.urlText";
    }
}
