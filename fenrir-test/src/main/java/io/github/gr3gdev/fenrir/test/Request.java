package io.github.gr3gdev.fenrir.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import lombok.Getter;

@Getter
public class Request {
    private HttpMethod method;
    private String path;
    private String body;
    private String contentType;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Request request;
        private final ObjectMapper mapper;

        private Builder() {
            this.request = new Request();
            this.mapper = new ObjectMapper();
        }

        public Builder method(HttpMethod method) {
            this.request.method = method;
            return this;
        }

        public Builder path(String path) {
            this.request.path = path;
            return this;
        }

        public Builder contentType(String contentType) {
            this.request.contentType = contentType;
            return this;
        }

        public <T> Builder body(T object) {
            try {
                this.request.body = mapper.writeValueAsString(object);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public Request build() {
            return request;
        }
    }
}
