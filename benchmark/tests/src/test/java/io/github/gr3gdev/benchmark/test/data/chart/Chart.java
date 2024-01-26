package io.github.gr3gdev.benchmark.test.data.chart;

import java.util.ArrayList;
import java.util.List;

public abstract class Chart<D extends Dataset> {
    private final List<String> labels;
    protected List<D> datasets = new ArrayList<>();

    protected Chart(List<String> labels) {
        this.labels = labels;
    }

    public abstract String getType();

    public List<String> getLabels() {
        return labels;
    }

    public List<D> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<D> datasets) {
        this.datasets = datasets;
    }
}
