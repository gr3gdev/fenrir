package io.github.gr3gdev.fenrir.server;

import java.util.function.Consumer;

public interface AddStartupEvent {
    void add(Consumer<StartupEvent> consumer);
    void add(int index, Consumer<StartupEvent> consumer);
}
