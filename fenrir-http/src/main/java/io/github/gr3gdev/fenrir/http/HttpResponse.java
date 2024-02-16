package io.github.gr3gdev.fenrir.http;

import io.github.gr3gdev.fenrir.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Implementation for HTTP response.
 */
@Getter
public class HttpResponse implements Response {

    private Consumer<OutputStream> write;
    @Setter
    private HttpStatus status;
    private String redirect;
    private String contentType;
    private final Set<Cookie> cookies = new HashSet<>();

    private HttpResponse() {
        write = (output) -> {
            try {
                output.write(new byte[0]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
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
     * Specify a method to write a Response.
     *
     * @param write       The functional interface called to write content
     * @param contentType The content-type of the response
     * @return Response
     */
    public HttpResponse content(Consumer<OutputStream> write, String contentType) {
        this.write = write;
        this.contentType = contentType;
        return this;
    }

    /**
     * Define a content-type.
     *
     * @param contentType The content-type of the response
     * @return Response
     */
    public HttpResponse contentType(String contentType) {
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
