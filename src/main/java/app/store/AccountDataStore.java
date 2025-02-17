package app.store;

import app.data.Transaction;
import app.data.TransactionRequest;
import app.data.TransactionType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service class responsible for managing account data, including account creation,
 * transactions, and balance management. This class uses thread-safe data structures
 * to maintain multiple accounts and their associated transaction details.
 */
@Service
public class AccountDataStore {
    private static ConcurrentHashMap<Integer, CopyOnWriteArrayList<Transaction>> accountTransactionsMap;
    private static ConcurrentHashMap<Integer, DoubleAdder> accountBalance;
    private static ConcurrentHashMap<Integer, ReentrantLock> accountLocks;

    public AccountDataStore() {
        accountTransactionsMap = new ConcurrentHashMap<>();
        accountBalance = new ConcurrentHashMap<>();
        accountLocks = new ConcurrentHashMap<>();
    }

    /**
     * Creates a new account with the specified account ID and initial balance.
     *
     * @param accountId the unique identifier for the account to be created
     * @param balance the initial balance for the account
     * @return true if the account was successfully created
     * @throws IllegalArgumentException if an account with the specified account ID already exists
     */
    public boolean createNewAccount(int accountId, double balance) {
        if (accountTransactionsMap.containsKey(accountId)) {
            throw new IllegalArgumentException("Account with ID " + accountId + " already exists");
        }
        accountTransactionsMap.computeIfAbsent(accountId, id -> new CopyOnWriteArrayList<>());
        accountLocks.putIfAbsent(accountId, new ReentrantLock());
        addAccountTransaction(new TransactionRequest(accountId, TransactionType.Deposit, balance));
        return true;
    }

    /**
     * Retrieves the list of transactions associated with a specified account ID.
     *
     * @param accountId the unique identifier for the account whose transactions are to be retrieved
     * @return a list of transactions associated with the specified account ID
     * @throws IllegalArgumentException if the account with the specified account ID does not exist
     */
    public List<Transaction> getTransactionsByAccountId(int accountId) {
        if (!accountTransactionsMap.containsKey(accountId)) {
            throw new IllegalArgumentException("Account " + accountId + " does not exist.");
        }
        return accountTransactionsMap.get(accountId);
    }

    /**
     * Retrieves the account balance for the specified account ID.
     *
     * @param accountId the unique identifier for the account whose balance is to be retrieved
     * @return the current balance of the specified account
     * @throws IllegalArgumentException if the account with the specified account ID does not exist
     */
    public Double getAccountBalanceByAccountId(int accountId) {
        if (!accountBalance.containsKey(accountId)) {
            throw new IllegalArgumentException("Account " + accountId + " does not exist.");
        }
        return accountBalance.get(accountId).doubleValue();
    }

    /**
     * Processes and adds a transaction to the account specified in the transaction request.
     * Updates the account's balance and transaction history.
     * Throws an exception in case of invalid transactions or if the account does not exist.
     *
     * @param transactionRequest the request object containing details about the transaction to be performed,
     *                           including account ID, transaction type, and transaction amount
     * @return true if the transaction was successfully added to the account
     * @throws IllegalArgumentException if the account does not exist, the transaction is invalid, or if there
     *                                  are insufficient funds for a withdrawal
     */
    public boolean addAccountTransaction(TransactionRequest transactionRequest) {
        int accountId = transactionRequest.getAccountId();
        if (!accountTransactionsMap.containsKey(accountId)) {
            throw new IllegalArgumentException("Account " + accountId + " does not exist.");
        }

        ReentrantLock lock = accountLocks.get(accountId);

        lock.lock();

        try
        {
            DoubleAdder balanceAddr = accountBalance.computeIfAbsent(accountId, key->new DoubleAdder());

            if(isValidTransaction(transactionRequest, balanceAddr.doubleValue())) {
                double amount = transactionRequest.getAmount();

                Transaction transaction = new Transaction(accountId,
                        LocalDateTime.now(),
                        transactionRequest.getTransactionType(),
                        amount);

                accountTransactionsMap.computeIfPresent(accountId, (key, transactions)->
                {
                    transactions.add(transaction); return transactions;
                });

                balanceAddr.add(amount);
            }

            return true;
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * Validates a transaction request based on its type, amount, and the account's current balance.
     * For deposit transactions, ensures the amount is positive.
     * For withdrawal transactions, ensures the amount is negative and within the available balance.
     * Throws an exception for invalid transaction types or invalid amounts.
     *
     * @param transactionRequest the request object containing details about the transaction,
     *                           including transaction type and amount
     * @param balance the current balance of the account
     * @return true if the transaction request is valid
     * @throws IllegalArgumentException if the transaction type is invalid, the deposit amount is non-positive,
     *                                  or the withdrawal amount is invalid (non-negative or exceeds available balance)
     */
    private static boolean isValidTransaction(TransactionRequest transactionRequest, Double balance) {
        TransactionType transactionType = transactionRequest.getTransactionType();
        double amount = transactionRequest.getAmount();

        if (transactionType == TransactionType.Deposit) {
            if (amount <= 0){
                throw new IllegalArgumentException("Deposit amount must be positive.");
            }
        } else if (transactionType == TransactionType.Withdrawal) {
            if (amount >= 0){
                throw new IllegalArgumentException("Withdrawal amount must be negative.");
            }
            if (Math.abs(amount) > balance) {
                throw new IllegalArgumentException("Insufficient funds for withdrawal.");
            }
        } else {
            throw new IllegalArgumentException("Invalid transactionRequest type.");
        }

        return true;
    }
}
