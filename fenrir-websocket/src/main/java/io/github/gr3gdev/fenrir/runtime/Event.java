package io.github.gr3gdev.fenrir.runtime;

import io.github.gr3gdev.fenrir.websocket.WebSocket;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
enum Event {
    OPEN(WebSocket.Open.class), CLOSE(WebSocket.Close.class), ERROR(WebSocket.Error.class), MESSAGE(WebSocket.Message.class);

    private final Class<?> annotationClass;

    public static Event findByAnnotation(Class<?> annotationClass) {
        return Arrays.stream(Event.values())
                .filter(e -> e.annotationClass.equals(annotationClass))
                .findFirst().orElseThrow();
    }
}
