package io.github.gr3gdev.bench;

public record Iteration(int index, int max, long memory) {
    @Override
    public String toString() {
        final int increment = index + 1;
        return increment + ", memory=" + memory + "MB";
    }
}
