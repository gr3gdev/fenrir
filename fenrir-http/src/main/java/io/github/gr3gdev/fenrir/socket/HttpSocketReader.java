package io.github.gr3gdev.fenrir.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Set;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.SocketReader;
import io.github.gr3gdev.fenrir.event.SocketEvent;
import io.github.gr3gdev.fenrir.http.impl.HttpRequestImpl;

public class HttpSocketReader extends SocketReader {

    public HttpSocketReader(Socket socket, Set<SocketEvent> socketEvents) {
        super(socket, socketEvents);
    }

    @Override
    protected Request newRequest(String remoteAddress, InputStream input) {
        try {
            return new HttpRequestImpl(remoteAddress, input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
