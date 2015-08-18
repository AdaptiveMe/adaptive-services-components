package me.adaptive.services.notification.template;

import me.adaptive.core.data.domain.NotificationEntity;

import java.util.Map;

/**
 * Created by panthro on 17/08/15.
 */
public interface TemplateService {

    /**
     * Parses the template
     *
     * @param notification
     * @param model        The modelMap to be used when parsing the template
     * @return a String with the template parsed
     */
    String parseTemplate(NotificationEntity notification, Map<String, Object> model) throws TemplateParseException;

    /**
     * Some notifications require a Title
     * eg: Email Subjects, Push Notifications, etc
     *
     * @param notificationEntity
     * @param model
     * @return
     * @throws TemplateParseException
     */
    String parseTemplateTitle(NotificationEntity notificationEntity, Map<String, Object> model) throws TemplateParseException;
}
