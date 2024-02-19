package io.github.gr3gdev.fenrir.jpa.page;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The sort used on the page.
 *
 * @param by        sorted by
 * @param direction direction of the sort
 */
public record Sort(String by, Direction direction) {

    @RequiredArgsConstructor
    @Getter
    public enum Direction {
        ASC(true), DESC(false);

        private final boolean asc;
    }
}
