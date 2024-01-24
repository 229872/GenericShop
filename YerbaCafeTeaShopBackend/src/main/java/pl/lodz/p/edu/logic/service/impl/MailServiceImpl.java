package pl.lodz.p.edu.logic.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.lodz.p.edu.logic.service.api.MailService;

@RequiredArgsConstructor
@Slf4j

@Service
class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendSimpleMessage(String recipientEmail, String messageSubject, String message) {
        var mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipientEmail);
        mailMessage.setSubject(messageSubject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
        log.info("Successfully sent mail to {}", recipientEmail);
    }

    @Override
    public void sendHtmlMessage(String recipientEmail, String messageSubject, String message) {

    }
}
