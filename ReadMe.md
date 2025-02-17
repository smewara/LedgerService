# LedgerService API Documentation

## Introduction
LedgerService is a simple API that allows you to create an account, check balance, add transaction, and retrieve transaction history. Below are the available endpoints with detailed descriptions of their functionality.

## Endpoints

### POST: Create Account
Create a new account with an initial balance.

**URL:**  
`/accounts`

**Query Parameters:**
- `accountId` (required): The unique identifier for the account.
- `balance` (required): The initial balance for the account.

**Example Request:**
```
POST http://localhost:8080/accounts?accountId=1&balance=10
```
### GET: Check Balance

This endpoint allows you to retrieve the current balance of a specific account using its unique account ID.

**URL:**  
`/accounts/{accountId}/balance`

**Example Request:**
```
GET http://localhost:8080/accounts/1/balance
```
### GET: Get Transactions

This endpoint allows you to retrieve the transactions of a specific account between specified dates.

**URL:**  
`/accounts/{accountId}/transactions?startDate=&endDate=`

**Example Request:**
```
GET http://localhost:8080/accounts/1/transactions?startDate=2025-01-15&endDate=2025-02-17```
```

### POST: Add Transaction

This endpoint allows you to add a new transaction to an account. Transactions can either be a **deposit** or a **withdrawal** based on the request details.

**URL:**
`/accounts/{accountId}/transactions`

**Example Request Body:**

```json
{
  "accountId": 1,
  "transactionType": "Deposit",
  "amount": 100.02
}
