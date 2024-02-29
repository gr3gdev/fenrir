package io.github.gr3gdev.fenrir.thymeleaf.validation;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.WebEngineContext;

import java.util.Map;

/**
 * Record for thymeleaf validation.
 *
 * @param context the context
 * @param errors  the constraint violations
 */
public record FieldsValidation(IExpressionContext context, Map<String, String> errors) {
    /**
     * Check if an error exists for the field name.
     *
     * @param fieldName the field name
     * @return boolean
     */
    public boolean hasErrors(String fieldName) {
        if (context instanceof WebEngineContext webEngineContext) {
            webEngineContext.setSelectionTarget(errors);
        }
        return errors.containsKey(fieldName);
    }
}
