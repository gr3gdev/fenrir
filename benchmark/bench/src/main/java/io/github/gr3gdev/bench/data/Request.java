package io.github.gr3gdev.bench.data;

public record Request(String uid, int order, String method, String path, String body) {
    public Request(int order, String method, String path, String body) {
        this(String.valueOf((method + path + order).hashCode()),
                order, method, path, body);
    }

    public String name() {
        return method + " " + path;
    }

    @Override
    public String toString() {
        return "Request{" +
                "uid='" + uid + '\'' +
                ", order=" + order +
                ", method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
