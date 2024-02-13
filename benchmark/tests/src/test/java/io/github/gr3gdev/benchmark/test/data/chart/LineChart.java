package io.github.gr3gdev.benchmark.test.data.chart;

import io.github.gr3gdev.bench.Iteration;
import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.parameterized.IteratorSource;

import java.util.List;

public class LineChart extends Chart<Line> {
    private final String averageLabel;

    public LineChart(String key, List<String> labels, String averageLabel) {
        super(key, labels);
        this.averageLabel = averageLabel;
    }

    public void save(Framework framework, Iteration iteration, String title, float value) {
        final int index = iteration.index();
        final String fullTitle = framework.getName() + " - " +  title + " (" + iteration.memory() + "MB)";
        final Line line = datasets.stream()
                .filter(l -> l.getLabel().equals(fullTitle))
                .findFirst()
                .orElseGet(() -> {
                    final Line newLine = new Line(fullTitle, framework.getColor(iteration), iteration.max());
                    datasets.add(newLine);
                    return newLine;
                });
        line.addData(index, value);
    }

    @Override
    public String getType() {
        return "line";
    }

    public String getAverageLabel() {
        return averageLabel;
    }
}
