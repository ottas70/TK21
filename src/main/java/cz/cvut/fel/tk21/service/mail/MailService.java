package cz.cvut.fel.tk21.service.mail;

import cz.cvut.fel.tk21.model.mail.Mail;
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
import java.util.logging.Level;
import java.util.logging.LogRecord;

@Service
public class MailService {

    private static Logger log = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

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
    public void sendReservationSummary(Mail mail){
        try{
            MimeMessage mimeMessage = loadMessageTemplate(mail, "ReservationSummary");
            javaMailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            log.error("Error while sending an email: " + ex.getMessage());
        }
    }


    private MimeMessage loadMessageTemplate(Mail mail, String template) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

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
