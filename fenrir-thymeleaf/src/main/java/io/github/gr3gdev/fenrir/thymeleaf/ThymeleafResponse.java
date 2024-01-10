package io.github.gr3gdev.fenrir.thymeleaf;

import java.util.Locale;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ThymeleafResponse {
    private final String page;
    private final Map<String, Object> variables;
    private final Locale locale;
}
