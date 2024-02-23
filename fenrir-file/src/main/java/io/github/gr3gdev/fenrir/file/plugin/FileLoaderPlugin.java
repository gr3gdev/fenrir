package io.github.gr3gdev.fenrir.file.plugin;

import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.http.HttpStatus;
import io.github.gr3gdev.fenrir.plugin.HttpSocketPlugin;
import io.github.gr3gdev.fenrir.plugin.HttpWriter;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of {@link HttpSocketPlugin}, the method must return a file path (in classpath).
 */
public class FileLoaderPlugin extends HttpSocketPlugin<String> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected HttpWriter write(HttpRequest request, String methodReturn) {
        final String path;
        if (methodReturn.startsWith("/")) {
            path = methodReturn.substring(1);
        } else {
            path = methodReturn;
        }
        return out -> {
            try (final InputStream resourceAsStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(path)) {
                if (resourceAsStream == null) {
                    throw new HttpSocketException("File not found : " + path, HttpStatus.NOT_FOUND);
                } else {
                    try {
                        resourceAsStream.transferTo(out);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException exc) {
                throw new RuntimeException(exc);
            }
        };
    }

    @Override
    protected String redirect(String methodReturn) {
        return null;
    }
}
