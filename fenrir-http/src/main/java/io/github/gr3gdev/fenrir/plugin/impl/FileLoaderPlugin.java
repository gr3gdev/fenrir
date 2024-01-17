package io.github.gr3gdev.fenrir.plugin.impl;

import io.github.gr3gdev.fenrir.Logger;
import io.github.gr3gdev.fenrir.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileLoaderPlugin extends HttpSocketPlugin<String> {

    @Override
    protected String toString(String methodReturn) throws HttpSocketException {
        final File file = new File(methodReturn);
        try {
            byte[] content = file.content();
            if (content == null) {
                throw new HttpSocketException("File not found : " + file.path, HttpStatus.NOT_FOUND);
            }
            return new String(content, StandardCharsets.UTF_8);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    static class File {

        private static final Logger LOGGER = new Logger("Fenrir.FileLoaderPlugin.File");

        private final String path;

        public File(String path) {
            if (path.startsWith("/")) {
                this.path = path.substring(1);
            } else {
                this.path = path;
            }
        }

        private byte[] content() throws IOException {
            try (final InputStream resourceAsStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(path)) {
                if (resourceAsStream == null) {
                    LOGGER.warn("Content not found : {0}", path);
                    return null;
                } else {
                    return resourceAsStream.readAllBytes();
                }
            }
        }
    }
}
