package io.github.gr3gdev.fenrir.plugin.impl;

import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.http.HttpResponse;
import io.github.gr3gdev.fenrir.http.HttpStatus;
import io.github.gr3gdev.fenrir.plugin.SocketPlugin;
import io.github.gr3gdev.fenrir.runtime.HttpMode;
import lombok.Getter;

import java.util.Map;

public abstract class HttpSocketPlugin<T> extends SocketPlugin<T, HttpRequest, HttpResponse> {

    @Override
    public HttpResponse process(T methodReturn, Map<String, Object> properties) {
        return process(methodReturn, (HttpStatus) properties.get(HttpMode.RESPONSE_CODE), (String) properties.get(HttpMode.CONTENT_TYPE));
    }

    protected abstract String toString(T methodReturn) throws HttpSocketException;

    HttpResponse process(T methodReturn, HttpStatus responseCode, String contentType) {
        try {
            return HttpResponse.of(responseCode).content(toString(methodReturn), contentType);
        } catch (HttpSocketException exc) {
            return HttpResponse.of(exc.getReturnStatus()).content(exc.getMessage(), contentType);
        }
    }

    @Getter
    public static class HttpSocketException extends Exception {
        private final HttpStatus returnStatus;

        public HttpSocketException(String message, HttpStatus returnStatus) {
            super(message);
            this.returnStatus = returnStatus;
        }
    }
}
