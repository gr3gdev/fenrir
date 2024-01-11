package io.github.gr3gdev.fenrir;

import java.util.function.Consumer;

import io.github.gr3gdev.fenrir.event.StartupEvent;

public interface AddStartupEvent {
    void add(Consumer<StartupEvent> consumer);
    void add(int index, Consumer<StartupEvent> consumer);
}
