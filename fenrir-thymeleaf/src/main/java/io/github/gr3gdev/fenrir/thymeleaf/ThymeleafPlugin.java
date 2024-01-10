package io.github.gr3gdev.fenrir.thymeleaf;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import io.github.gr3gdev.fenrir.server.http.HttpStatus;
import io.github.gr3gdev.fenrir.server.http.Response;
import io.github.gr3gdev.fenrir.server.plugin.Plugin;

public class ThymeleafPlugin extends Plugin<ThymeleafResponse> {

    private static final TemplateEngine templateEngine = new TemplateEngine();

    public ThymeleafPlugin() {
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
            super(thymeleafResponse.getLocale(), thymeleafResponse.getVariables());
        }
    }

    @Override
    protected Class<ThymeleafResponse> getReturnMethodClass() {
        return ThymeleafResponse.class;
    }

    @Override
    public Response process(ThymeleafResponse methodReturn, String contentType) {
        final ThymeleafResponse thymeleafResponse = (ThymeleafResponse) methodReturn;
        final String content = templateEngine.process(thymeleafResponse.getPage(),
                new JServerContext(thymeleafResponse));
        return Response.of(HttpStatus.OK).content(content, contentType);
    }
}
