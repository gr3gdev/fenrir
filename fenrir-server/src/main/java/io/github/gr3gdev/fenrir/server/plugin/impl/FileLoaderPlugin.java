package io.github.gr3gdev.fenrir.server.plugin.impl;

import io.github.gr3gdev.fenrir.server.http.HttpStatus;
import io.github.gr3gdev.fenrir.server.http.Response;
import io.github.gr3gdev.fenrir.server.plugin.Plugin;

public class FileLoaderPlugin extends Plugin<String> {
    @Override
    protected Class<String> getReturnMethodClass() {
        return String.class;
    }

    @Override
    public Response process(String methodReturn, String contentType) {
        return Response.of(HttpStatus.OK).file((String) methodReturn, contentType);
    }
}
