package io.github.gr3gdev.benchmark.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "city")
@Data
public class City {
    @Id
    @Column(name = "city_id")
    private Long id;
    @Column(name = "name")
    private String name;
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
}
