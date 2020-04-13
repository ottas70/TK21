package cz.cvut.fel.tk21.service.mail;

import cz.cvut.fel.tk21.config.properties.AppProperties;
import cz.cvut.fel.tk21.model.mail.Mail;
import cz.cvut.fel.tk21.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
public class MailService {

    private static Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final AppProperties appProperties;

    @Autowired
    public MailService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine, AppProperties appProperties) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.appProperties = appProperties;
    }

    @Async
    public void sendEmailConfirmation(Mail mail){
        try{
            MimeMessage mimeMessage = loadMessageTemplate(mail, "EmailConfirmation");
            javaMailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            log.error("Error while sending an email: " + ex.getMessage());
        }
    }

    @Async
    public void sendForgottenPassword(Mail mail){
        try{
            MimeMessage mimeMessage = loadMessageTemplate(mail, "ForgottenPassword");
            javaMailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            log.error("Error while sending an email: " + ex.getMessage());
        }
    }

    @Async
    public void sendReservationSummary(Mail mail){
        try{
            MimeMessage mimeMessage = loadMessageTemplate(mail, "ReservationSummary");
            javaMailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            log.error("Error while sending an email: " + ex.getMessage());
        }
    }

    @Async
    public void sendProfessionalPlayerInvite(Mail mail){
        try{
            MimeMessage mimeMessage = loadMessageTemplate(mail, "ProfessionalPlayerInvitation");
            javaMailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            log.error("Error while sending an email: " + ex.getMessage());
        }
    }

    @Async
    public void sendProfessionalPlayerInviteNonRegisteredPlayer(Mail mail){
        try{
            MimeMessage mimeMessage = loadMessageTemplate(mail, "ProfessionalPlayerInvitationNonRegisteredPlayer");
            javaMailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            log.error("Error while sending an email: " + ex.getMessage());
        }
    }

    @Async
    public void sendInvitationAccepted(Mail mail){
        try{
            MimeMessage mimeMessage = loadMessageTemplate(mail, "InvitationAccepted");
            javaMailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            log.error("Error while sending an email: " + ex.getMessage());
        }
    }

    @Async
    public void sendScrapingErrorMail(Mail mail){
        try{
            MimeMessage mimeMessage = loadMessageTemplate(mail, "ScrapingError");
            javaMailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            log.error("Error while sending an email: " + ex.getMessage());
        }
    }


    private MimeMessage loadMessageTemplate(Mail mail, String template) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

        mail.getModel().put("url", appProperties.getUrl());
        mail.getModel().put("year", DateUtils.getCurrentYear());

        Context context = new Context();
        context.setVariables(mail.getModel());
        String html = templateEngine.process(template, context);

        message.setTo(mail.getTo());
        message.setText(html, true);
        message.setSubject(mail.getSubject());
        message.setFrom(mail.getFrom());

        return mimeMessage;
    }

}
