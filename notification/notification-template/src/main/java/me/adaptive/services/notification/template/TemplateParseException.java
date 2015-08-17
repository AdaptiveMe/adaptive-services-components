package me.adaptive.services.notification.template;

/**
 * Created by panthro on 17/08/15.
 */
public class TemplateParseException extends Exception {

    public TemplateParseException(String message) {
        super(message);
    }

    public TemplateParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
