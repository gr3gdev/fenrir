package io.github.gr3gdev.sample.route;

import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Param;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.plugin.JsonPlugin;
import io.github.gr3gdev.sample.bean.Account;
import io.github.gr3gdev.sample.repository.AccountRepository;

import java.util.Optional;

@Route(plugin = JsonPlugin.class, path = "/account")
public class AccountRestRoute extends AbstractRest<Account, Account.AccountId> {

    public AccountRestRoute(AccountRepository repository) {
        super(repository);
    }

    @Listener(path = "/{accountType}/{accountNumber}")
    public Optional<Account> findById(@Param("accountType") String accountNumber, @Param("accountNumber") String accountType) {
        return repository.findById(new Account.AccountId(accountNumber, accountType));
    }

    @Listener(path = "/{accountType}/{accountNumber}", method = HttpMethod.DELETE)
    public void deleteById(@Param("accountType") String accountNumber, @Param("accountNumber") String accountType) {
        repository.deleteById(new Account.AccountId(accountNumber, accountType));
    }
}
