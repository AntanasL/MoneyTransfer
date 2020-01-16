package lt.fintech.api.domain.sercvices;

import lt.fintech.api.domain.models.Account;
import lt.fintech.api.domain.models.Transfer;
import lt.fintech.api.domain.services.AccountService;
import lt.fintech.api.domain.services.TransferService;
import lt.fintech.api.infrastructure.AccountRepositoryImplementation;
import lt.fintech.api.infrastructure.TransferRepositoryImplementation;
import org.junit.jupiter.api.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransferServiceTest {
    private final AccountService accountService = new AccountService(new AccountRepositoryImplementation());
    private final TransferService transferService = new TransferService(new TransferRepositoryImplementation());

    private final float INITIAL_AMOUNT = 1000f;

    @BeforeEach
    void setUp() {
        addAccount("1", INITIAL_AMOUNT);
        addAccount("2", 0f);
    }

    @AfterEach
    void tearDown() {
        accountService.reset();
    }

    private void addAccount(String accountNumber, float amount) {
        accountService.create(new Account(accountNumber, amount));
    }

    private Account getAccount(String accountNumber) {
        return accountService.find(accountNumber);
    }

    private void executeTransfer(String id, String accountFrom, String accountTo, float amount) {
        transferService.transfer(new Transfer(id, accountFrom, accountTo, amount), accountService);
    }

    @Test
    public void givenAccountDoesNotExists_whenAccountInfoIsRetrieved_thenNullIsReceived() {
        Account account = getAccount("000");
        assertNull(account);
    }

    @Test
    public void givenAccountDoesExist_whenAccountInfoIsRetrieved_thenAccountIsFound() {
        Account account = getAccount("1");
        assertEquals(account.getAccountNumber(), "1");
    }

    @Test
    public void givenSimpleTransfer_whenAccountInfoIsRetrieved_thenAccountAmountsCorrect() {
        executeTransfer("1", "1", "2", 100f);
        executeTransfer("2", "2", "1", 50f);
        Account account1 = getAccount("1");
        Account account2 = getAccount("2");

        assertEquals(INITIAL_AMOUNT - 50f, account1.getAmount());
        assertEquals(50f, account2.getAmount());
    }

    @Test
    public void givenConcurrentTransfer_whenAccountInfoIsRetrieved_thenAccountAmountsCorrect() throws InterruptedException {
        int numberOfThreads = 1000;

        Runnable transferOne = () -> {
            executeTransfer(Long.toString(Thread.currentThread().getId()), "1", "2", 1f);
        };

        Runnable transferHalf = () -> {
            executeTransfer(Long.toString(Thread.currentThread().getId()), "2", "1", .5f);
        };

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        IntStream.range(1, numberOfThreads + 1).forEach(i -> {
            executorService.submit(transferOne);
            executorService.submit(transferHalf);
        });
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        Account account1 = getAccount("1");
        Account account2 = getAccount("2");
        assertEquals((float) (INITIAL_AMOUNT - numberOfThreads + numberOfThreads / 2), account1.getAmount());
        assertEquals((float) numberOfThreads / 2, account2.getAmount());
    }

    @Test
    public void givenNonSufficientAmount_whenAccountInfoIsRetrieved_thenAccountAmountsZero() throws InterruptedException {
        int numberOfThreads = 10;

        Runnable transferOne = () -> {
            executeTransfer(Long.toString(Thread.currentThread().getId()), "1", "2", INITIAL_AMOUNT-10);
        };

        ExecutorService executorService = Executors.newCachedThreadPool();
        IntStream.range(1, numberOfThreads + 1).forEach(i -> {
            executorService.submit(transferOne);
        });
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        Account account1 = getAccount("1");
        Account account2 = getAccount("2");
        assertEquals((float) 10, account1.getAmount());
        assertEquals((float) INITIAL_AMOUNT-10, account2.getAmount());
    }

    @Test
    public void givenAccountToNotExistent_whenAccountInfoIsRetrieved_thenAccountAmountsIsTheSame() throws InterruptedException {
        int numberOfThreads = 1000;

        Runnable transferNonExistant = () -> {
            executeTransfer(Long.toString(Thread.currentThread().getId()), "1", "000", INITIAL_AMOUNT);
        };

        ExecutorService executorService = Executors.newCachedThreadPool();
        IntStream.range(1, numberOfThreads + 1).forEach(i -> {
            executorService.submit(transferNonExistant);
        });
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        Account account1 = getAccount("1");
        assertEquals((float) INITIAL_AMOUNT, account1.getAmount());
    }
}