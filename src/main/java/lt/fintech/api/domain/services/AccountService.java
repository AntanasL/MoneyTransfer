package lt.fintech.api.domain.services;

import lombok.AllArgsConstructor;
import lt.fintech.api.domain.models.Account;
import lt.fintech.api.domain.models.AccountRepository;

@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public void create(Account newAccount) {
        this.accountRepository.create(newAccount);
    }

    public Account find(String accountId) {
        return this.accountRepository.find(accountId);
    }

    public boolean canDeposit(Account account, float amount) {
        return account.canDeposit(amount);
    }

    public void deposit(Account account, float amount) {
        account.deposit(amount);
    }

    public void credit(Account account, float amount) {
        account.credit(amount);
    }

    public void reset() {
        this.accountRepository.reset();
    }
}