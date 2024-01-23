package io.github.gr3gdev.benchmark.spring.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

public abstract class AbstractController<E> {
    protected final JpaRepository<E, Long> repository;

    public AbstractController(JpaRepository<E, Long> repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public List<E> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<E> findById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @PostMapping("/")
    public E create(@RequestBody E bean) {
        return repository.save(bean);
    }

    @PutMapping("/")
    public E update(@RequestBody E bean) {
        return repository.save(bean);
    }

    @DeleteMapping("/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody E bean) {
        repository.delete(bean);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
