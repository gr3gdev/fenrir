package io.github.gr3gdev.fenrir.plugin.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gr3gdev.fenrir.http.HttpRequest;

import java.lang.reflect.Parameter;

public class JsonPlugin extends HttpSocketPlugin<Object> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected Object extractBody(Parameter parameter, HttpRequest request) {
        return request.param("body").map(b -> {
            try {
                return mapper.readValue(b, parameter.getType());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).orElse(null);
    }

    @Override
    protected String toString(Object methodReturn) {
        if (methodReturn != null) {
            try {
                return mapper.writeValueAsString(methodReturn);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return "";
        }
    }
}
