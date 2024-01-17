package io.github.gr3gdev.fenrir.samples.jpa.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

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

    @AllArgsConstructor
    @Data
    public static class AccountId implements Serializable {
        private String accountNumber;
        private String accountType;
    }
}
