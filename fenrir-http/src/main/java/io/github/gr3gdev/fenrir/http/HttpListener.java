package io.github.gr3gdev.fenrir.http;

import io.github.gr3gdev.fenrir.Response;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class HttpListener {
    /**
     * Add headers to the HTTP response.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return Array of byte
     */
    protected byte[] constructResponseHeader(HttpRequest request, Response response) {
        final HttpResponse httpResponse = (HttpResponse) response;
        final List<String> headers = new ArrayList<>();
        request.header(HttpRequest.UID).ifPresent(uid -> headers.add(HttpRequest.UID + ": " + uid));
        if (httpResponse.getRedirect() != null) {
            httpResponse.setStatus(HttpStatus.FOUND);
            headers.add("Location: " + httpResponse.getRedirect());
        } else {
            headers.add("Content-Type: " + httpResponse.getContentType());
            //headers.add("Content-Length: " + httpResponse.getContent().length);
        }
        httpResponse.getCookies().forEach(cookie -> headers.add("Set-Cookie: " + cookie));
        return ("HTTP/1.1 " + httpResponse.getStatus().getCode() + "\r\n"
                + String.join("\r\n", headers) + "\r\n\r\n")
                .getBytes(StandardCharsets.UTF_8);
    }

}
