package me.adaptive.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Created by panthro on 17/08/15.
 */
@Configuration
@ComponentScan(basePackages = "me.adaptive")
public class EmailConfiguration {

    @Value("#{environment.SMTP_HOST}")
    public String host;
    @Value("#{environment.SMTP_PORT}")
    public Integer port;
    @Value("#{environment.SMTP_USER}")
    public String user;
    @Value("#{environment.SMTP_PASSWORD}")
    public String password;

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
