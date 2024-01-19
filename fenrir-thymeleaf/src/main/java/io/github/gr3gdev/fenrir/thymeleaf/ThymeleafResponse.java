package io.github.gr3gdev.fenrir.thymeleaf;

import java.util.Locale;
import java.util.Map;

/**
 * Thymeleaf response.
 *
 * @param page      the page path (in classpath)
 * @param variables the variables (replace value in page)
 * @param locale    the locale (i18n)
 */
public record ThymeleafResponse(String page, Map<String, Object> variables, Locale locale) {
}
