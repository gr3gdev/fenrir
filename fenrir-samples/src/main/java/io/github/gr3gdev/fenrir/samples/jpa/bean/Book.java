package io.github.gr3gdev.fenrir.samples.jpa.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class Book {
    @EmbeddedId
    private BookId bookId;
    @Column
    private String author;

    @Embeddable
    @Data
    @AllArgsConstructor
    public static class BookId implements Serializable {
        private String title;
        private String language;
    }
}
