package io.github.gr3gdev.fenrir.thymeleaf;

import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.plugin.HttpSocketPlugin;
import io.github.gr3gdev.fenrir.plugin.HttpWriter;
import io.github.gr3gdev.fenrir.thymeleaf.context.FenrirWebExchange;
import io.github.gr3gdev.fenrir.thymeleaf.context.WebRequestAdapter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.web.IWebExchange;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.function.Consumer;

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

    private static final class JServerContext extends AbstractContext implements IWebContext {

        private final IWebExchange exchange;

        public JServerContext(HttpRequest request, ThymeleafResponse thymeleafResponse) {
            super(thymeleafResponse.locale(), thymeleafResponse.variables());
            exchange = new FenrirWebExchange(new WebRequestAdapter(request), thymeleafResponse);
        }

        @Override
        public IWebExchange getExchange() {
            return exchange;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HttpWriter write(HttpRequest request, ThymeleafResponse methodReturn) {
        return output -> templateEngine.process(methodReturn.page(),
                new JServerContext(request, methodReturn),
                new OutputStreamWriter(output));
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
