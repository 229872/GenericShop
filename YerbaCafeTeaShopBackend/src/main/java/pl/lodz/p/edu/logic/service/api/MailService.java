package pl.lodz.p.edu.logic.service.api;

import java.util.Map;

public interface MailService {

    void sendSimpleMessage(String recipientEmail, String messageSubject, String message);

    void sendHtmlMessage(String recipientEmail, String messageSubject, String templateName, Map<String, Object> variables);

    void sendVerificationMail(String recipientEmail, String locale, String verificationToken);
}
