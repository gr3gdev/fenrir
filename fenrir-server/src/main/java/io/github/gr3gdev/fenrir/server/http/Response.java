package io.github.gr3gdev.fenrir.server.http;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Response.
 *
 * @author Gregory Tardivel
 */
@Getter
public class Response {

    private byte[] content;
    @Setter
    private HttpStatus status;
    private String contentType;
    private String redirect;
    private final Set<Cookie> cookies = new HashSet<>();

    private Response() {
        content = new byte[0];
        contentType = "text/html";
    }

    /**
     * Static constructor of Response.
     * 
     * @param status Http status
     * @return Response
     */
    public static Response of(HttpStatus status) {
        final Response response = new Response();
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
    public Response content(String text, String contentType) {
        this.content = text.getBytes(StandardCharsets.UTF_8);
        this.contentType = contentType;
        return this;
    }

    /**
     * Response from file.
     * 
     * @param pathFile    The path of the file
     * @param contentType The content-type of the file
     * @return Response
     */
    public Response file(String pathFile, String contentType) {
        final File file = new File(pathFile, contentType);
        this.contentType = file.contentType;
        try {
            this.content = file.content();
            if (this.content == null) {
                this.status = HttpStatus.NOT_FOUND;
                this.content = file.path.getBytes(StandardCharsets.UTF_8);
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        return this;
    }

    /**
     * Add a cookie.
     *
     * @param cookie Cookie
     * @return Response
     */
    public Response cookie(Cookie cookie) {
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

    static class File {

        private final String path;
        private final String contentType;

        public File(String path, String contentType) {
            if (path.startsWith("/")) {
                this.path = path.substring(1);
            } else {
                this.path = path;
            }
            this.contentType = contentType;
        }

        private byte[] content() throws IOException {
            final InputStream resourceAsStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(path);
            if (resourceAsStream == null) {
                System.out.println("Content not found : " + path);
                return null;
            } else {
                return resourceAsStream.readAllBytes();
            }
        }
    }

}
