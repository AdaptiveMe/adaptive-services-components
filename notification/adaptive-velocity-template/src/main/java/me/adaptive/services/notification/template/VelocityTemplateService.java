package me.adaptive.services.notification.template;

import me.adaptive.core.data.domain.NotificationEntity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by panthro on 17/08/15.
 */
@Service
public class VelocityTemplateService implements TemplateService {

    @Autowired
    VelocityEngine velocityEngine;


    private String getTemplateLocation(NotificationEntity notification) {
        return notification.getChannel().name().toLowerCase() + File.separator + notification.getEvent().name().toLowerCase() + ".vm";
    }

    @Override
    public String parseTemplate(NotificationEntity notification, Map<String, Object> model) throws TemplateParseException {
        if (model == null) {
            model = new HashMap<>();
        }
        model.put("notification", notification);
        try {
            return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, getTemplateLocation(notification), "UTF-8", model);
        } catch (Exception e) {
            throw new TemplateParseException("Error parsing email for notification", e);
        }


    }
}
