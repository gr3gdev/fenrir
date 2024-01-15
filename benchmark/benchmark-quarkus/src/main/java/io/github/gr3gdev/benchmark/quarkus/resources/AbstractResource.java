package io.github.gr3gdev.benchmark.quarkus.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public class AbstractResource<E> {
    protected final JpaRepository<E, Long> repository;

    protected AbstractResource(JpaRepository<E, Long> repository) {
        this.repository = repository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<E> findAll() {
        return repository.findAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<E> findById(@PathParam("id") Long id) {
        return repository.findById(id);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public E create(E bean) {
        return repository.save(bean);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public E update(E bean) {
        return repository.save(bean);
    }

    @DELETE
    public void delete(E bean) {
        repository.delete(bean);
    }

    @DELETE
    @Path("/{id}")
    public void deleteById(@PathParam("id") Long id) {
        repository.deleteById(id);
    }
}
