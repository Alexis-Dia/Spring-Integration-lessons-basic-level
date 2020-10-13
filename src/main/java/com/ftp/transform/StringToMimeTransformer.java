package com.ftp.transform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Created by Alexey Druzik on 13.10.2020.
 */
@Service
public class StringToMimeTransformer {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.user}")
    private String user;

    @Value("${mail.subject}")
    private String subject;

    public MimeMessage mailCreation(String m, @Header("target") String target) {

        MimeMessage mimeMessage;
        try {
            mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.toString());
            message.setTo(user);
            message.setSubject(subject);
            message.setText(m, false);
            message.setSentDate(new Date(System.currentTimeMillis()));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return mimeMessage;
    }
}
