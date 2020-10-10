package com.ftp.config;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.dsl.*;
import org.springframework.integration.ftp.dsl.Ftp;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.MessageChannel;

import java.io.File;

@Configuration
public class FtpConfig {

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    @Primary
    public PollerMetadata poller() {
        return Pollers.fixedRate(1000).get();
    }

    @Bean
    DefaultFtpSessionFactory ftpSessionFactory() {
        DefaultFtpSessionFactory defaultFtpSessionFactory = new DefaultFtpSessionFactory();
        defaultFtpSessionFactory.setHost("127.0.0.1");
        defaultFtpSessionFactory.setUsername("user1");
        defaultFtpSessionFactory.setPassword("user1");
        defaultFtpSessionFactory.setClientMode(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE);
        return defaultFtpSessionFactory;
    }

    @Bean
    public MessageChannel ftpInboundResultChannel() {
        return MessageChannels.queue().get();
    }

    @Bean
    public MessageChannel sendTwitterChannel() {
        return MessageChannels.queue().get();
    }

    @Bean
    public IntegrationFlow ftpInboundFlow() {
        return IntegrationFlows
                .from(Ftp.inboundAdapter(ftpSessionFactory())
                        .preserveTimestamp(true)
                        .localDirectory(new File("public"))
                        .autoCreateLocalDirectory(true)
                    )
                .channel("ftpInboundResultChannel")
                .get();
    }

    @Bean
    public IntegrationFlow processDownloaded() {
        return IntegrationFlows
                .from("ftpInboundResultChannel")
            .transform(Transformers.objectToString())
            .log(msg -> "client1: " + msg.getPayload())
/*                .transform(new FileToStringTransformer())
                .<String, String>route(s -> s.split(":")[0],
                        spec -> spec.prefix("send").suffix("Channel")
                                .channelMapping("mail", "RawMail")
                                .channelMapping("tweet", "Twitter")
                                .resolutionRequired(false)
                                .defaultOutputChannel("sendSystemOutChannel"))*/
                //.channel("sendSystemOutChannel")
                .get();
    }

    @Bean
    public IntegrationFlow showOnConsole() {
        System.out.println("11111");
        return f -> f.channel("sendSystemOutChannel")
                .handle(
                    (msg) -> {
                        System.out.println("Msg = " + msg);
                    }
                );
    }

    @Bean
    public IntegrationFlow eMail() {
        return f -> f.channel("sendRawMailChannel")
                .enrichHeaders(h -> h.<String>headerFunction("target", m -> m.getPayload().split(":")[1]))
                .<String, String>transform(s -> s.substring(s.lastIndexOf(":")))
                .channel("sendMailChannel");
    }
}