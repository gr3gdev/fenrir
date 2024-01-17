package io.github.gr3gdev.benchmark.fenrir.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.Set;

@Entity
@Data
public class Person {
    @Id
    private Long id;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @OneToMany
    private Set<Address> addresses;
}
