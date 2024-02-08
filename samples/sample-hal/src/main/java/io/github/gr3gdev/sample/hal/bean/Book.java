package io.github.gr3gdev.sample.hal.bean;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue
    private long id;

    @Column(length = 100)
    private String title;

    @Column(length = 100)
    private String author;

    @Column(length = 1000)
    private String blurb;

    private int pages;
}
