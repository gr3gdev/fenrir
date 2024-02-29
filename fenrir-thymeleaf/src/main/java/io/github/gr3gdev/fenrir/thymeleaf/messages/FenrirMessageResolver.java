package io.github.gr3gdev.fenrir.thymeleaf.messages;

import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templateresource.ClassLoaderTemplateResource;
import org.thymeleaf.templateresource.ITemplateResource;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

/**
 * Message resolver for Thymeleaf.
 */
public class FenrirMessageResolver extends StandardMessageResolver {
    private final ITemplateResource commonTemplateResource = new ClassLoaderTemplateResource("/messages", StandardCharsets.UTF_8.toString());

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, String> resolveMessagesForTemplate(String template, ITemplateResource templateResource, Locale locale) {
        final Map<String, String> messages = super.resolveMessagesForTemplate(template, commonTemplateResource, locale);
        if (messages.isEmpty()) {
            return super.resolveMessagesForTemplate(template, templateResource, locale);
        } else {
            return messages;
        }
    }
}
