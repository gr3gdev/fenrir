package io.github.gr3gdev.fenrir.jpa.page;

import java.util.List;

/**
 * Page content of a list of entities.
 *
 * @param content          the list of entities
 * @param pageable         the page information
 * @param totalPages       the total of pages
 * @param totalElements    the number of all elements
 * @param first            true if this is the first page
 * @param last             true if this is the last page
 * @param numberOfElements the number of elements in the page
 * @param empty            true if the page is empty
 * @param <E>              the entity type
 */
public record Page<E>(List<E> content, Pageable pageable, int totalPages, long totalElements,
                      boolean first, boolean last, int numberOfElements, boolean empty) {
}
