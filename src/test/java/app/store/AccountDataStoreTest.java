package app.store;

import app.data.TransactionRequest;
import app.data.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AccountDataStoreTest {

    @Test
    public void testAddAccountTransaction_Deposit_Success() {
        AccountDataStore accountDataStore = new AccountDataStore();
        int accountId = 1;
        double initialBalance = 100.0;

        accountDataStore.createNewAccount(accountId, initialBalance);

        TransactionRequest transactionRequest = new TransactionRequest(
                accountId,
                TransactionType.Deposit,
                50.0
        );

        boolean result = accountDataStore.addAccountTransaction(transactionRequest);

        Assertions.assertTrue(result);
        Assertions.assertEquals(150.0, accountDataStore.getAccountBalanceByAccountId(accountId));
    }

    @Test
    public void testCreateNewAccount_Success() {
        AccountDataStore accountDataStore = new AccountDataStore();
        int accountId = 10;
        double initialBalance = 500.0;

        boolean result = accountDataStore.createNewAccount(accountId, initialBalance);

        Assertions.assertTrue(result);
        Assertions.assertEquals(500.0, accountDataStore.getAccountBalanceByAccountId(accountId));
    }

    @Test
    public void testCreateNewAccount_AccountAlreadyExists() {
        AccountDataStore accountDataStore = new AccountDataStore();
        int accountId = 11;
        double initialBalance = 300.0;

        accountDataStore.createNewAccount(accountId, initialBalance);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            accountDataStore.createNewAccount(accountId, 200.0);
        });
    }

    @Test
    public void testAddAccountTransaction_Withdrawal_Success() {
        AccountDataStore accountDataStore = new AccountDataStore();
        int accountId = 2;
        double initialBalance = 200.0;

        accountDataStore.createNewAccount(accountId, initialBalance);

        TransactionRequest transactionRequest = new TransactionRequest(
                accountId,
                TransactionType.Withdrawal,
                -50.0
        );

        boolean result = accountDataStore.addAccountTransaction(transactionRequest);

        Assertions.assertTrue(result);
        Assertions.assertEquals(150.0, accountDataStore.getAccountBalanceByAccountId(accountId));
    }

    @Test
    public void testAddAccountTransaction_Deposit_InvalidAmount() {
        AccountDataStore accountDataStore = new AccountDataStore();
        int accountId = 3;
        double initialBalance = 100.0;

        accountDataStore.createNewAccount(accountId, initialBalance);

        TransactionRequest transactionRequest = new TransactionRequest(
                accountId,
                TransactionType.Deposit,
                -50.0
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            accountDataStore.addAccountTransaction(transactionRequest);
        });
        Assertions.assertEquals(100.0, accountDataStore.getAccountBalanceByAccountId(accountId));
    }

    @Test
    public void testAddAccountTransaction_Withdrawal_InsufficientFunds() {
        AccountDataStore accountDataStore = new AccountDataStore();
        int accountId = 4;
        double initialBalance = 50.0;

        accountDataStore.createNewAccount(accountId, initialBalance);

        TransactionRequest transactionRequest = new TransactionRequest(
                accountId,
                TransactionType.Withdrawal,
                -100.0
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            accountDataStore.addAccountTransaction(transactionRequest);
        });
        Assertions.assertEquals(50.0, accountDataStore.getAccountBalanceByAccountId(accountId));
    }

    @Test
    public void testAddAccountTransaction_InvalidTransactionType() {
        AccountDataStore accountDataStore = new AccountDataStore();
        int accountId = 5;
        double initialBalance = 100.0;

        accountDataStore.createNewAccount(accountId, initialBalance);

        TransactionRequest transactionRequest = new TransactionRequest(
                accountId,
                null,
                50.0
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            accountDataStore.addAccountTransaction(transactionRequest);
        });
        Assertions.assertEquals(100.0, accountDataStore.getAccountBalanceByAccountId(accountId));
    }

    @Test
    public void testAddAccountTransaction_AccountNotFound() {
        AccountDataStore accountDataStore = new AccountDataStore();

        TransactionRequest transactionRequest = new TransactionRequest(
                999,
                TransactionType.Deposit,
                50.0
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            accountDataStore.addAccountTransaction(transactionRequest);
        });
    }
}