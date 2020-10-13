package com.ftp.config;

import com.ftp.transform.StringToMimeTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.mail.MailHeaders;
import org.springframework.integration.mail.dsl.Mail;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.messaging.MessageChannel;

/**
 * Created by Alexey Druzik on 13.10.2020.
 * Turning ON Allow Less Secure Apps was simply the solution in my case
 * - https://myaccount.google.com/u/1/lesssecureapps
 * - https://stackoverflow.com/questions/35347269/javax-mail-authenticationfailedexception-535-5-7-8-username-and-password-not-ac
 */
@Configuration
public class MailConfig {

    @Value("${mail.smtp.host}")
    private String host;
    @Value("${mail.port}")
    private int port;
    @Value("${mail.protocol}")
    private String protocol;
    @Value("${mail.user}")
    private String user;
    @Value("${mail.password}")
    private String password;
    @Value("${mail.from}")
    private String from;
    @Value("${mail.encoding}")
    private String encoding;
    @Value("${mail.smtp.auth}")
    private boolean smtpAuth;
    @Value("${mail.smtp.starttls.enable}")
    private boolean smtpStartTlsEnable;
    @Value("${mail.debug}")
    private boolean debug;

    @Autowired
    private StringToMimeTransformer toMimeTransformer;

    @Bean
    public MessageChannel sendMailChannel() {
        return MessageChannels.queue().datatype(String.class).get();
    }

    @Bean
    public JavaMailSenderImpl mailSender() {
        return new JavaMailSenderImpl();
    }

    @Bean
    IntegrationFlow sendMailFlow() {
        return IntegrationFlows.from("sendMailChannel")
                .enrichHeaders(h -> h.header(MailHeaders.FROM, from))
                .transform(toMimeTransformer)
                .handle(Mail.outboundAdapter(host)
                                .port(port)
                                .protocol(protocol)
                                .credentials(user, password)
                                .defaultEncoding(encoding)
                                .javaMailProperties(p -> {
                                    p.put("mail.smtp.auth", smtpAuth);
                                    p.put("mail.smtp.starttls.enable", smtpStartTlsEnable);
                                    p.put("mail.debug", debug);
                                }),
                        e -> e.id("sendMailEndpoint")).get();
    }

}

