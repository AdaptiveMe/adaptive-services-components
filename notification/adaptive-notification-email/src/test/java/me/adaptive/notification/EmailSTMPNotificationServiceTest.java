package me.adaptive.notification;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import me.adaptive.core.data.config.JpaConfiguration;
import me.adaptive.core.data.domain.NotificationEntity;
import me.adaptive.core.data.domain.types.NotificationChannel;
import me.adaptive.core.data.domain.types.NotificationEvent;
import me.adaptive.core.data.domain.types.NotificationStatus;
import me.adaptive.core.data.util.SystemSettingHolder;
import me.adaptive.services.notification.error.NotificationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

/**
 * Created by panthro on 18/08/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {JpaConfiguration.class, EmailConfiguration.class})
@IntegrationTest({"SMTP_HOST=127.0.0.1", "SMTP_PORT=6060", "SMTP_USER=auser", "SMTP_PASSWORD=apassword", "SMTP_FROM=test@localhost"})
public class EmailSTMPNotificationServiceTest {


    @Autowired
    EmailSTMPNotificationService emailSTMPNotificationService;
    GreenMail mailServer;

    String destination = "test@adaptive.me";

    @Before
    public void setUp() {
        mailServer = new GreenMail(new ServerSetup(6060, null, "smtp"));
        mailServer.start();
    }

    @After
    public void tearDown() {
        mailServer.stop();
    }

    @Test
    public void testNotifyUserRegistered() throws Exception {
        notifyEvent(NotificationEvent.USER_REGISTERED);
    }

    @Test
    public void testNotifyBuildSuccessful() throws Exception {
        notifyEvent(NotificationEvent.BUILD_SUCCESSFUL);
    }

    @Test
    public void testNotifyBuildFailed() throws Exception {
        notifyEvent(NotificationEvent.BUILD_FAILED);
    }

    @Test
    public void testNotifyBuildCancelled() throws Exception {
        notifyEvent(NotificationEvent.BUILD_CANCELLED);
    }

    private void notifyEvent(String eventName) throws NotificationException {
        NotificationEntity notification = new NotificationEntity();
        notification.setChannel(NotificationChannel.EMAIL);
        notification.setStatus(NotificationStatus.CREATED);
        notification.setEvent(eventName);
        notification.setDestination(destination);
        emailSTMPNotificationService.notify(notification, new HashMap<>(SystemSettingHolder.getAll()));
    }
}