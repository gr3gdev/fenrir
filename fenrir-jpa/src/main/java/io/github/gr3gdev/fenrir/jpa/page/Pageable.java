package io.github.gr3gdev.fenrir.jpa.page;

/**
 * Page information.
 *
 * @param pageNumber the page number
 * @param pageSize   the page size
 * @param sorts      the sorts
 * @param offset     the offset
 */
public record Pageable(int pageNumber, int pageSize, Sort[] sorts, int offset) {
}
