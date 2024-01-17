package io.github.gr3gdev.fenrir.plugin;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.Response;

import java.util.Map;

public class TestPlugin extends SocketPlugin<String, Request, Response> {

    @Override
    public Response process(String methodReturn, Map<String, Object> properties) {
        return null;
    }
}
