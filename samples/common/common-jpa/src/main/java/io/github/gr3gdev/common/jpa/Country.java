package io.github.gr3gdev.common.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name = "country")
@Data
public class Country {
    @Id
    @Column(name = "country_id")
    private Long id;
    @Column(name = "name")
    private String name;
}
