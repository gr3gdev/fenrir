package io.github.gr3gdev.sample.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Data
@IdClass(Account.AccountId.class)
public class Account {
    @Id
    private String accountNumber;
    @Id
    private String accountType;
    @Column
    private String name;
    @CreationTimestamp
    private ZonedDateTime createdDate;

    @AllArgsConstructor
    @Data
    public static class AccountId implements Serializable {
        private String accountNumber;
        private String accountType;
    }
}
