package com.ftp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

/**
 * Created by Alexey Druzik on 13.10.2020.
 * In order to send emails - turning ON Allow Less Secure Apps was simply the solution in my case
 * - https://myaccount.google.com/u/1/lesssecureapps
 * - https://stackoverflow.com/questions/35347269/javax-mail-authenticationfailedexception-535-5-7-8-username-and-password-not-ac
  */
@SpringBootApplication
@EnableIntegration
@IntegrationComponentScan
public class DemoProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoProcessorApplication.class, args);
    }
}
