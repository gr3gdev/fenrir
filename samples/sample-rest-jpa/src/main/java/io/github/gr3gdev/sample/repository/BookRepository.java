package io.github.gr3gdev.sample.repository;

import io.github.gr3gdev.fenrir.jpa.JpaRepository;
import io.github.gr3gdev.sample.bean.Book;

public class BookRepository implements JpaRepository<Book, Book.BookId> {
    @Override
    public Class<Book> getDomainClass() {
        return Book.class;
    }

    @Override
    public Class<Book.BookId> getIdClass() {
        return Book.BookId.class;
    }
}
