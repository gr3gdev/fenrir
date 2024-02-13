package io.github.gr3gdev.fenrir.thymeleaf;

import io.github.gr3gdev.fenrir.plugin.HttpSocketPlugin;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;

/**
 * Implementation of {@link HttpSocketPlugin}, execute {@link TemplateEngine} for thymeleaf rendering.
 */
public class ThymeleafPlugin extends HttpSocketPlugin<ThymeleafResponse> {

    private static final TemplateEngine templateEngine = new TemplateEngine();

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
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setMessageResolver(new StandardMessageResolver());
    }

    private static final class JServerContext extends AbstractContext {

        public JServerContext(ThymeleafResponse thymeleafResponse) {
            super(thymeleafResponse.locale(), thymeleafResponse.variables());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected byte[] toBytes(ThymeleafResponse methodReturn) {
        return templateEngine.process(methodReturn.page(),
                        new JServerContext(methodReturn))
                .getBytes(StandardCharsets.UTF_8);
    }
}
