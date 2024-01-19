package io.github.gr3gdev.fenrir.samples.jpa.bean;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Data
public class Animal {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private ZonedDateTime added;
    @OneToMany
    private List<Dog> dogs;
}
