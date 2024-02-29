package io.github.gr3gdev.fenrir.thymeleaf.context;

import io.github.gr3gdev.fenrir.http.HttpRequest;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.processor.IProcessor;

import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Map;

/**
 * Thymeleaf builder for resolve a custom expression.
 */
public interface IValidation {
    /**
     * Build a custom object.
     *
     * @param context the thymeleaf expression context
     * @return Object
     */
    Object buildObject(IExpressionContext context);

    /**
     * The Class match with the converter.
     *
     * @param parameterClass the class to test
     * @return boolean
     */
    boolean supports(Class<?> parameterClass);

    /**
     * Convert a parameter to Object.
     *
     * @param request         the HTTP request
     * @param parametersValue the parameters value
     * @return Object
     */
    Object convert(HttpRequest request, Map<Parameter, Object> parametersValue);

    /**
     * Return the processors defined for this validation.
     *
     * @param dialectPrefix the prefix of the thymeleaf dialect
     * @return Collection of {@link IProcessor}
     */
    Collection<? extends IProcessor> getProcessors(String dialectPrefix);
}
