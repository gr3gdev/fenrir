package io.github.gr3gdev.fenrir.socket;

import io.github.gr3gdev.fenrir.ErrorListener;
import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.RouteListener;
import io.github.gr3gdev.fenrir.SocketReader;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentMap;

public class WebSocketReader extends SocketReader {

    public WebSocketReader(String firstLine, BufferedReader bufferedReader, OutputStream outputStream, SocketAddress remoteAddress, ConcurrentMap<Request, RouteListener> listeners, ErrorListener errorListener) {
        super(firstLine, bufferedReader, outputStream, remoteAddress, listeners, errorListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Request newRequest(String firstLine, String remoteAddress, BufferedReader bufferedReader) {
        return null;
    }
}
