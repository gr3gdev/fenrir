package io.github.gr3gdev.fenrir.http;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.RouteListener;
import io.github.gr3gdev.fenrir.plugin.HttpSocketPlugin;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * Implementation for HTTP RouteListener.
 */
@RequiredArgsConstructor
public final class HttpRouteListener extends HttpListener implements RouteListener {
    private final Function<HttpRequest, HttpResponse> run;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleEvent(Request request, OutputStream output) {
        HttpRequest httpRequest = (HttpRequest) request;
        HttpResponse response = this.run.apply(httpRequest);
        try {
            output.write(constructResponseHeader(response));
            response.getWrite().write(output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (HttpSocketPlugin.HttpSocketException exc) {
            try {
                output.write(constructResponseHeader(HttpResponse.of(exc.getReturnStatus()).contentType(response.getContentType())));
                output.write(exc.getMessage().getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
