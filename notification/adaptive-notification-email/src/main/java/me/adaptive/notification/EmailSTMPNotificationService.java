package me.adaptive.notification;

import me.adaptive.core.data.domain.NotificationEntity;
import me.adaptive.core.data.domain.NotificationErrorEntity;
import me.adaptive.core.data.domain.types.NotificationChannel;
import me.adaptive.core.data.util.UserPreferences;
import me.adaptive.services.notification.NotificationService;
import me.adaptive.services.notification.error.NotificationException;
import me.adaptive.services.notification.template.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by panthro on 17/08/15.
 * Sends notifications using SMTP server
 */
@Service
public class EmailSTMPNotificationService implements NotificationService {

    @Value("#{systemEnvironment.smtp.from}")
    String from;
    @Autowired
    TemplateService templateService;
    @Autowired
    JavaMailSender mailSender;

    public String getServiceId() {
        return EmailSTMPNotificationService.class.getSimpleName();
    }

    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void notify(final NotificationEntity notification, Map<String, Object> model) throws NotificationException {
        String email = notification.getUserNotified().getPreferences().get(UserPreferences.Notification.EMAIL);
        if (email == null) {
            throw new NotificationException(new NotificationErrorEntity(notification, "User does not have an email to be sent"));
        }
        notification.setDestination(email);
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setTo(email);
            message.setFrom(from);
            String text = templateService.parseTemplate(notification, model);
            message.setText(text, true);
        };
        this.mailSender.send(preparator);
    }
}
