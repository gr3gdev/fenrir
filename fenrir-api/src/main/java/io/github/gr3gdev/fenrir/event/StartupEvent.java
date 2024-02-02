package io.github.gr3gdev.fenrir.event;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The event send when the server is started.
 */
public record StartupEvent(int port, AtomicBoolean status) {

}
