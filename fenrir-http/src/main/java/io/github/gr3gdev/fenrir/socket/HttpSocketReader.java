package io.github.gr3gdev.fenrir.socket;

import io.github.gr3gdev.fenrir.ErrorListener;
import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.RouteListener;
import io.github.gr3gdev.fenrir.SocketReader;
import io.github.gr3gdev.fenrir.http.impl.HttpRequestImpl;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentMap;

public class HttpSocketReader extends SocketReader {
    /**
     * Constructor.
     *
     * @param firstLine      the first line of the input stream
     * @param bufferedReader the buffered reader of the socket input stream
     * @param outputStream   the socket output stream
     * @param remoteAddress  the remote address
     * @param listeners      the listeners to execute
     * @param errorListener  the error listener
     */
    public HttpSocketReader(String firstLine, BufferedReader bufferedReader, OutputStream outputStream, SocketAddress remoteAddress, ConcurrentMap<Request, RouteListener> listeners, ErrorListener errorListener) {
        super(firstLine, bufferedReader, outputStream, remoteAddress, listeners, errorListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Request newRequest(String firstLine, String remoteAddress, BufferedReader bufferedReader) {
        return new HttpRequestImpl(firstLine, remoteAddress, bufferedReader);
    }

}
