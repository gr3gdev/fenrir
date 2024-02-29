package io.github.gr3gdev.fenrir.thymeleaf.validation.el;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;

/**
 * Implementation of {@link ExpressionFactory}.
 */
public class FenrirExpressionFactory extends ExpressionFactory {
    @Override
    public ValueExpression createValueExpression(ELContext context, String expression, Class<?> expectedType) {
        return null;
    }

    @Override
    public ValueExpression createValueExpression(Object instance, Class<?> expectedType) {
        return null;
    }

    @Override
    public MethodExpression createMethodExpression(ELContext context, String expression, Class<?> expectedReturnType, Class<?>[] expectedParamTypes) {
        return null;
    }

    @Override
    public <T> T coerceToType(Object obj, Class<T> targetType) {
        return null;
    }
}
