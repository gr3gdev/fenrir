package io.github.gr3gdev.fenrir.thymeleaf.validation;

import jakarta.validation.ConstraintViolation;

import java.util.Set;

/**
 * Result for validation.
 */
public record ValidationResult(Set<ConstraintViolation<Object>> violations) {
}
