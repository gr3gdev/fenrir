package io.github.gr3gdev.fenrir.server.http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

/**
 * RouteListener.
 *
 * @author Gregory Tardivel
 */
@RequiredArgsConstructor
public final class RouteListener {

    private final Function<Request, Response> run;

    private byte[] constructResponseHeader(Response response) {
        final List<String> headers = new ArrayList<>();
        if (response.getRedirect() != null) {
            response.setStatus(HttpStatus.FOUND);
            headers.add("Location: " + response.getRedirect());
        } else {
            headers.add("Content-Type: " + response.getContentType());
            headers.add("Content-Length: " + response.getContent().length);
        }
        response.getCookies().forEach(cookie -> headers.add("Set-Cookie: " + cookie));
        return ("HTTP/1.1 " + response.getStatus().getCode() + "\r\n"
                + String.join("\r\n", headers) + "\r\n\r\n")
                .getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Execute RouteListener.
     *
     * @param request HTTP Request
     * @param output  Output stream for HTTP response
     */
    public void handleEvent(Request request, OutputStream output) {
        Response response = this.run.apply(request);
        try {
            output.write(constructResponseHeader(response));
            if (response.getContent().length > 0) {
                output.write(response.getContent());
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
