package io.github.gr3gdev.fenrir.socket;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.SocketReader;
import io.github.gr3gdev.fenrir.event.SocketEvent;
import io.github.gr3gdev.fenrir.http.impl.HttpRequestImpl;

import java.io.InputStream;
import java.net.Socket;
import java.util.Set;

public class HttpSocketReader extends SocketReader {

    /**
     * Constructor.
     *
     * @param socket       the server socket
     * @param socketEvents the socket events
     */
    public HttpSocketReader(Socket socket, Set<SocketEvent> socketEvents) {
        super(socket, socketEvents);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Request newRequest(String remoteAddress, InputStream input) {
        return new HttpRequestImpl(remoteAddress, input);
    }

}
