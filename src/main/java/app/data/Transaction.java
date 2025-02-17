package app.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class Transaction {
    private int AccountId;
    private LocalDateTime TransactionDate;
    private TransactionType TransactionType;
    private double Amount;

    public Transaction(){}

    @JsonCreator
    public Transaction(@JsonProperty("accountId") int accountId,
                       @JsonProperty("transactionDate") LocalDateTime transactionDate,
                       @JsonProperty("transactionType") TransactionType transactionType,
                       @JsonProperty("amount") double amount) {
        AccountId = accountId;
        TransactionDate = transactionDate;
        TransactionType = transactionType;
        Amount = amount;
    }

    @JsonProperty(value = "accountId", index = 1)
    public int getAccountId() {
        return AccountId;
    }

    @JsonProperty(value = "transactionDate", index = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getTransactionDate() {
        return TransactionDate;
    }

    @JsonProperty(value = "transactionType", index = 3)
    public TransactionType getTransactionType() {
        return TransactionType;
    }

    @JsonProperty(value = "amount", index = 4)
    public double getAmount() {
        return Amount;
    }
}
