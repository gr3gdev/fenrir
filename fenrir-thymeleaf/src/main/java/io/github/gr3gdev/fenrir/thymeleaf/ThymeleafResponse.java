package io.github.gr3gdev.fenrir.thymeleaf;

import java.util.Locale;
import java.util.Map;

public record ThymeleafResponse(String page, Map<String, Object> variables, Locale locale) {
}
