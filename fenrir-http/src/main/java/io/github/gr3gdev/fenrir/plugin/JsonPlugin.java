package io.github.gr3gdev.fenrir.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gr3gdev.fenrir.http.HttpRequest;

/**
 * Implementation {@link HttpSocketPlugin}, convert return method and parameters into JSON.
 */
public class JsonPlugin extends HttpSocketPlugin<Object> {
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object extractBody(Class<?> parameterClass, HttpRequest request) {
        return request.param("body").map(b -> {
            try {
                return mapper.readValue(b, parameterClass);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
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
