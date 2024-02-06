package io.github.gr3gdev.benchmark.test.data.chart;

import java.util.List;

public class BarChart extends Chart<Bar> {
    public BarChart(String key, List<String> labels) {
        super(key, labels);
    }

    @Override
    public String getType() {
        return "bar";
    }
}
