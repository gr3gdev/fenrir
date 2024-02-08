package io.github.gr3gdev.sample.hal.rest;

import io.github.gr3gdev.sample.hal.bean.Book;
import io.github.gr3gdev.sample.hal.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookRestController {

    private final BookRepository repository;

    @GetMapping("/")
    public List<Book> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Book> findById(@PathVariable("id") Long id) {
        return repository.findById(id);
    }

    @PostMapping("/")
    public Book findAll(@RequestBody Book book) {
        return repository.save(book);
    }
}
