package io.github.gr3gdev.fenrir.plugin.impl;

import io.github.gr3gdev.fenrir.http.HttpStatus;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.http.HttpResponse;

public class FileLoaderPlugin extends Plugin<String, HttpRequest, HttpResponse> {
    @Override
    protected Class<String> getReturnMethodClass() {
        return String.class;
    }

    @Override
    public HttpResponse process(String methodReturn, String contentType) {
        return HttpResponse.of(HttpStatus.OK).file(methodReturn, contentType);
    }
}
