package io.github.gr3gdev.fenrir.properties;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The FenrirProperties class represents a persistent set of properties.
 * The properties are automatically replace when a placeholder is used.
 */
public class FenrirProperties extends Properties {
    private final Pattern placeholderPattern = Pattern.compile("\\$\\{.*}");

    @Override
    public synchronized Object put(Object key, Object value) {
        if (value instanceof String property) {
            final Matcher matcher = placeholderPattern.matcher(property);
            if (matcher.find()) {
                final String placeholder = property.substring(matcher.start() + 2, matcher.end() - 1);
                final String[] values = placeholder.split(":");
                final String defaultValue;
                if (values.length > 1) {
                    defaultValue = placeholder.substring(placeholder.indexOf(":") + 1);
                } else {
                    defaultValue = "";
                }
                // Update property with environment variable or system property
                return super.put(key, System.getenv().getOrDefault(
                        values[0],
                        System.getProperty(values[0],
                                defaultValue)));
            }
        }
        return super.put(key, value);
    }
}
