package lt.fintech.api.domain.services;

import lombok.AllArgsConstructor;
import lt.fintech.api.adapter.ResponseCode;
import lt.fintech.api.adapter.StandardResponse;
import lt.fintech.api.domain.models.Account;
import lt.fintech.api.domain.models.Transfer;
import lt.fintech.api.domain.models.TransferRepository;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@AllArgsConstructor
public class TransferService {
    private final TransferRepository transferRepository;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public void create(Transfer newTransfer) {
        this.transferRepository.create(newTransfer);
    }

    public StandardResponse<Transfer> transfer(Transfer transfer, AccountService accountService) {
        this.create(transfer);
        ResponseCode rc = ResponseCode.OK;
        String errorMessage = "";

        lock.writeLock().lock();
        try {
            Account accountFrom = accountService.find(transfer.getAccountFrom());
            Account accountTo = accountService.find(transfer.getAccountTo());

            if (accountFrom != null && accountTo != null) {
                if (accountService.canDeposit(accountFrom, transfer.getAmount())) {
                    accountService.deposit(accountFrom, transfer.getAmount());
                    accountService.credit(accountTo, transfer.getAmount());
                } else {
                    rc = ResponseCode.BAD_REQUEST;
                    errorMessage = "Not enough money in the account";
                }
            } else {
                rc = ResponseCode.BAD_REQUEST;
                errorMessage = "Account does not exist";
            }
        } finally {
            lock.writeLock().unlock();
        }

        return new StandardResponse<>(transfer, rc, errorMessage);
    }

}
