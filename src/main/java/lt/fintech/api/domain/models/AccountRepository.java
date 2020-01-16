package lt.fintech.api.domain.models;

public interface AccountRepository {
    public void create(Account newAccount);
    public Account find(String accountId);
    public void reset();
}
