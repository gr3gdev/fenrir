package io.github.gr3gdev.fenrir.http;

import io.github.gr3gdev.fenrir.ErrorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Implementation for HTTP ErrorListener.
 */
public class HttpErrorListener extends HttpListener implements ErrorListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpErrorListener.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleEvent(OutputStream output, String message) {
        LOGGER.error(message);
        final HttpResponse response = HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR);
        try {
            output.write(constructResponseHeader(response));
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
