package io.github.gr3gdev.fenrir.thymeleaf.context.expression;

import io.github.gr3gdev.fenrir.thymeleaf.context.IValidation;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Adapter for {@link StandardExpressionObjectFactory}.
 */
public class FenrirExpressionObjectFactory extends StandardExpressionObjectFactory {
    private final Map<String, IValidation> validations;
    private final Set<String> allExpressionObjectNames;

    public FenrirExpressionObjectFactory(Map<String, IValidation> validations) {
        super();
        this.validations = validations;
        this.allExpressionObjectNames = Stream.concat(validations.keySet().stream(),
                        StandardExpressionObjectFactory.ALL_EXPRESSION_OBJECT_NAMES.stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAllExpressionObjectNames() {
        return this.allExpressionObjectNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object buildObject(IExpressionContext context, String expressionObjectName) {
        return Optional.ofNullable(validations.get(expressionObjectName))
                .map(expressionBuilder -> expressionBuilder.buildObject(context))
                .orElse(super.buildObject(context, expressionObjectName));
    }
}
