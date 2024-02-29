package io.github.gr3gdev.fenrir.thymeleaf;

import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.plugin.HttpSocketPlugin;
import io.github.gr3gdev.fenrir.plugin.HttpWriter;
import io.github.gr3gdev.fenrir.thymeleaf.context.FenrirContext;
import io.github.gr3gdev.fenrir.thymeleaf.context.dialect.FenrirDialect;
import io.github.gr3gdev.fenrir.thymeleaf.messages.FenrirMessageResolver;
import io.github.gr3gdev.fenrir.validator.RouteValidator;
import io.github.gr3gdev.fenrir.validator.ValidatorException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.OutputStreamWriter;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link HttpSocketPlugin}, execute {@link TemplateEngine} for thymeleaf rendering.
 */
public class ThymeleafPlugin extends HttpSocketPlugin<ThymeleafResponse> {

    protected final TemplateEngine templateEngine;
    protected final FenrirDialect dialect;

    /**
     * Default constructor.
     */
    public ThymeleafPlugin() {
        super();
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver(
                Thread.currentThread().getContextClassLoader());
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(3600000L); // 1h by default
        templateResolver.setCacheable(true);
        templateResolver.setCheckExistence(true);
        templateEngine = new TemplateEngine();
        dialect = new FenrirDialect();
        templateEngine.setDialect(dialect);
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setMessageResolver(new FenrirMessageResolver());
    }

    @Override
    protected Object[] validateParameters(HttpRequest request, Map<String, Object> properties, List<RouteValidator> validators,
                                          Map<Parameter, Object> mapParameterValues) throws ValidatorException {
        if (!dialect.getValidations().isEmpty()) {
            Object[] parameters = new Object[mapParameterValues.size()];
            dialect.getValidations().values().forEach(v -> {
                int idx = 0;
                for (final Map.Entry<Parameter, Object> entry : mapParameterValues.entrySet()) {
                    final Parameter parameter = entry.getKey();
                    final Object parameterValue = entry.getValue();
                    if (v.supports(parameter.getType())) {
                        parameters[idx] = v.convert(request, mapParameterValues);
                    } else {
                        parameters[idx] = parameterValue;
                    }
                    idx++;
                }
            });
            return parameters;
        } else {
            return super.validateParameters(request, properties, validators, mapParameterValues);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HttpWriter write(HttpRequest request, ThymeleafResponse methodReturn) {
        final FenrirContext context = new FenrirContext(request, methodReturn);
        return output -> templateEngine.process(methodReturn.page(), context, new OutputStreamWriter(output));
    }

    @Override
    protected String redirect(ThymeleafResponse methodReturn) {
        if (methodReturn.redirection()) {
            return methodReturn.page();
        } else {
            return null;
        }
    }
}
