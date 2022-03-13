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
    public double getInitialBal() {return initialBal;}

    //returns current balance - note that for now the actual balance of account does not change - it is all done artificially
    public double getCurrentBal() {
        double currentBal = initialBal; //start at initial balance of account
        for(Transaction trns : transactions) { //attempts to do all transactions in list with the appropriate checks
            if (account.getID().equals(trns.getWidAcc().getID()) && currentBal - trns.getAmount() >= 0) {
                currentBal -= trns.getAmount();
                if (trns.getStatus() == 0) {
                    numSuccessful++;
                    trns.setStatus(1);
                }
            } else if (trns.getWidAcc().getBalance() - trns.getAmount() >= 0 || !trns.getWidAcc().getLocal()) {
                currentBal += trns.getAmount();
                if (trns.getStatus() == 0) {
                    numSuccessful++;
                    trns.setStatus(1);
                }
            }
            else numFailed++;
        }
        return Math.round(currentBal*100.00)/100.00;
    }

    //getter for numFailed
    public int getNumFailed() {
        return numFailed;
    }

    //getter for numSuccessful
    public int getNumSuccessful() {
        return numSuccessful;
    }
}

