package io.github.gr3gdev.fenrir;

import java.util.List;

import io.github.gr3gdev.fenrir.event.SocketEvent;

public interface AddSocketEvent {
    void addEvents(List<SocketEvent> socketEvents, Class<? extends SocketReader> socketReaderClass);
}
