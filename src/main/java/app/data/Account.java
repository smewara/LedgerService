package app.data;

public class Account {
    public int AccountId;
    public double Amount;

    public Account(int accountId, double amount) {
        AccountId = accountId;
        Amount = amount;
    }

    public double getAmount() {
        return Amount;
    }

    public int getAccountId() {
        return AccountId;
    }
}
