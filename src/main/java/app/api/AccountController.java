package app.api;

import app.data.Transaction;
import app.data.TransactionRequest;
import app.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * The {@code AccountController} class is a Spring REST Controller that
 * exposes various endpoints to manage accounts and their transactions.
 * It provides functionalities like creating accounts, fetching account balance,
 * retrieving transaction history, and adding new transactions.
 */
@RestController
@RequestMapping(AccountController.BASE_URL)
public class AccountController {

    public static final String BASE_URL = "/accounts";

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Creates a new account with the specified account ID and initial balance.
     *
     * @param accountId the unique identifier for the account to be created
     * @param balance   the initial balance for the account
     * @return a ResponseEntity containing true if the account was successfully created,
     * false if the creation failed.
     */
    @PostMapping
    public ResponseEntity<Boolean> createAccount(@RequestParam("accountId") int accountId,
                                                 @RequestParam("balance") double balance) {
        try {
            return ResponseEntity.ok(accountService.createAccount(accountId, balance));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves the current balance of a specified account.
     *
     * @param accountId the ID of the account for which the balance is to be retrieved
     * @return a ResponseEntity containing the current balance if successful,
     * or a ResponseEntity with HTTP 400 (Bad Request) and an error message if the account ID is invalid
     */
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<?> getCurrentBalance(@PathVariable("accountId") int accountId) {
        try {
            return ResponseEntity.ok(accountService.getCurrentBalance(accountId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Error:" + ex.getMessage());
        }
    }

    /**
     * Retrieves a list of transactions for a specified account within the given date range.
     *
     * @param accountId the ID of the account for which transactions are to be retrieved
     * @param startDate the start date of the date range (inclusive)
     * @param endDate   the end date of the date range (inclusive)
     * @return a ResponseEntity containing a list of transactions if successful,
     * or a ResponseEntity with HTTP 400 (Bad Request) if the start date
     * is after the end date
     */
    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(
            @PathVariable("accountId") int accountId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(accountService.getTransactions(accountId, startDate, endDate));
    }

    /**
     * Creates a new transaction based on the details provided in the request body.
     *
     * @param transactionRequest the request object containing details of the transaction such as
     *                           account ID, transaction amount, and transaction type
     * @return a ResponseEntity indicating the outcome of the request:
     * - 201 Created if the transaction is successfully added
     * - 400 Bad Request if the transaction details are invalid or if an error occurs
     */
    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<String> addTransaction(
            @PathVariable("accountId") int accountId,
            @RequestBody TransactionRequest transactionRequest) {
        try {
            // Since transactionRequest already contains accountId, no need to manually set it
            boolean isSuccess = accountService.addTransaction(transactionRequest);
            if (isSuccess) {
                return ResponseEntity.status(201).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Error:" + ex.getMessage());
        }
    }
}
