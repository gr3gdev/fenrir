package io.github.gr3gdev.common.thymeleaf;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Locale;

@Entity(name = "prefs")
@Data
public class Prefs {
    @Id
    @Column(name = "pref_id")
    private Long id;
    @Column(name = "locale")
    private Locale locale;
}
