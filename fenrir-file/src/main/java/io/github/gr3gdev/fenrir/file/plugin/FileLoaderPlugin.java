package io.github.gr3gdev.fenrir.file.plugin;

import io.github.gr3gdev.fenrir.http.HttpStatus;
import io.github.gr3gdev.fenrir.plugin.HttpSocketPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * Implementation of {@link HttpSocketPlugin}, the method must return a file path (in classpath).
 */
public class FileLoaderPlugin extends HttpSocketPlugin<String> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Consumer<OutputStream> write(String methodReturn) throws HttpSocketException {
        String path = methodReturn;
        if (path.startsWith("/")) {
            path = path.substring(1);
        } else {
            path = methodReturn;
        }
        try (final InputStream resourceAsStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(path)) {
            if (resourceAsStream == null) {
                throw new HttpSocketException("File not found : " + path, HttpStatus.NOT_FOUND);
            } else {
                return out -> {
                    try {
                        resourceAsStream.transferTo(out);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                };
            }
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    protected String redirect(String methodReturn) {
        return null;
    }
}
