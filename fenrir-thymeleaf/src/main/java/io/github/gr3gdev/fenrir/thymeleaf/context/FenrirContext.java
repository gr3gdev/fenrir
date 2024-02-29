package io.github.gr3gdev.fenrir.thymeleaf.context;

import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafResponse;
import io.github.gr3gdev.fenrir.thymeleaf.context.web.FenrirWebExchange;
import io.github.gr3gdev.fenrir.thymeleaf.context.web.WebRequestAdapter;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.web.IWebExchange;

/**
 * Thymeleaf web context for Fenrir.
 */
public class FenrirContext extends AbstractContext implements IWebContext {
    private final IWebExchange exchange;

    public FenrirContext(HttpRequest request, ThymeleafResponse thymeleafResponse) {
        super(thymeleafResponse.locale(), thymeleafResponse.variables());
        this.exchange = new FenrirWebExchange(new WebRequestAdapter(request), thymeleafResponse);
    }

    @Override
    public IWebExchange getExchange() {
        return exchange;
    }
}
