package me.adaptive.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

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
        sender.setJavaMailProperties(getJavaMailProperties());
        return sender;
    }

    private Properties getJavaMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.socketFactory.port", port);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        properties.put("mail.smtp.timeout", 25000);
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        return properties;
    }

}
