package io.github.gr3gdev.fenrir.samples.jpa.repository;

import io.github.gr3gdev.fenrir.jpa.JpaRepository;
import io.github.gr3gdev.fenrir.samples.jpa.bean.Account;

public class AccountRepository implements JpaRepository<Account, Account.AccountId> {
    @Override
    public Class<Account> getDomainClass() {
        return Account.class;
    }

    @Override
    public Class<Account.AccountId> getIdClass() {
        return Account.AccountId.class;
    }
}
