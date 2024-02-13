package io.github.gr3gdev.sample.route;

import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Param;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.json.plugin.JsonPlugin;
import io.github.gr3gdev.sample.bean.Book;
import io.github.gr3gdev.sample.repository.BookRepository;

import java.util.Optional;

@Route(plugin = JsonPlugin.class, path = "/book")
public class BookRestRoute extends AbstractRest<Book, Book.BookId> {

    public BookRestRoute(BookRepository repository) {
        super(repository);
    }

    @Listener(path = "/{title}/{language}")
    public Optional<Book> findById(@Param("title") String title, @Param("language") String language) {
        return repository.findById(new Book.BookId(title, language));
    }

    @Listener(path = "/{title}/{language}", method = HttpMethod.DELETE)
    public void deleteById(@Param("title") String title, @Param("language") String language) {
        repository.deleteById(new Book.BookId(title, language));
    }
}
