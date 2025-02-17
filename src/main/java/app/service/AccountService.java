package app.service;

import app.data.Transaction;
import app.data.TransactionRequest;
import app.store.AccountDataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for managing account-related operations, such as creating new accounts.
 */
@Service
public class AccountService {
    private final AccountDataStore accountDataStore;

    @Autowired
    public AccountService(AccountDataStore accountDataStore) {
        this.accountDataStore = accountDataStore;
    }

    /**
     * Creates a new account with the specified account ID and initial balance.
     *
     * @param accountId the unique identifier for the account to be created
     * @param balance the initial balance for the account
     * @return true if the account was successfully created, false if there was an error
     */
    public boolean createAccount(int accountId, double balance){
        return accountDataStore.createNewAccount(accountId, balance);
    }


    /**
     * Retrieves the current balance of the specified account.
     *
     * @param accountId the unique identifier of the account whose balance is being requested
     * @return the current balance of the account as a double
     * @throws IllegalArgumentException if the account with the specified account ID does not exist
     */
    public double getCurrentBalance(int accountId) {
        return accountDataStore.getAccountBalanceByAccountId(accountId);
    }

    /**
     * Retrieves a list of transactions for a specific account that fall within a specified date range.
     * The transactions are sorted in descending order by transaction date (most recent first).
     *
     * @param accountId the unique identifier of the account whose transactions are to be retrieved
     * @param startDate the start date of the range within which transactions should fall (inclusive)
     * @param endDate the end date of the range within which transactions should fall (inclusive)
     * @return a list of Transactions that match the criteria, sorted by date in descending order
     */
    public List<Transaction> getTransactions(int accountId, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactionsWithinDateRange = new ArrayList<>();

        for (Transaction transaction : accountDataStore.getTransactionsByAccountId(accountId)) {
            /// add transactions that are within the provided date range
            if (isTransactionWithinDateRange(transaction, startDate, endDate)) {
                transactionsWithinDateRange.add(transaction);
            }
        }
        /// Sort transactions by date in descending order (most recent first)
        transactionsWithinDateRange.sort((t1,t2)->t2.getTransactionDate().compareTo(t1.getTransactionDate()));
        return transactionsWithinDateRange;
    }

    public boolean addTransaction(TransactionRequest transactionRequest) {
        return accountDataStore.addAccountTransaction(transactionRequest);
    }

    private boolean isTransactionWithinDateRange(Transaction transaction, LocalDate startDate, LocalDate endDate) {
        LocalDate transactionDate = transaction.getTransactionDate().atZone(ZoneId.systemDefault()).toLocalDate();
        return (!transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate));
    }
}
