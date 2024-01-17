package io.github.gr3gdev.fenrir.samples.jpa.route;

import io.github.gr3gdev.fenrir.annotation.Body;
import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Param;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.plugin.impl.JsonPlugin;
import io.github.gr3gdev.fenrir.samples.jpa.bean.Book;
import io.github.gr3gdev.fenrir.samples.jpa.repository.BookRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Route(plugin = JsonPlugin.class, path = "/book")
@RequiredArgsConstructor
public class BookRestRoute {

    private final BookRepository repository;

    @Listener(path = "/")
    public List<Book> findAll() {
        return repository.findAll();
    }

    @Listener(path = "/{title}/{language}")
    public Optional<Book> findById(@Param("title") String title, @Param("language") String language) {
        return repository.findById(new Book.BookId(title, language));
    }

    @Listener(path = "/", method = HttpMethod.POST)
    public Book create(@Body Book book) {
        return repository.save(book);
    }

    @Listener(path = "/", method = HttpMethod.PUT)
    public Book update(@Body Book book) {
        return repository.save(book);
    }

    @Listener(path = "/", method = HttpMethod.DELETE)
    public void delete(@Body Book book) {
        repository.delete(book);
    }

    @Listener(path = "/{title}/{language}", method = HttpMethod.DELETE)
    public void deleteById(@Param("title") String title, @Param("language") String language) {
        repository.deleteById(new Book.BookId(title, language));
    }
}
