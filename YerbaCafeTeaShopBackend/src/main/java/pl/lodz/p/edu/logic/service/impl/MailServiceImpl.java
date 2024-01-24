package pl.lodz.p.edu.logic.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pl.lodz.p.edu.logic.service.api.MailService;
import pl.lodz.p.edu.util.I18nUtil;
import pl.lodz.p.edu.util.I18nUtil.MessageKey;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j

@Service
class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendSimpleMessage(String recipientEmail, String messageSubject, String message) {
        var mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipientEmail);
        mailMessage.setSubject(messageSubject);
        mailMessage.setText(message);

        sendMail(mailMessage);
    }

    @Override
    public void sendHtmlMessage(String recipientEmail, String messageSubject, String templateName, Map<String, Object> variables) {
        try {
            String emailVerificationTemplate = buildEmailTemplate(templateName, variables);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setTo(recipientEmail);
            mimeMessageHelper.setSubject(messageSubject);
            mimeMessageHelper.setText(emailVerificationTemplate, true);

            sendMail(mimeMessage, recipientEmail);

        } catch (MessagingException e) {
            log.error("Messaging exception: ", e);
        }
    }

    @Override
    public void sendVerificationMail(String recipientEmail, String locale, String verificationToken) {
        String subject = I18nUtil.getMessage(MessageKey.MAIL_VERIFICATION_SUBJECT, locale);
        String hello = I18nUtil.getMessage(MessageKey.MAIL_HELLO, locale);
        String companyName = I18nUtil.getMessage(MessageKey.MAIL_COMPANY_NAME, locale);
        String subtitle = I18nUtil.getMessage(MessageKey.MAIL_VERIFICATION_SUBTITLE, locale);
        String content = I18nUtil.getMessage(MessageKey.MAIL_VERIFICATION_CONTENT, locale);
        String urlText = I18nUtil.getMessage(MessageKey.MAIL_VERIFICATION_URL_TEXT, locale);
        String footer = I18nUtil.getMessage(MessageKey.MAIL_VERIFICATION_FOOTER, locale);

        Map<String, Object> variables = Map.of(
            "hello", hello,
            "companyName", companyName,
            "subtitle", subtitle,
            "name", recipientEmail,
            "content", content,
            "url", verificationToken,
            "urlText", urlText,
            "footer", footer
        );

        sendHtmlMessage(recipientEmail, subject, "emailVerificationTemplate", variables);
    }


    private String buildEmailTemplate(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }

    private void sendMail(SimpleMailMessage message) {
        try {
            mailSender.send(message);
            log.info("Successfully sent mail to {}", (Object) message.getTo());

        } catch (MailException mailException) {
            log.error("Couldn't send mail to {}", message.getTo(), mailException);
        }
    }

    private void sendMail(MimeMessage message, String recipientEmail) {
        try {
            mailSender.send(message);
            log.info("Successfully sent mail to {}", recipientEmail);

        } catch (MailException mailException) {
            log.error("Couldn't send mail to {}", recipientEmail, mailException);
        }
    }
}
