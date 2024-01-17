package io.github.gr3gdev.benchmark.fenrir.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Country {
    @Id
    private Long id;
    @Column
    private String name;
}
