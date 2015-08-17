package me.adaptive.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Created by panthro on 17/08/15.
 */
@Configuration
public class EmailConfiguration {

    @Value("#{systemEnvironment.smtp.host}")
    private String host;
    @Value("#{systemEnvironment.smtp.port}")
    private Integer port;
    @Value("#{systemEnvironment.smtp.user}")
    private String user;
    @Value("#{systemEnvironment.smtp.user}")
    private String password;

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setUsername(user);
        sender.setPassword(password);
        sender.setDefaultEncoding("UTF-8");
        sender.setPort(port);
        return sender;
    }

}
