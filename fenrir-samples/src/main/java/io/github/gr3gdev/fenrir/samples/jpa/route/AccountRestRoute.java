package io.github.gr3gdev.fenrir.samples.jpa.route;

import io.github.gr3gdev.fenrir.annotation.Body;
import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Param;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.http.HttpStatus;
import io.github.gr3gdev.fenrir.plugin.impl.JsonPlugin;
import io.github.gr3gdev.fenrir.samples.jpa.bean.Account;
import io.github.gr3gdev.fenrir.samples.jpa.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Route(plugin = JsonPlugin.class, path = "/account")
@RequiredArgsConstructor
public class AccountRestRoute {

    private final AccountRepository repository;

    @Listener(path = "/")
    public List<Account> findAll() {
        return repository.findAll();
    }

    @Listener(path = "/{accountType}/{accountNumber}")
    public Optional<Account> findById(@Param("accountType") String accountNumber, @Param("accountNumber") String accountType) {
        return repository.findById(new Account.AccountId(accountNumber, accountType));
    }

    @Listener(path = "/", method = HttpMethod.POST, responseCode = HttpStatus.CREATED)
    public Account create(@Body Account account) {
        return repository.save(account);
    }

    @Listener(path = "/", method = HttpMethod.PUT)
    public Account update(@Body Account account) {
        return repository.save(account);
    }

    @Listener(path = "/", method = HttpMethod.DELETE)
    public void delete(@Body Account account) {
        repository.delete(account);
    }

    @Listener(path = "/{accountType}/{accountNumber}", method = HttpMethod.DELETE)
    public void deleteById(@Param("accountType") String accountNumber, @Param("accountNumber") String accountType) {
        repository.deleteById(new Account.AccountId(accountNumber, accountType));
    }
}
