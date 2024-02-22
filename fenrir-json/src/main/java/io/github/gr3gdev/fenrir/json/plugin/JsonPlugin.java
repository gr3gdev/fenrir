package io.github.gr3gdev.fenrir.json.plugin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.http.HttpResponse;
import io.github.gr3gdev.fenrir.http.HttpStatus;
import io.github.gr3gdev.fenrir.plugin.HttpSocketPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.function.Consumer;

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
        super();
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected HttpResponse process(Object methodReturn, HttpStatus responseCode, String contentType) {
        if (methodReturn instanceof Optional<?> optional && optional.isEmpty()) {
            return HttpResponse.of(HttpStatus.NO_CONTENT).contentType(contentType);
        } else {
            return super.process(methodReturn, responseCode, contentType);
        }
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
    protected Consumer<OutputStream> write(Object methodReturn) {
        LOGGER.trace("Convert method return to string");
        if (methodReturn != null) {
            return output -> {
                try {
                    mapper.writeValue(output, methodReturn);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
        } else {
            return output -> {
                try {
                    output.write(new byte[0]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }

    @Override
    protected String redirect(Object methodReturn) {
        return null;
    }
}
