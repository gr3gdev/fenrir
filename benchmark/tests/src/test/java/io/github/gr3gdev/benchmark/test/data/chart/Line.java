package io.github.gr3gdev.benchmark.test.data.chart;

import java.util.ArrayList;
import java.util.List;

public class Line implements Dataset {
    private final String label;
    private final List<Float> data;
    private boolean fill = false;
    private final String borderColor;
    private Float tension = 0.1f;

    public Line(String label, String borderColor, int size) {
        this.label = label;
        this.borderColor = borderColor;
        this.data = new ArrayList<>(size);
    }

    public String getLabel() {
        return label;
    }

    public List<Float> getData() {
        return data;
    }

    public boolean isFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public Float getTension() {
        return tension;
    }

    public void setTension(Float tension) {
        this.tension = tension;
    }

    public void addData(int index, float value) {
        this.data.add(index, value);
    }
}
