package lt.fintech.api.infrastructure;

import lt.fintech.api.domain.models.Account;
import lt.fintech.api.domain.models.AccountRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountRepositoryImplementation implements AccountRepository {
    private static final Map<String, Account> ACCOUNTS_REPOSITORY = new ConcurrentHashMap<>();

    @Override
    public void create(Account newAccount) {
        ACCOUNTS_REPOSITORY.putIfAbsent(newAccount.getAccountNumber(), newAccount);
    }

    @Override
    public Account find(String accountId) {
        return ACCOUNTS_REPOSITORY.get(accountId);
    }

    @Override
    public void reset() {
        synchronized (ACCOUNTS_REPOSITORY) {
            ACCOUNTS_REPOSITORY.clear();
        }
    }
}