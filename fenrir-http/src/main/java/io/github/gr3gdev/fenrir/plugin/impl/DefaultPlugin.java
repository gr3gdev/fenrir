package io.github.gr3gdev.fenrir.plugin.impl;

public class DefaultPlugin extends HttpSocketPlugin<String> {

    @Override
    protected String toString(String methodReturn) {
        return methodReturn;
    }
}
