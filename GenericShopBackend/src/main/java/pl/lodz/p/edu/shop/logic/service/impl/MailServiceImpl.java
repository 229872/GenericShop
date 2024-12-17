package pl.lodz.p.edu.shop.logic.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pl.lodz.p.edu.shop.config.frontend.property.FrontendProperties;
import pl.lodz.p.edu.shop.config.security.property.JwtProperties;
import pl.lodz.p.edu.shop.logic.service.api.MailService;
import pl.lodz.p.edu.shop.util.I18nUtil;
import pl.lodz.p.edu.shop.util.I18nUtil.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j

@Service
class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final FrontendProperties frontendProperties;
    private final JwtProperties resetPasswordProperties;

    public MailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine, FrontendProperties frontendProperties,
                           @Qualifier("resetPasswordTokenProperties") JwtProperties resetPasswordProperties) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.frontendProperties = frontendProperties;
        this.resetPasswordProperties = resetPasswordProperties;
    }

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
        String frontendUrl = frontendProperties.getFrontendAppUrl();
        String companyName = I18nUtil.getMessage(MessageKey.MAIL_COMPANY_NAME, locale);
        String subtitle = I18nUtil.getMessage(MessageKey.MAIL_VERIFICATION_SUBTITLE, locale);
        String content = I18nUtil.getMessage(MessageKey.MAIL_VERIFICATION_CONTENT, locale);
        String url = "%s?token=%s".formatted(frontendProperties.getFrontendAccountVerificationUrl(), verificationToken);
        String urlText = I18nUtil.getMessage(MessageKey.MAIL_VERIFICATION_URL_TEXT, locale);
        String footer = I18nUtil.getMessage(MessageKey.MAIL_VERIFICATION_FOOTER, locale);

        Map<String, Object> variables = Map.of(
            "hello", hello,
            "frontendUrl", frontendUrl,
            "companyName", companyName,
            "subtitle", subtitle,
            "name", recipientEmail,
            "content", content,
            "url", url,
            "urlText", urlText,
            "footer", footer
        );

        sendHtmlMessage(recipientEmail, subject, "emailVerificationTemplate", variables);
    }

    @Override
    public void sendResetPasswordMail(String recipientEmail, String locale, String resetPasswordToken) {
        long resetPasswordTimeoutInMillis = resetPasswordProperties.getTimeoutInMillis();
        long resetPasswordTimeoutInHours = TimeUnit.MILLISECONDS.toHours(resetPasswordTimeoutInMillis);

        String subject = I18nUtil.getMessage(MessageKey.MAIL_RESET_PASSWORD_SUBJECT, locale);
        String hello = I18nUtil.getMessage(MessageKey.MAIL_HELLO, locale);
        String frontendUrl = frontendProperties.getFrontendAppUrl();
        String companyName = I18nUtil.getMessage(MessageKey.MAIL_COMPANY_NAME, locale);
        String subtitle = I18nUtil.getMessage(MessageKey.MAIL_RESET_PASSWORD_SUBTITLE, locale);
        String content = I18nUtil.getMessage(MessageKey.MAIL_RESET_PASSWORD_CONTENT, locale);
        String url = "%s?token=%s".formatted(frontendProperties.getFrontendAccountResetPasswordUrl(), resetPasswordToken);
        String urlText = I18nUtil.getMessage(MessageKey.MAIL_RESET_PASSWORD_URL_TEXT, locale);
        String content2Template = resetPasswordTimeoutInHours == 1
            ? I18nUtil.getMessage(MessageKey.MAIL_RESET_PASSWORD_CONTENT2_SINGULAR, locale)
            : I18nUtil.getMessage(MessageKey.MAIL_RESET_PASSWORD_CONTENT2_PLURAL, locale);
        String content2 = String.format(content2Template, resetPasswordTimeoutInHours);
        String footer = I18nUtil.getMessage(MessageKey.MAIL_RESET_PASSWORD_FOOTER, locale);

        Map<String, Object> variables = Map.of(
            "hello", hello,
            "frontendUrl", frontendUrl,
            "companyName", companyName,
            "subtitle", subtitle,
            "name", recipientEmail,
            "content", content,
            "url", url,
            "urlText", urlText,
            "content2", content2,
            "footer", footer
        );

        sendHtmlMessage(recipientEmail, subject, "resetPasswordTemplate", variables);
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
