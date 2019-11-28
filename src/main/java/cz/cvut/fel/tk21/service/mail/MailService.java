package cz.cvut.fel.tk21.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void sendEmail(SimpleMailMessage mail){
        javaMailSender.send(mail);
    }

}
