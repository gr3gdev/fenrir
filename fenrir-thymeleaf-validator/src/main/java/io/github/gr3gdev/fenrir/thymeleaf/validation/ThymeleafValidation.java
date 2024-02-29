package io.github.gr3gdev.fenrir.thymeleaf.validation;

import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.thymeleaf.context.IValidation;
import io.github.gr3gdev.fenrir.thymeleaf.validation.processor.FenrirErrorProcessor;
import jakarta.validation.*;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.web.IWebRequest;

import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Thymeleaf builder for validate a parameter.
 */
public class ThymeleafValidation implements IValidation {
    private static final Map<String, Set<ConstraintViolation<Object>>> CACHE = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Object buildObject(IExpressionContext context) {
        final Set<ConstraintViolation<Object>> violations = new HashSet<>();
        if (context instanceof WebEngineContext webEngineContext) {
            final IWebRequest request = webEngineContext.getExchange().getRequest();
            if (request.containsHeader(HttpRequest.UID)) {
                violations.addAll(Optional.ofNullable(CACHE.get(request.getHeaderValue(HttpRequest.UID)))
                        .orElse(Set.of()));
                // Purge cache
                CACHE.remove(request.getHeaderValue(HttpRequest.UID));
            }
        }
        return new FieldsValidation(context, violations.stream().collect(Collectors.toMap(
                v -> v.getPropertyPath().toString(),
                ConstraintViolation::getMessage
        )));
    }

    @Override
    public boolean supports(Class<?> parameterClass) {
        return ValidationResult.class.isAssignableFrom(parameterClass);
    }

    @Override
    public Object convert(HttpRequest request, Map<Parameter, Object> parametersValue) {
        try (final ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            final Validator validator = factory.getValidator();
            return parametersValue.entrySet().stream()
                    .filter(e -> e.getKey().isAnnotationPresent(Valid.class))
                    .map(e -> {
                        final Set<ConstraintViolation<Object>> violations = validator.validate(e.getValue());
                        request.header(HttpRequest.UID).ifPresent(uid -> CACHE.put(uid, violations));
                        return new ValidationResult(violations);
                    })
                    .findFirst()
                    .orElse(new ValidationResult(Set.of()));
        }

    }

    @Override
    public Collection<? extends IProcessor> getProcessors(String dialectPrefix) {
        return List.of(new FenrirErrorProcessor(TemplateMode.HTML, dialectPrefix));
    }
}
