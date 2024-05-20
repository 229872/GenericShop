package pl.lodz.p.edu.shop.logic.service.api;

import java.util.Map;

public interface MailService {

    void sendSimpleMessage(String recipientEmail, String messageSubject, String message);

    void sendHtmlMessage(String recipientEmail, String messageSubject, String templateName, Map<String, Object> variables);

    void sendVerificationMail(String recipientEmail, String locale, String verificationToken);

    void sendResetPasswordMail(String recipientEmail, String locale, String resetPasswordToken);
}
