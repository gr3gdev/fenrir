package io.github.gr3gdev.fenrir;

import java.io.OutputStream;

public interface RouteListener {
    void handleEvent(Request request, OutputStream output);
}
