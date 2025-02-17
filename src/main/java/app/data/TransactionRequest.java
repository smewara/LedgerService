package app.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionRequest {
    public int AccountId;

    public double Amount;

    public TransactionType TransactionType;

    public TransactionRequest(){}

    @JsonCreator
    public TransactionRequest(@JsonProperty("accountId") int accountId,
                              @JsonProperty("transactionType") TransactionType transactionType,
                              @JsonProperty("amount") double amount) {
        AccountId = accountId;
        TransactionType = transactionType;
        Amount = amount;
    }

    public TransactionType getTransactionType() {
        return TransactionType;
    }

    public double getAmount() {
        return Amount;
    }

    public int getAccountId() {
        return AccountId;
    }

}
