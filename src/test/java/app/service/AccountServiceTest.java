package app.service;

import app.data.Transaction;
import app.data.TransactionType;
import app.store.AccountDataStore;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;

@SpringBootTest
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @MockitoBean
    private AccountDataStore accountDataStore;

    @Test
    void testGetTransactions_WithinDateRange_SortedByDateDescending() {
        Transaction transaction1 = new Transaction(1, LocalDateTime.of(2023, 9, 10, 10, 0), TransactionType.Deposit, 100.0);
        Transaction transaction2 = new Transaction(1, LocalDateTime.of(2023, 9, 12, 12, 0), TransactionType.Deposit, 200.0);
        Transaction transaction3 = new Transaction(1, LocalDateTime.of(2023, 9, 11, 11, 0), TransactionType.Deposit, 150.0);

        Mockito.when(accountDataStore.getTransactionsByAccountId(1)).thenReturn(Arrays.asList(transaction1, transaction2, transaction3));

        LocalDate startDate = LocalDate.of(2023, 9, 10);
        LocalDate endDate = LocalDate.of(2023, 9, 12);
        List<Transaction> transactions = accountService.getTransactions(1, startDate, endDate);

        assertEquals(3, transactions.size());
        assertEquals(transaction2, transactions.get(0));
        assertEquals(transaction3, transactions.get(1));
        assertEquals(transaction1, transactions.get(2));
    }

    @Test
    void testGetTransactions_NoTransactionsFound() {
        Mockito.when(accountDataStore.getTransactionsByAccountId(anyInt())).thenReturn(Collections.emptyList());

        LocalDate startDate = LocalDate.of(2023, 9, 10);
        LocalDate endDate = LocalDate.of(2023, 9, 12);
        List<Transaction> transactions = accountService.getTransactions(1, startDate, endDate);

        assertEquals(0, transactions.size());
    }

    @Test
    void testGetTransactions_TransactionsOutsideDateRangeExcluded() {
        Transaction transaction1 = new Transaction(1, LocalDateTime.of(2023, 9, 8, 10, 0), TransactionType.Deposit, 100.0);
        Transaction transaction2 = new Transaction(1, LocalDateTime.of(2023, 9, 12, 10, 0), TransactionType.Deposit, 200.0);
        Transaction transaction3 = new Transaction(1, LocalDateTime.of(2023, 9, 14, 10, 0), TransactionType.Withdrawal, -150.0);

        Mockito.when(accountDataStore.getTransactionsByAccountId(1)).thenReturn(Arrays.asList(transaction1, transaction2, transaction3));

        LocalDate startDate = LocalDate.of(2023, 9, 10);
        LocalDate endDate = LocalDate.of(2023, 9, 13);
        List<Transaction> transactions = accountService.getTransactions(1, startDate, endDate);

        assertEquals(1, transactions.size());
        assertEquals(transaction2, transactions.get(0));
    }

    @Test
    void testGetTransactions_AllTransactionsWithinRange() {
        Transaction transaction1 = new Transaction(1, LocalDateTime.of(2023, 9, 10, 10, 0), TransactionType.Deposit, 100.0);
        Transaction transaction2 = new Transaction(1, LocalDateTime.of(2023, 9, 11, 12, 0), TransactionType.Deposit, 200.0);

        Mockito.when(accountDataStore.getTransactionsByAccountId(1)).thenReturn(Arrays.asList(transaction1, transaction2));

        LocalDate startDate = LocalDate.of(2023, 9, 10);
        LocalDate endDate = LocalDate.of(2023, 9, 11);
        List<Transaction> transactions = accountService.getTransactions(1, startDate, endDate);

        assertEquals(2, transactions.size());
        assertEquals(transaction2, transactions.get(0));
        assertEquals(transaction1, transactions.get(1));
    }
}