package io.github.gr3gdev.benchmark.test.data;

import io.github.gr3gdev.benchmark.test.data.chart.Chart;

import java.util.LinkedHashMap;
import java.util.Map;

public class Report {
    private final Map<String, Chart<?>> charts = new LinkedHashMap<>();

    public Map<String, Chart<?>> getCharts() {
        return charts;
    }
}
