package io.github.gr3gdev.fenrir;

import io.github.gr3gdev.fenrir.event.StartupEvent;

import java.util.function.Consumer;

/**
 * Interface used for add elements to a list with lombok.
 */
interface AddStartupEvent {
    /**
     * Add an element.
     *
     * @param consumer the consumer
     */
    void add(Consumer<StartupEvent> consumer);

    /**
     * Add an element at the index.
     *
     * @param index    the index
     * @param consumer the consumer
     */
    void add(int index, Consumer<StartupEvent> consumer);
}
