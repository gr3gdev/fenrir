package io.github.gr3gdev.fenrir.thymeleaf;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Thymeleaf response.
 */
public record ThymeleafResponse(String page, Map<String, Object> variables, Locale locale, boolean redirection) {
    public ThymeleafResponse(String page) {
        this(page, new HashMap<>(), Locale.UK, false);
    }

    public ThymeleafResponse(String page, boolean redirection) {
        this(page, new HashMap<>(), Locale.UK, redirection);
    }

    public ThymeleafResponse(String page, Map<String, Object> variables) {
        this(page, new HashMap<>(variables), Locale.UK, false);
    }

    public ThymeleafResponse(String page, Map<String, Object> variables, Locale locale) {
        this(page, new HashMap<>(variables), locale, false);
    }
}
