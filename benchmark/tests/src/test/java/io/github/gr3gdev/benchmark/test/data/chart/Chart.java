package io.github.gr3gdev.benchmark.test.data.chart;

import java.util.*;

public class Chart {
    private final String key;
    private final String cssClass;
    private final String title;
    private final Map<String, List<Value>> dataset = new TreeMap<>();

    public Chart(String key, String cssClass, String caption) {
        this.key = key;
        this.cssClass = cssClass;
        this.title = caption;
    }

    public String getKey() {
        return key;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getTitle() {
        return title;
    }

    public void addDataset(String key, Value value) {
        final List<Value> data = dataset.computeIfAbsent(key, k -> new ArrayList<>());
        data.add(value);
        data.sort(Comparator.comparing(Value::legend));
    }

    public Map<String, List<Value>> getDataset() {
        return dataset;
    }

    public record Value(Double size, String value, String legend, String tooltip) {
    }
}
