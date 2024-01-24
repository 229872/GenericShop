package pl.lodz.p.edu.logic.service.api;

public interface MailService {

    void sendSimpleMessage(String recipientEmail, String messageSubject, String message);

    void sendHtmlMessage(String recipientEmail, String messageSubject, String message);
}
