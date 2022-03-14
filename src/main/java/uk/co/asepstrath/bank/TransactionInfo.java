package uk.co.asepstrath.bank;

import java.util.ArrayList;
import java.util.Collections;

public class TransactionInfo {
    private final double initialBal;                  //initial balance before any transactions are done
    private final Account account;                    //account we want the transaction info for (note that only local accounts can have transactionInfo objects
    private ArrayList<Transaction> transactions;      //list of all transactions associated with account
    private int numSuccessful;                        //number of successful transactions
    private int numFailed;                            //number of failed transactions

    //main constructor
    public TransactionInfo(Account account, ArrayList<Transaction> transactions){
        this.account = account;
        this.transactions = transactions;
        Collections.sort(this.transactions);
        this.initialBal = account.getBalance();
    }

    //getter for account
    public Account getAccount() {return account;}

    //returns all transactions
    public ArrayList<Transaction> getTransactions() {return transactions;}

    //returns the initial balance
    public double getInitialBal() {return account.getInitialBal();}

    //returns current balance
    public double getCurrentBal() {
        for (Transaction t : transactions){
            t.doTransaction();
        }
        return account.getBalance();
    }

    //getter for numFailed
    public int getNumFailed() {
        numFailed = 0;
        for (Transaction t : transactions){
            if (t.getStatus() == -1){
                numFailed++;
            }
        }
        return numFailed;
    }

    //getter for numSuccessful
    public int getNumSuccessful() {
        numSuccessful = 0;
        for (Transaction t : transactions) {
            if (t.getStatus() == 1) {
                numSuccessful++;
            }
        }
        return numSuccessful;
    }
}

