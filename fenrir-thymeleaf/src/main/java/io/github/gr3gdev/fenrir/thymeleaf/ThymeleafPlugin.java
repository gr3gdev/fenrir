package io.github.gr3gdev.fenrir.thymeleaf;

import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.http.HttpResponse;
import io.github.gr3gdev.fenrir.http.HttpStatus;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class ThymeleafPlugin extends Plugin<ThymeleafResponse, HttpRequest, HttpResponse> {

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
            super(thymeleafResponse.locale(), thymeleafResponse.variables());
        }
    }

    @Override
    protected Class<ThymeleafResponse> getReturnMethodClass() {
        return ThymeleafResponse.class;
    }

    @Override
    public HttpResponse process(ThymeleafResponse methodReturn, String contentType) {
        final String content = templateEngine.process(methodReturn.page(),
                new JServerContext(methodReturn));
        return HttpResponse.of(HttpStatus.OK).content(content, contentType);
    }
}
