package io.github.gr3gdev.benchmark.test.data;

import io.github.gr3gdev.benchmark.test.data.chart.Chart;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class Report {
    private final Map<String, Chart> charts = new TreeMap<>();

    public Map<String, Chart> getCharts() {
        return charts;
    }
}
