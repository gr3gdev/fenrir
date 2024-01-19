package io.github.gr3gdev.fenrir.http;

import io.github.gr3gdev.fenrir.Logger;
import io.github.gr3gdev.fenrir.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation for HTTP response.
 */
@Getter
public class HttpResponse implements Response {

    private static final Logger LOGGER = new Logger("Fenrir.HttpResponse");

    private byte[] content;
    @Setter
    private HttpStatus status;
    private String contentType;
    private String redirect;
    private final Set<Cookie> cookies = new HashSet<>();

    private HttpResponse() {
        content = new byte[0];
        contentType = "text/html";
    }

    /**
     * Static constructor of Response.
     *
     * @param status Http status
     * @return Response
     */
    public static HttpResponse of(HttpStatus status) {
        final HttpResponse response = new HttpResponse();
        response.status = status;
        return response;
    }

    /**
     * Response from text.
     *
     * @param text        The text
     * @param contentType The content-type of the text
     * @return Response
     */
    public HttpResponse content(String text, String contentType) {
        this.content = text.getBytes(StandardCharsets.UTF_8);
        this.contentType = contentType;
        return this;
    }

    /**
     * Add a cookie.
     *
     * @param cookie Cookie
     * @return Response
     */
    public HttpResponse cookie(Cookie cookie) {
        cookies.add(cookie);
        return this;
    }

    /**
     * Create a cookie.
     *
     * @param name  The name of the cookie
     * @param value The value
     * @return Cookie
     */
    public static Cookie createCookie(String name, String value) {
        return new Cookie(name, value);
    }

    enum CookieSameSite {
        STRICT, LAX, NONE;

        public String value() {
            return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
        }
    }

    /**
     * HTTP Cookie.
     */
    @RequiredArgsConstructor
    @Getter
    @Setter
    public static class Cookie {
        private final String name;
        private final String value;
        private int maxAge = 3600;
        private String domain = null;
        private String path = null;
        private Boolean secure = false;
        private Boolean httpOnly = true;
        private CookieSameSite sameSite = CookieSameSite.LAX;

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder(name)
                    .append("=").append(value).append("; ")
                    .append("Max-Age=").append(maxAge);
            if (domain != null) {
                builder.append("; Domain=").append(domain);
            }
            if (path != null) {
                builder.append("; Path=").append(path);
            }
            if (Boolean.TRUE.equals(secure)) {
                builder.append("; Secure");
            }
            if (Boolean.TRUE.equals(httpOnly)) {
                builder.append("; HttpOnly");
            }
            builder.append("; SameSite=").append(sameSite.value());
            return builder.toString();
        }
    }


}
