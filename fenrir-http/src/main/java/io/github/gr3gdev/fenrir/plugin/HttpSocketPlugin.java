package io.github.gr3gdev.fenrir.plugin;

import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.http.HttpResponse;
import io.github.gr3gdev.fenrir.http.HttpStatus;
import io.github.gr3gdev.fenrir.runtime.HttpMode;
import io.github.gr3gdev.fenrir.validator.ContentTypeValidator;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * An abstract implementation of {@link SocketPlugin} for {@link HttpMode}.
 *
 * @param <T> the method return
 */
public abstract class HttpSocketPlugin<T> extends SocketPlugin<T, HttpRequest, HttpResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSocketPlugin.class);

    /**
     * Constructor.
     */
    public HttpSocketPlugin() {
        // Add the default validator for this plugin
        addValidator(new ContentTypeValidator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse process(HttpRequest request, T methodReturn, Map<String, Object> properties) {
        return process(request, methodReturn, (HttpStatus) properties.get(HttpMode.RESPONSE_CODE), (String) properties.get(HttpMode.CONTENT_TYPE));
    }

    /**
     * Convert the method's return to a String value to be written in the response.
     *
     * @param request         the current request
     * @param methodReturn    the method return
     * @return Consumer with the output stream in parameter
     */
    protected abstract HttpWriter write(HttpRequest request, T methodReturn);

    /**
     * Get the path for the redirection.
     *
     * @param methodReturn the method return
     * @return String
     */
    protected abstract String redirect(T methodReturn);

    protected HttpResponse process(HttpRequest request, T methodReturn, HttpStatus responseCode, String contentType) {
        final String redirect = redirect(methodReturn);
        if (redirect != null) {
            return HttpResponse.of(responseCode).redirect(redirect);
        } else {
            return HttpResponse.of(responseCode).content(write(request, methodReturn), contentType);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HttpResponse processInternalError(Map<String, Object> properties, Exception exception) {
        LOGGER.error("Internal error", exception);
        return HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType((String) properties.get(HttpMode.CONTENT_TYPE));
    }

    /**
     * An exception to throw when method return conversion is impossible or invalid.
     */
    @Getter
    public static class HttpSocketException extends Exception {
        private final HttpStatus returnStatus;

        /**
         * Constructor.
         *
         * @param message      the error message
         * @param returnStatus the Http status to use
         */
        public HttpSocketException(String message, HttpStatus returnStatus) {
            super(message);
            this.returnStatus = returnStatus;
        }
    }
}
