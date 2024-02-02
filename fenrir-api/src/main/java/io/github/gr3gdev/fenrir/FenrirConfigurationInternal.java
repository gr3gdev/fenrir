package io.github.gr3gdev.fenrir;

import io.github.gr3gdev.fenrir.event.SocketEvent;
import io.github.gr3gdev.fenrir.interceptor.Interceptor;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.runtime.Mode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Internal class for {@link FenrirConfiguration}, load recursively the imported configuration files.
 */
@Getter
class FenrirConfigurationInternal {
    private final List<Class<? extends Plugin>> plugins;
    private final List<Class<? extends Mode<? extends SocketEvent, ? extends Response>>> modes;
    private final List<Class<? extends Interceptor<?, ? extends Response, ? extends Plugin>>> interceptors;

    public FenrirConfigurationInternal(FenrirConfiguration annotation) {
        this.plugins = new ArrayList<>();
        this.modes = new ArrayList<>();
        this.interceptors = new ArrayList<>();
        complete(annotation);
    }

    void complete(FenrirConfiguration annotation) {
        this.plugins.addAll(Arrays.stream(annotation.plugins()).toList());
        this.modes.addAll(Arrays.stream(annotation.modes()).toList());
        this.interceptors.addAll(Arrays.stream(annotation.interceptors()).toList());
        Arrays.stream(annotation.imports())
                .forEach(c -> complete(c.getAnnotation(FenrirConfiguration.class)));
    }
}
