package io.github.gr3gdev.benchmark.test.data.chart;

import java.util.List;

public class BarChart extends Chart<Bar> {
    public BarChart(List<String> labels) {
        super(labels);
    }

    @Override
    public String getType() {
        return "bar";
    }
}
