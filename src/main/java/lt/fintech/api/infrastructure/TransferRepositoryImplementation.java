package lt.fintech.api.infrastructure;

import lt.fintech.api.domain.models.Transfer;
import lt.fintech.api.domain.models.TransferRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransferRepositoryImplementation implements TransferRepository {
    private static final Map<String, Transfer> TRANSACTION_REPOSITORY = new ConcurrentHashMap<>();

    @Override
    public void create(Transfer newTransfer) {
        TRANSACTION_REPOSITORY.putIfAbsent(newTransfer.getId(), newTransfer);
    }
}
