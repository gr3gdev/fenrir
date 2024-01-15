package io.github.gr3gdev.benchmark.spring.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Address {
    @Id
    private Long id;
    @Column
    private String name;
    @ManyToOne
    private City city;
}
