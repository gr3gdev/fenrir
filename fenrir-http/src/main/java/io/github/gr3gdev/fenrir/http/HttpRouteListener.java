package io.github.gr3gdev.fenrir.http;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.Response;
import io.github.gr3gdev.fenrir.RouteListener;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Implementation for HTTP RouteListener.
 */
@RequiredArgsConstructor
public final class HttpRouteListener implements RouteListener {

    private final Function<HttpRequest, HttpResponse> run;

    private byte[] constructResponseHeader(Response response) {
        final HttpResponse httpResponse = (HttpResponse) response;
        final List<String> headers = new ArrayList<>();
        if (httpResponse.getRedirect() != null) {
            httpResponse.setStatus(HttpStatus.FOUND);
            headers.add("Location: " + httpResponse.getRedirect());
        } else {
            headers.add("Content-Type: " + httpResponse.getContentType());
            headers.add("Content-Length: " + httpResponse.getContent().length);
        }
        httpResponse.getCookies().forEach(cookie -> headers.add("Set-Cookie: " + cookie));
        return ("HTTP/1.1 " + httpResponse.getStatus().getCode() + "\r\n"
                + String.join("\r\n", headers) + "\r\n\r\n")
                .getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Execute RouteListener.
     *
     * @param request the request
     * @param output  output stream for HTTP response
     */
    @Override
    public void handleEvent(Request request, OutputStream output) {
        HttpRequest httpRequest = (HttpRequest) request;
        HttpResponse response = this.run.apply(httpRequest);
        try {
            output.write(constructResponseHeader(response));
            if (response.getContent().length > 0) {
                output.write(response.getContent());
            }
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
