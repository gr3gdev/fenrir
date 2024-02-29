package io.github.gr3gdev.fenrir.thymeleaf.context.dialect;

import io.github.gr3gdev.fenrir.thymeleaf.context.IValidation;
import io.github.gr3gdev.fenrir.thymeleaf.context.expression.FenrirExpressionObjectFactory;
import io.github.gr3gdev.fenrir.thymeleaf.context.processor.FenrirFieldProcessor;
import lombok.Getter;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Custom dialect for override {@link StandardDialect}.
 */
@Getter
public class FenrirDialect extends StandardDialect {
    private final Map<String, IValidation> validations = new HashMap<>();

    /**
     * Constructor.
     */
    public FenrirDialect() {
        super("FenrirStandard", StandardDialect.PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
    }

    /**
     * Add a validation.
     *
     * @param name       validation's name
     * @param validation the implementation of {@link IValidation}
     */
    public void addValidation(String name, IValidation validation) {
        this.validations.put(name, validation);
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        final Set<IProcessor> processors = super.getProcessors(dialectPrefix);
        processors.add(new FenrirFieldProcessor(TemplateMode.HTML, dialectPrefix));
        this.validations.values().forEach(v -> processors.addAll(v.getProcessors(dialectPrefix)));
        return processors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        if (this.expressionObjectFactory == null) {
            this.expressionObjectFactory = new FenrirExpressionObjectFactory(validations);
        }
        return this.expressionObjectFactory;
    }
}
