package io.github.gr3gdev.benchmark.test.data.chart;

import java.util.ArrayList;
import java.util.List;

public class Bar implements Dataset {
    private final String label;
    private List<Double> data = new ArrayList<>();
    private int borderWidth = 1;

    public Bar(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }
}
