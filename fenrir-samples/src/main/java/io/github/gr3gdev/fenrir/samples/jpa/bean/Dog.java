package io.github.gr3gdev.fenrir.samples.jpa.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Dog {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String name;
}
