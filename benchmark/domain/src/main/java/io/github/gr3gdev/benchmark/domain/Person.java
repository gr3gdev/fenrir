package io.github.gr3gdev.benchmark.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity(name = "person")
@Data
public class Person {
    @Id
    @Column(name = "person_id")
    private Long id;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    @OneToMany(mappedBy = "id")
    private Set<Address> addresses;
}
