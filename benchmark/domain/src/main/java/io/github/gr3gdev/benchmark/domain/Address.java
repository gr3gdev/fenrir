package io.github.gr3gdev.benchmark.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "address")
@Data
public class Address {
    @Id
    @Column(name = "address_id")
    private Long id;
    @Column(name = "name")
    private String name;
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
}
