package io.github.gr3gdev.fenrir.plugin;

import java.io.OutputStream;

@FunctionalInterface
public interface HttpWriter {
    /**
     * Write a response into the socket output stream.
     *
     * @param outputStream the socket output stream
     * @throws HttpSocketPlugin.HttpSocketException exception thrown when the conversion is impossible or invalid
     */
    void write(OutputStream outputStream) throws HttpSocketPlugin.HttpSocketException;
}
