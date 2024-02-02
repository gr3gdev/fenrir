package io.github.gr3gdev.fenrir.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.validator.JsonValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation {@link HttpSocketPlugin}, convert return method and parameters into JSON.
 */
public class JsonPlugin extends HttpSocketPlugin<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonPlugin.class);

    private final ObjectMapper mapper;

    /**
     * Constructor.
     */
    public JsonPlugin() {
        mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        // Add the default validator for this plugin
        addValidator(new JsonValidator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object extractBody(Class<?> parameterClass, HttpRequest request) {
        LOGGER.trace("Extract body and convert into parameter {}", parameterClass.getCanonicalName());
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
        LOGGER.trace("Convert method return to string");
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
